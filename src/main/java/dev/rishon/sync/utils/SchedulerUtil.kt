package dev.rishon.sync.utils

import dev.rishon.sync.Sync
import io.papermc.paper.threadedregions.scheduler.FoliaAsyncScheduler
import io.papermc.paper.threadedregions.scheduler.FoliaGlobalRegionScheduler
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitScheduler
import java.util.concurrent.TimeUnit
import java.util.function.Consumer


object SchedulerUtil {

    // Instance
    private val INSTANCE: Sync = Sync.instance

    // Bukkit Scheduler
    @JvmStatic
    val scheduler: BukkitScheduler = Bukkit.getScheduler()

    // Folia
    @JvmStatic
    val asyncScheduler: FoliaAsyncScheduler = FoliaAsyncScheduler()
    val regionScheduler: FoliaGlobalRegionScheduler = FoliaGlobalRegionScheduler()

    @JvmStatic
    fun runTaskAsync(runnable: Consumer<Any?>) {
        if (INSTANCE.isFolia) {
            asyncScheduler.runNow(INSTANCE) { t: ScheduledTask? -> runnable.accept(t) }
        } else {
            this.scheduler.runTaskAsynchronously(Sync.instance, runnable)
        }
    }

    @JvmStatic
    fun runTaskTimerAsync(runnable: Consumer<Any?>, ticks: Long) {
        if (INSTANCE.isFolia) {
            asyncScheduler.runAtFixedRate(
                INSTANCE,
                { t: ScheduledTask? -> runnable.accept(t) },
                0,
                ticks * 5000L,
                TimeUnit.MICROSECONDS
            )
        } else {
            this.scheduler.runTaskTimerAsynchronously(Sync.instance, runnable, 0, ticks)
        }
    }

    @JvmStatic
    fun runTaskTimerSync(runnable: Consumer<Any?>, ticks: Long) {
        if (INSTANCE.isFolia) {
            regionScheduler.runAtFixedRate(INSTANCE, { t: ScheduledTask? -> runnable.accept(t) }, 0, ticks)
        } else {
            this.scheduler.runTaskTimer(Sync.instance, runnable, 0, ticks)
        }
    }

    @JvmStatic
    fun runTaskSync(runnable: Consumer<Any?>) {
        if (INSTANCE.isFolia) {
            regionScheduler.run(INSTANCE) { t: ScheduledTask? -> runnable.accept(t) }
        } else {
            this.scheduler.runTask(Sync.instance, runnable)
        }
    }

}