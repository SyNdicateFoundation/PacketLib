package dev.mhpro.packetlib;

import dev.mhpro.packetlib.events.PacketConnectionEvent;
import dev.mhpro.packetlib.handlers.PacketCodec;
import dev.mhpro.packetlib.handlers.PacketOutBound;
import dev.mhpro.packetlib.objects.impl.PacketUser;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.netty.PipelineUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class PacketLib implements Listener {
    private static final Field SERVER_CHILD, MODIFIERS_FIELD;

    static {
        try {
            MODIFIERS_FIELD = Field.class.getDeclaredField("modifiers");
            MODIFIERS_FIELD.setAccessible(true);

            SERVER_CHILD = PipelineUtils.class.getDeclaredField("SERVER_CHILD");
            SERVER_CHILD.setAccessible(true);

            MODIFIERS_FIELD.setInt(SERVER_CHILD, SERVER_CHILD.getModifiers() & ~Modifier.FINAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private final Plugin plugin;

    @SneakyThrows
    public PacketLib(Plugin plugin) {
        this.plugin = plugin;

        ChannelInitializer<Channel> child = PipelineUtils.SERVER_CHILD;
        Method initChannel = child.getClass().getDeclaredMethod("initChannel", Channel.class);
        initChannel.setAccessible(true);

        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(@NotNull Channel channel) throws Exception {
                initChannel.invoke(child, channel);
                PacketLib.this.initChannel(channel);
            }
        };

        SERVER_CHILD.set(null, initializer);
    }

    private void initChannel(@NotNull Channel channel) throws InterruptedException {
        ChannelHandler handler = channel.pipeline().get("packetlib-user");

        if (handler != null) return;

        if (channel.pipeline().get("packet-decoder") == null) {
            channel.disconnect().sync();
            return;
        }

        PacketUser player = new PacketUser(channel);

        channel.pipeline().addAfter("packet-decoder", "packetlib-codec", new PacketCodec(player));
        channel.pipeline().addAfter("packet-encoder", "packetlib-out", new PacketOutBound(player));
        channel.pipeline().addLast("packetlib-user", player);

        PacketConnectionEvent event = new PacketConnectionEvent(player);
        PacketLibAPI.callEvent(event);

        if (event.isCancelled()){
            player.disconnect(event.getCancelReason());
        }
    }

    @SneakyThrows
    public PacketUser getUser(Object connection) {
        Field field = connection.getClass().getDeclaredField("ch");
        field.setAccessible(true);

        Object channel;
        channel = field.get(connection);

        try {
            Method method = channel.getClass().getDeclaredMethod("getHandle");
            method.setAccessible(true);
            channel = method.invoke(channel);
        } catch (Exception ignored) {

        }

        return ((Channel) channel).pipeline().get(PacketUser.class);
    }

}
