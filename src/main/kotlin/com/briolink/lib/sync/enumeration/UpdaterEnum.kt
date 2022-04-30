package com.briolink.lib.sync.enumeration

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

enum class UpdaterEnum(val id: Int) {
    @JsonProperty("1")
    User(1),

    @JsonProperty("2")
    Company(2),

    @JsonProperty("3")
    CompanyService(3),

    @JsonProperty("4")
    Connection(4),

    @JsonProperty("5")
    Search(5),

    @JsonProperty("6")
    ExpVerification(6);

    companion object {
        private val map = values().associateBy(UpdaterEnum::id)

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun ofId(id: Int): UpdaterEnum = map[id]!!
    }
}
