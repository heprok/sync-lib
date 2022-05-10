package com.briolink.lib.sync.enumeration

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

enum class ObjectSyncEnum(val value: Int) {
    @JsonProperty("1")
    Company(0),
    @JsonProperty("2")
    CompanyIndustry(1),
    @JsonProperty("3")
    CompanyOccupation(2),
    @JsonProperty("4")
    CompanyService(3),
    @JsonProperty("5")
    CompanyKeyword(4),
    @JsonProperty("6")
    UserKeyword(5),
    @JsonProperty("7")
    User(6),
    @JsonProperty("8")
    UserEducation(7),

    @JsonProperty("9")
    University(8),

    @JsonProperty("10")
    UserJobPosition(9),

    @JsonProperty("11")
    Connection(10),

    @JsonProperty("12")
    ConnectionCompanyRole(11),

    @JsonProperty("13")
    ExpVerification(12),

    @JsonProperty("14")
    Project(13);

    companion object {
        private val map = values().associateBy(ObjectSyncEnum::value)

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun fromInt(type: Int): ObjectSyncEnum = map[type]!!
    }
}
