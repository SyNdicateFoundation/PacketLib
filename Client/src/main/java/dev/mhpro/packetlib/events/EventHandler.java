package dev.mhpro.packetlib.events;

import dev.mhpro.packetlib.packets.Packet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

public abstract class EventHandler {
    public void onPacketReceive(PacketEvent event) {

    }

    public void onPacketSent(PacketEvent event) {

    }

    public void onConnectToServer() {

    }

    public interface Event {

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    public static class PacketEvent extends CancelableEvent {
        private final Packet packet;
        private final boolean canceled;
    }

    @Data
    @SuperBuilder
    public static class CancelableEvent implements Event {
        private boolean canceled;
    }

}
