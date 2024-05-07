package dev.rishon.sync.listeners

import dev.rishon.sync.enums.Animations
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packets.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityToggleGlideEvent
import org.bukkit.event.entity.EntityToggleSwimEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerAnimationType
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent

class AnimationEvent : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerAnimation(event: PlayerAnimationEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val animation = event.animationType

        val animationID = when (animation) {
            PlayerAnimationType.ARM_SWING -> {
                Animations.SWING_MAIN_ARM
            }

            PlayerAnimationType.OFF_ARM_SWING -> {
                Animations.SWIM_OFFHAND
            }
        }

        JedisManager.instance.sendPacket(AnimationPacket(uuid, animationID))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerSneak(event: PlayerToggleSneakEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val isSneaking = event.isSneaking
        val isSwimming = player.isSwimming
        if (isSwimming) return
        JedisManager.instance.sendPacket(SneakPacket(uuid, isSneaking))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerSwim(event: EntityToggleSwimEvent) {
        val entity = event.entity
        val player = entity as? Player ?: return
        val uuid = player.uniqueId
        val isSwimming = event.isSwimming
        JedisManager.instance.sendPacket(SwimPacket(uuid, isSwimming))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerGlide(event: EntityToggleGlideEvent) {
        val entity = event.entity
        val player = entity as? Player ?: return
        val uuid = player.uniqueId
        val isGliding = event.isGliding
        JedisManager.instance.sendPacket(GlidePacket(uuid, isGliding))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val location = player.location.serialize()
        JedisManager.instance.sendPacket(MovePacket(uuid, location))
    }
}