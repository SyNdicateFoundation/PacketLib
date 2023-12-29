package dev.mhpro.packetlib.events;

import dev.mhpro.packetlib.objects.impl.PacketUser;
import dev.mhpro.packetlib.packets.Packet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
@Data
@EqualsAndHashCode(callSuper = false)
public class PacketEvent extends Event {
    private final Packet packet;
    private final ProxiedPlayer player;
    private final PacketUser user;

    private boolean cancelled;

}
