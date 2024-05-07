package dev.rishon.sync.utils

import dev.rishon.sync.Sync
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitScheduler


object SchedulerUtil {

    // Bukkit Scheduler
    @JvmStatic
    var scheduler: BukkitScheduler = Bukkit.getScheduler()

    @JvmStatic
    fun runTaskAsync(runnable: Runnable) {
        this.scheduler.runTaskAsynchronously(Sync.instance, runnable)
    }

    @JvmStatic
    fun runTaskTimerAsync(runnable: Runnable, ticks: Long) {
        this.scheduler.runTaskTimerAsynchronously(Sync.instance, runnable, 0, ticks)
    }

    @JvmStatic
    fun runTaskTimerSync(runnable: Runnable, ticks: Long) {
        this.scheduler.runTaskTimer(Sync.instance, runnable, 0, ticks)
    }

    @JvmStatic
    fun runTaskSync(runnable: Runnable) {
        this.scheduler.runTask(Sync.instance, runnable)
    }

}