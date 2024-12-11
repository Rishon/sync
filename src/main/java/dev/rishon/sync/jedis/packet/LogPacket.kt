package dev.rishon.sync.jedis.packet

import dev.rishon.sync.utils.LoggerUtil

class LogPacket(private val message: String) : IPacket {

    override fun onReceive() {
        LoggerUtil.info(message)
    }
}