package systems.rishon.sync.jedis.packet

import systems.rishon.sync.utils.LoggerUtil

class LogPacket(private val message: String) : IPacket {

    override fun onReceive() {
        LoggerUtil.info(message)
    }
}