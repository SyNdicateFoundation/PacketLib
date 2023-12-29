package dev.mhpro.packetlib.objects;

import dev.mhpro.packetlib.MinecraftServer;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.events.EventHandler;
import dev.mhpro.packetlib.objects.impl.PacketUser;
import io.netty.channel.Channel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Player extends PacketUser {
    private final MinecraftServer server;
    private long lastKeepAlive;

    public long getLastKeepAlive() {
        return System.currentTimeMillis() - lastKeepAlive;
    }

    public Player(MinecraftServer server, Channel channel) {
        super(channel);
        this.server = server;
    }

    public ProtocolVersion getVersion() {
        return Optional.ofNullable(super.getVersion()).orElse(server.getVersion());
    }


    @SneakyThrows
    public void disconnect(String message) {
        EventHandler.CancelableEvent event = EventHandler.CancelableEvent.builder()
                .player(this)
                .build();

        this.getServer().callEvent("onPlayerKick", event, message);

        if (event.isCanceled()) return;

       super.disconnect(message);
    }

}
