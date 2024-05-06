package dev.rishon.sync.jedis.packets

interface IPacket {

    fun onReceive()
}