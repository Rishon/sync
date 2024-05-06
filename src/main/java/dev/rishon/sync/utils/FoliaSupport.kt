package dev.rishon.sync.utils

object FoliaSupport {
    @JvmStatic
    fun isFolia(): Boolean {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
            return true
        } catch (e: ClassNotFoundException) {
            return false
        }
    }
}