package com.briolink.lib.sync.enumeration

enum class ObjectSyncEnum(val value: Int) {
    Company(0),
    CompanyIndustry(1),
    CompanyOccupation(2),
    CompanyService(3),
    CompanyKeyword(4),
    UserKeyword(5),
    User(6),
    UserEducation(7),
    University(8),
    UserJobPosition(9),
    Connection(10),
    ConnectionCompanyRole(11);

    companion object {
        private val map = values().associateBy(ObjectSyncEnum::value)
        fun fromInt(type: Int): ObjectSyncEnum = map[type]!!
    }
}
