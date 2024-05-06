package dev.rishon.sync.utils

import dev.rishon.sync.Sync
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitScheduler


object SchedulerUtil {

    // Bukkit Scheduler
    private var scheduler: BukkitScheduler = Bukkit.getScheduler()

    @JvmStatic
    fun runTaskAsync(runnable: Runnable) {
        scheduler.runTaskAsynchronously(Sync.instance, runnable)
    }

    @JvmStatic
    fun runTaskTimerAsync(runnable: Runnable, ticks: Long) {
        scheduler.runTaskTimerAsynchronously(Sync.instance, runnable, 0, ticks)
    }

    @JvmStatic
    fun runTaskTimerSync(runnable: Runnable, ticks: Long) {
        scheduler.runTaskTimer(Sync.instance, runnable, 0, ticks)
    }

    @JvmStatic
    fun runTaskSync(runnable: Runnable) {
        scheduler.runTask(Sync.instance, runnable)
    }

}