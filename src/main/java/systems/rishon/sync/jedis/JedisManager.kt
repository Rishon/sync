package systems.rishon.sync.jedis

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import systems.rishon.sync.api.SyncAPI
import systems.rishon.sync.data.RedisData
import systems.rishon.sync.jedis.packet.IPacket
import systems.rishon.sync.jedis.packet.LogPacket
import systems.rishon.sync.utils.LoggerUtil
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class JedisManager(redisData: RedisData) {

    private val mainChannel = "sync"
    private val jedisPool: JedisPool = redisData.jedisPool!!
    private val gson: Gson = GsonBuilder().registerTypeAdapter(IPacket::class.java, IPacketDeserializer()).create()
    private var jedisPubSub: JedisPubSub? = null
    private val executorService = Executors.newFixedThreadPool(2)

    init {
        instance = this

        jedisPubSub = object : JedisPubSub() {
            override fun onMessage(channel: String, message: String) {
                if (channel != mainChannel) return

                CompletableFuture.runAsync {
                    try {
                        val jsonObject = gson.fromJson(message, JsonObject::class.java)
                        val packetClassName = jsonObject.remove("sync-packet").asString
                        val packetClass = Class.forName(packetClassName)
                        val packet: IPacket = gson.fromJson(jsonObject, packetClass) as IPacket
                        packet.onReceive()
                    } catch (e: Exception) {
                        LoggerUtil.error("Error processing message: ${e.message}")
                    }
                }
            }
        }

        // Start a new thread for subscribing to the Redis channel
        Thread {
            val jedisSubscriber = jedisPool.resource
            try {
                jedisSubscriber.subscribe(jedisPubSub, mainChannel)
                LoggerUtil.info("Subscribed to Redis channel: $mainChannel")
            } catch (e: Exception) {
                LoggerUtil.error("Error initializing Redis subscription: ${e.message}")
            } finally {
                jedisSubscriber.close()
            }
        }.start()

        // Notify other instances
        sendPacket(LogPacket("Instance ${SyncAPI.getAPI().getInstanceID()} has been connected"))
    }

    fun end() {
        jedisPubSub?.unsubscribe(mainChannel)
    }

    fun sendPacket(packet: IPacket) {
        CompletableFuture.supplyAsync({
            val fieldMap = ConcurrentHashMap<String, Any>()

            for (field in packet.javaClass.declaredFields) {
                field.isAccessible = true
                fieldMap[field.name] = field[packet]
            }

            fieldMap
        }, executorService).thenAcceptAsync({ fieldMap ->
            try {
                val jsonObject = gson.fromJson(gson.toJson(fieldMap), JsonObject::class.java)
                jsonObject.addProperty("sync-packet", packet::class.java.name)
                jsonObject.addProperty("instance", SyncAPI.getAPI().getInstanceID())

                jedisPool.resource.use { jedis ->
                    jedis.publish(mainChannel, jsonObject.toString())
                }
            } catch (_: Exception) {
                LoggerUtil.error("Failed to send packet: ${packet::class.java.simpleName}")
            }
        }, executorService)
    }

    companion object {
        // Static-Access
        lateinit var instance: JedisManager
    }
}
