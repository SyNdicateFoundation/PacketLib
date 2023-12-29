package dev.mhpro.packetlib.events;

import dev.mhpro.packetlib.objects.impl.PacketUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.plugin.Event;

@EqualsAndHashCode(callSuper = true)
@Data
public class PacketLoginSuccessEvent extends Event {
    private final PacketUser user;
    private boolean cancelled;
    private String cancelReason = "Login Cancelled";
}
