package dev.rishon.sync.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import dev.rishon.sync.utils.LoggerUtil
import java.util.*

@JsonSerialize
class ServerData {

    @JsonProperty("instanceID")
    var instanceID: String? = null

    @JsonProperty("instanceFormatted")
    var instanceFormatted: String? = null

    @JsonProperty("ip")
    var serverIP: String? = null

    @JsonProperty("port")
    var serverPort: Int? = null

    @JsonProperty("onlinePlayers")
    var onlinePlayers: MutableSet<UUID> = mutableSetOf()

    override fun toString(): String {
        try {
            return objectMapper.writeValueAsString(this)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private val objectMapper: ObjectMapper =
            ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        @JvmStatic
        fun fromJson(data: String?): ServerData? {
            var result = data
            if (result == null || result.trim { it <= ' ' }.isEmpty()) return null
            try {
                result = result.replace("\n", "\\n").replace("\r", "\\r")
                return objectMapper.readValue(result, ServerData::class.java)
            } catch (e: JsonProcessingException) {
                LoggerUtil.error("An error occurred while deserializing serverData.")
                return null
            }
        }
    }
}