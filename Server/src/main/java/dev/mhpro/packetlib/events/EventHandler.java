package dev.mhpro.packetlib.events;

import dev.mhpro.packetlib.objects.Player;
import dev.mhpro.packetlib.packets.Packet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

public abstract class EventHandler {
    public void onPacketReceive(PacketEvent event) {

    }

    public void onPacketSent(PacketEvent event) {

    }

    public void onPlayerLogin(CancelableEvent event) {

    }

    public void onPlayerJoin(Player player) {

    }

    public void onPlayerKick(CancelableEvent event, String reason) {

    }

    public void onPlayerLeave(Player player) {

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    public static class PacketEvent extends CancelableEvent {
        private final Packet packet;
        private final boolean canceled;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    public static class CancelableEvent extends Event {
        private  boolean canceled;
    }

    @Data
    @SuperBuilder
    public static class Event {
        private final Player player;

    }
}
