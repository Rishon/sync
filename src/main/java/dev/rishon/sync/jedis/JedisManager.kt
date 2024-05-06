package dev.rishon.sync.jedis

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dev.rishon.sync.api.SyncAPI
import dev.rishon.sync.data.RedisData
import dev.rishon.sync.jedis.packets.IPacket
import dev.rishon.sync.utils.LoggerUtil
import dev.rishon.sync.utils.SchedulerUtil
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub


class JedisManager(redisData: RedisData) {

    private val mainChannel = "sync"
    private val jedisPool: JedisPool = redisData.jedisPool!!
    private val gson: Gson = GsonBuilder().registerTypeAdapter(IPacket::class.java, IPacketDeserializer()).create()
    private var jedisPubSub: JedisPubSub? = null

    init {
        instance = this
        // Initialize Subscription
        Thread {
            val jedisSubscriber = this.jedisPool.resource
            this.jedisPubSub = object : JedisPubSub() {
                override fun onMessage(channel: String, message: String) {
                    if (channel != mainChannel) return
                    try {
                        val jsonObject = gson.fromJson(message, JsonObject::class.java)
                        val packetClassName = jsonObject.remove("sync-packet").asString
                        val packetClass = Class.forName(packetClassName)
                        val packet: IPacket = gson.fromJson(jsonObject, packetClass) as IPacket
                        packet.onReceive()
                    } catch (ignored: Exception) {
                    }
                }
            }
            jedisSubscriber.subscribe(this.jedisPubSub, this.mainChannel)
            LoggerUtil.info("Subscribed to Redis channel: ${this.mainChannel}")
        }.start()
    }

    fun end() {
        this.jedisPubSub?.unsubscribe(this.mainChannel)
    }

    fun sendPacket(packet: IPacket) {
        SchedulerUtil.runTaskAsync {
            val fieldMap: MutableMap<String, Any> = HashMap()
            for (field in packet.javaClass.getDeclaredFields()) {
                field.setAccessible(true)
                fieldMap[field.name] = field.get(packet)
            }

            val jsonObject = gson.fromJson(gson.toJson(fieldMap), JsonObject::class.java)
            jsonObject.addProperty("sync-packet", packet.javaClass.getName())
            jsonObject.addProperty("instance", SyncAPI.getAPI().getInstanceID())

            this.jedisPool.resource.use { jedis ->
                jedis.publish(this.mainChannel, jsonObject.toString())
                jedis.close()
            }
        }
    }

    companion object {
        // Static-Access
        lateinit var instance: JedisManager
    }
}