package dev.rishon.sync.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import dev.rishon.sync.utils.InventorySerialization
import dev.rishon.sync.utils.LoggerUtil
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*

@JsonSerialize
class PlayerData {

    @JsonProperty("uuid")
    var uuid: UUID? = null

    @JsonProperty("instanceID")
    var instanceID: String? = ""

    @JsonProperty("inventory_base64")
    var inventory: String = ""

    @JsonProperty("player_location")
    var location: MutableMap<String, Any> = mutableMapOf()

    @JsonProperty("player_expPoints")
    var expPoints: Float = 0.0F

    @JsonProperty("player_expLevel")
    var expLevel: Int = 0

    @JsonProperty("player_health")
    var health: Double = 20.0

    @JsonProperty("player_maxHealth")
    var maxHealth: Double = 20.0

    @JsonProperty("player_hunger")
    var hunger: Int = 20

    @JsonProperty("player_gamemode")
    var gamemode: GameMode = GameMode.SURVIVAL

    @JsonProperty("player_effects")
    var potionEffects: List<MutableMap<String, Any>> = mutableListOf()

    fun loadInventory(player: Player, playerData: PlayerData) {
        // Load inventory
        val storedInventoryBase64 = playerData.inventory
        if (storedInventoryBase64.isEmpty()) return
        val storedInventory: Inventory = InventorySerialization.fromBase64(storedInventoryBase64, player)
        player.inventory.contents = storedInventory.contents
    }

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
        fun fromJson(data: String?): PlayerData? {
            var result = data
            if (result == null || result.trim { it <= ' ' }.isEmpty()) return null
            try {
                result = result.replace("\n", "\\n").replace("\r", "\\r")
                return objectMapper.readValue(result, PlayerData::class.java)
            } catch (e: JsonProcessingException) {
                LoggerUtil.error("An error occurred while deserializing.")
                return null
            }
        }
    }
}