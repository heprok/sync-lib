package com.briolink.lib.sync.enumeration

import com.fasterxml.jackson.annotation.JsonProperty

enum class ServiceEnum(val id: Int) {
    @JsonProperty("1")
    User(1),

    @JsonProperty("3")
    CompanyService(3),

    @JsonProperty("4")
    Connection(4),

    @JsonProperty("2")
    Company(2);

    companion object {
        private val map = values().associateBy(ServiceEnum::id)
        fun ofId(id: Int): ServiceEnum = map[id]!!
    }
}
