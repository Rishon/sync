package dev.rishon.sync.listener

import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packet.WorldPacket
import dev.rishon.sync.utils.SchedulerUtil
import io.papermc.paper.event.world.border.WorldBorderBoundsChangeEvent
import io.papermc.paper.event.world.border.WorldBorderCenterChangeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.event.world.TimeSkipEvent

class WorldEvents : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onTimeSkip(event: TimeSkipEvent) {
        if (event.skipReason == TimeSkipEvent.SkipReason.CUSTOM) return
        SchedulerUtil.runTaskAsync {
            val world = event.world
            JedisManager.instance.sendPacket(
                WorldPacket(
                    world.name,
                    world.time,
                    world.worldBorder.size,
                    world.worldBorder.center.serialize(),
                    world.weatherDuration,
                    world.hasStorm(),
                    world.isThundering,
                    world.difficulty
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onWeather(event: WeatherChangeEvent) {
        val cause = event.cause
        if (cause == WeatherChangeEvent.Cause.PLUGIN) return
        SchedulerUtil.runTaskAsync {
            val world = event.world
            JedisManager.instance.sendPacket(
                WorldPacket(
                    world.name,
                    world.time,
                    world.worldBorder.size,
                    world.worldBorder.center.serialize(),
                    world.weatherDuration,
                    world.hasStorm(),
                    world.isThundering,
                    world.difficulty
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onBorderBoundsChange(event: WorldBorderBoundsChangeEvent) {
        val world = event.world
        val newSize = event.newSize
        if (newSize == world.worldBorder.size) return
        SchedulerUtil.runTaskAsync {
            JedisManager.instance.sendPacket(
                WorldPacket(
                    world.name,
                    world.time,
                    newSize,
                    world.worldBorder.center.serialize(),
                    world.weatherDuration,
                    world.hasStorm(),
                    world.isThundering,
                    world.difficulty
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onBorderCenterChange(event: WorldBorderCenterChangeEvent) {
        val world = event.world
        val newCenter = event.newCenter
        if (newCenter == world.worldBorder.center) return
        SchedulerUtil.runTaskAsync {
            JedisManager.instance.sendPacket(
                WorldPacket(
                    world.name,
                    world.time,
                    world.worldBorder.size,
                    newCenter.serialize(),
                    world.weatherDuration,
                    world.hasStorm(),
                    world.isThundering,
                    world.difficulty
                )
            )
        }
    }
}