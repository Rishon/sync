package dev.rishon.sync.jedis

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import dev.rishon.sync.jedis.packets.IPacket
import java.lang.reflect.Type

class IPacketDeserializer : JsonDeserializer<IPacket> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): IPacket {
        val jsonObject = json?.asJsonObject
        val packetClassName = jsonObject?.remove("sync-packet")?.asString
        packetClassName?.let {
            try {
                val packetClass = Class.forName(packetClassName).asSubclass(IPacket::class.java)
                return context!!.deserialize(jsonObject, packetClass)
            } catch (e: ClassNotFoundException) {
                throw JsonParseException("Invalid packet class: $packetClassName", e)
            } catch (e: ClassCastException) {
                throw JsonParseException("Invalid packet class type: $packetClassName", e)
            }
        }
        throw JsonParseException("Missing packet class information")
    }
}
