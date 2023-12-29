package dev.mhpro.packetlib;

public interface iPacketLibServer {

    default int getCompressionThreshold() {
        return -1;
    }
}