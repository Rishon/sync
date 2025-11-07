package systems.rishon.sync.utils

import systems.rishon.sync.Sync
import systems.rishon.sync.handler.FileHandler

object LoggerUtil {

    fun info(message: String) {
        Sync.instance.logger.info(message)
    }

    fun error(message: String) {
        Sync.instance.logger.severe(message)
    }

    fun warn(message: String) {
        Sync.instance.logger.warning(message)
    }

    fun debug(message: String) {
        if (FileHandler.handler.debug) Sync.instance.logger.info("[DEBUG] $message")
    }

}