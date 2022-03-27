# Sync-event library

## Installation

To connect to the project in gradle.kts connect the maven repository.

Dependent on [event lib](https://gitlab.com/briolink/network/backend/event-lib), add maven repository

```kotlin
repositories {
    setOf(
        29889174, // BL Event
        33688770, // BL Sync
    ).forEach {
        maven {
            url = uri("https://gitlab.com/api/v4/projects/$it/packages/maven")
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
            credentials(HttpHeaderCredentials::class) {
                name = "Deploy-Token"
                value = System.getenv("CI_DEPLOY_PASSWORD")
            }
        }
    }
}

dependencies {
    implementation("com.briolink.lib:sync")
}

```

In the project configuration add lines to connect sync-service

```yaml
sync-service:
  api:
    url: http://sync-service.${env.spring_profiles_active}.svc.cluster.local/
    version: 1
```

To connect to the service and send synchronization events need:

1. Add a rest controller inherited from AbstractSyncController, add post method sync
2. Override publishSyncEvent method, adding sync event sending logic.

```kotlin
@RestController
@RequestMapping("/api/v1")
class SyncController(
    private val keywordService: KeywordService,
    private val occupationService: OccupationService,
    private val industryService: IndustryService,
    private val companyService: CompanyService,
) : AbstractSyncController() {
    @PreAuthorize("@servletUtil.isIntranet()")
    @Async
    @PostMapping("sync")
    fun postSync(
        @RequestParam startLocalDateTime: String? = null,
        @RequestParam endLocalDateTime: String? = null,
        @NotNull @RequestParam syncId: Int
    ): ResponseEntity<Any> {
        return super.sync(startLocalDateTime, endLocalDateTime, syncId)
    }

    override fun publishSyncEvent(syncId: Int, period: PeriodDateTime?) {
        keywordService.publishSyncEvent(syncId, period)
        occupationService.publishSyncEvent(syncId, period)
        industryService.publishSyncEvent(syncId, period)
        companyService.publishSyncEvent(syncId, period)
    }
}
```

3. Add a SyncLog entity inherited from ISyncLogEntity and repository inherited from ISyncLogRepository

```kotlin
@Table(name = "sync_log", schema = "read")
@Entity
class SyncLogReadEntity(
    @AttributeOverrides(
        AttributeOverride(name = "syncId", column = Column(name = "sync_id")),
        AttributeOverride(name = "_objectSync", column = Column(name = "object_sync")),
        AttributeOverride(name = "_service", column = Column(name = "service")),
    )
    @EmbeddedId
    override var id: SyncLogId,

    @Column(name = "completed")
    override var completed: Instant? = null,

    @Column(name = "with_error")
    override var withError: Boolean = false

) : ISyncLogEntity {
    var objectSync: ObjectSyncEnum
        get() = ObjectSyncEnum.fromInt(id._objectSync)
        set(value) {
            id._objectSync = value.value
        }

    var service: ServiceEnum
        get() = ServiceEnum.ofId(id._service)
        set(value) {
            id._service = value.id
        }
}

interface SyncLogReadRepository : JpaRepository<SyncLogReadEntity, SyncLogId>, ISyncLogRepository<SyncLogReadEntity>

```

4. In the updater module, define a SyncService inherited from BaseSyncService. Redefine the SyncLogId list for each
   service in which updater handles Domain

```kotlin
@Transactional
@Service
class SyncService(
    override val syncWebClient: SyncWebClient,
    override val syncLogRepository: SyncLogReadRepository
) : BaseSyncService() {
    override val CURRENT_UPDATER: UpdaterEnum = UpdaterEnum.Company
    override fun getListSyncLogIdAtCompany(syncId: Int): List<SyncLogId> =
        listOf(
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Company.id
                this._objectSync = ObjectSyncEnum.Company.value
            },
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Company.id
                this._objectSync = ObjectSyncEnum.CompanyIndustry.value
            },
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Company.id
                this._objectSync = ObjectSyncEnum.CompanyOccupation.value
            },
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Company.id
                this._objectSync = ObjectSyncEnum.CompanyKeyword.value
            },
        )

    override fun getListSyncLogIdAtUser(syncId: Int): List<SyncLogId> =
        listOf(
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.User.id
                this._objectSync = ObjectSyncEnum.User.value
            },
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.User.id
                this._objectSync = ObjectSyncEnum.UserJobPosition.value
            },
        )

    override fun getListSyncLogIdAtConnection(syncId: Int): List<SyncLogId> =
        listOf(
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Connection.id
                this._objectSync = ObjectSyncEnum.Connection.value
            },
        )

    override fun getListSyncLogIdAtCompanyService(syncId: Int): List<SyncLogId> =
        listOf(
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.CompanyService.id
                this._objectSync = ObjectSyncEnum.CompanyService.value
            },
        )
}
```

## Documentation

### Basic classes

[BaseSyncService](src/main/kotlin/com/briolink/lib/sync/BaseSyncService.kt)
— Basic sync service, it determines when to send a sync-service response about a successful sync or about an error in
sync

[SyncEventHandler](src/main/kotlin/com/briolink/lib/sync/SyncEventHandler.kt)
— Sync event handler,

[AbstractSyncController](src/main/kotlin/com/briolink/lib/sync/AbstractSyncController.kt)
— Abstract class, is needed to receive a request from the sync-service, to start the synchronization of the service

[EventModels.kt](src/main/kotlin/com/briolink/lib/sync/EventModels.kt)
— Model for Sync event inherited from Event (com.briolink.lib.event.Event), information about object sync

## Examples

### publish sync events

We must synchronize the service entities, let it be company and companyIndustry.

1. Add timemarks (created and changed(if needed) and inherit BaseTimeMarkRepository

```kotlin
interface IndustryWriteRepository : JpaRepository<IndustryWriteEntity, UUID>,
    BaseTimeMarkRepository<IndustryWriteEntity> {
    @Query("SELECT c from IndustryWriteEntity c WHERE c.created BETWEEN ?1 AND ?2")
    override fun findByPeriod(start: Instant, end: Instant, pageable: Pageable): Page<IndustryWriteEntity>
}

// if the entity has created and changed columns, then there is no need to overwrite the findByPeriod method
interface CompanyWriteRepository : JpaRepository<CompanyWriteEntity, UUID>, BaseTimeMarkRepository<CompanyWriteEntity> {
}
```

2. In the service add a method to send a sync event using SyncUtil.publishSyncEvent

```kotlin
    private fun publishCompanySyncEvent(
    syncId: Int,
    objectIndex: Long,
    totalObjects: Long,
    entity: CompanyWriteEntity?
) {
    eventPublisher.publishAsync(
        CompanySyncEvent(
            SyncData(
                objectIndex = objectIndex,
                totalObjects = totalObjects,
                objectSync = entity?.toDomain(),
                syncId = syncId,
                service = ServiceEnum.Company,
            ),
        ),
    )
}

fun publishSyncEvent(syncId: Int, period: PeriodDateTime? = null) {
    SyncUtil.publishSyncEvent(period, companyWriteRepository) { indexElement, totalElements, entity ->
        publishCompanySyncEvent(
            syncId, indexElement, totalElements,
            entity as CompanyWriteEntity?,
        )
    }
}

private fun publishIndustrySyncEvent(
    syncId: Int,
    objectIndex: Long,
    totalObjects: Long,
    entity: IndustryWriteEntity?
) {
    eventPublisher.publishAsync(
        IndustrySyncEvent(
            SyncData(
                objectIndex = objectIndex,
                totalObjects = totalObjects,
                objectSync = entity?.toDomain(),
                syncId = syncId,
                service = ServiceEnum.Company,
            ),
        ),
    )
}

fun publishSyncEvent(syncId: Int, period: PeriodDateTime? = null) {
    SyncUtil.publishSyncEvent(period, industryWriteRepository) { indexElement, totalElements, entity ->
        publishIndustrySyncEvent(
            syncId, indexElement, totalElements,
            entity as IndustryWriteEntity?,
        )
    }
}


```

### Handler sync event

We need to handle sync events from each service that is connected to the sync.

**IMPORTANT:**
It is necessary to add events handler for each service, at least 1 object, because sync-service always waits for a
response from each updater about successful synchronization or errors

You need to handle a sync event from the company-service, the synchronization object will be the company.

1. Add SyncEvent

```kotlin
data class CompanySyncEvent(override val data: SyncData<CompanyEventData>) : SyncEvent<CompanyEventData>("1.0")
```

2. We need to inherit SyncEventHandler

```kotlin
@EventHandler("CompanySyncEvent", "1.0")
class CompanySyncEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val connectionHandlerService: ConnectionHandlerService,
    private val connectionServiceHandlerService: ConnectionServiceHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    syncService: SyncService,
) : SyncEventHandler<CompanySyncEvent>(ObjectSyncEnum.Company, syncService) {
    override fun handle(event: CompanySyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return // if company-service starts sync, but there were no companies during the period, SyncService makes an entry about starting sync company (see listSyncLogIdAtCompany)
        try {
            val objectSync = syncData.objectSync!!
            val company = companyHandlerService.findById(objectSync.id)
            companyHandlerService.createOrUpdate(company, objectSync).also {
                connectionHandlerService.updateCompany(it)
                connectionServiceHandlerService.updateCompany(it)
                applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(objectSync.id, false))
            }
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData) // If this is the last synchronization object, then sync-service sends a message that the updater company has completed synchronization from company-service
    }
}

```