package dev.mhpro.packetlib;

import dev.mhpro.packetlib.decoders.PacketFrameDecoder;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.events.EventHandler;
import dev.mhpro.packetlib.handlers.PacketInboundHandler;
import dev.mhpro.packetlib.handlers.PacketOutboundStream;
import dev.mhpro.packetlib.objects.Player;
import dev.mhpro.packetlib.objects.world.DimensionRegistry;
import dev.mhpro.packetlib.objects.world.World;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.server.play.ServerKeepAlivePacket;
import dev.mhpro.packetlib.server.codec.PacketCompression;
import dev.mhpro.packetlib.server.codec.PacketConverterServer;
import dev.mhpro.packetlib.util.TCPInfo;
import dev.mhpro.packetlib.utils.Logger;
import dev.mhpro.packetlib.utils.TaskManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.Closeable;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@SuperBuilder
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class MinecraftServer implements Closeable, Runnable, iPacketLibServer {
    @Builder.Default
    private final String address = "0.0.0.0";
    @Builder.Default
    private final int port = 25565;
    private final List<Player> clientList = Collections.synchronizedList(new ArrayList<>());
    private final Map<Class<? extends Packet>, Packet> cachedPackets = new ConcurrentHashMap<>();
    private final List<EventHandler> eventHandlers = Collections.synchronizedList(new ArrayList<>());
    private ChannelFuture channelFuture;
    @Builder.Default
    private ProtocolVersion version = ProtocolVersion.ALL;
    @Builder.Default
    private int compressionThreshold = -1;
    @Builder.Default
    private int maxPlayer = -1;
    @Builder.Default
    private List<World> worlds = Collections.synchronizedList(new ArrayList<>());
    @Getter(AccessLevel.NONE)
    private TaskManager.Task keepAliveTask;

    public boolean isListening() {
        return this.channelFuture != null && this.channelFuture.channel().isOpen();
    }


    @SneakyThrows
    @Synchronized("worlds")
    public MinecraftServer loadWorlds(Path workingDir) {
        Path worlds = workingDir == null ? Paths.get(".") : workingDir;

        @Cleanup Stream<Path> listWorlds = Files.list(worlds);

        listWorlds.forEach((path) -> {
            if (!Files.isDirectory(path)) return;
            this.loadWorld(path);
        });

        return this;
    }


    @SneakyThrows
    @Synchronized("worlds")
    public void loadWorld(Path path) {
        Path settingsJson = path.resolve("settings.json");

        if (!Files.exists(settingsJson)) {
            throw new NullPointerException("World doesn't contains settings.json");
        }

        @Cleanup BufferedReader jsonReader = Files.newBufferedReader(settingsJson, StandardCharsets.UTF_8);

        World.WorldSettings settings = GsonComponentSerializer.gson().serializer().fromJson(jsonReader, World.WorldSettings.class);

        World world = World.builder()
                .settings(settings)
                .registry(new DimensionRegistry())
                .build();

        Logger.info("World " + settings.getName() + " Loaded!");

        worlds.add(world);
    }

    @Synchronized("worlds")
    public void unloadWorld(World world) {
        worlds.remove(world);
    }

    public MinecraftServer event(EventHandler... events) {
        this.eventHandlers.addAll(Arrays.asList(events));
        return this;
    }

    @Override
    @SneakyThrows
    public void run() {
        if (this.isListening()) return;

        TCPInfo.TransportMethod transportMethod = TCPInfo.OSTransportMethod();

        this.keepAliveTask = TaskManager.asyncRepeat(() -> {
            for (Player player : clientList) {
                if (!player.isConnected()) {
                    synchronized (clientList) {
                        clientList.remove(player);
                    }
                    continue;
                }
                if (!player.isPlaying()) continue;
                if (player.getLastKeepAlive() >= 15000) {
                    player.sendPacket(new ServerKeepAlivePacket(-(System.currentTimeMillis() / 1000)));
                    player.setLastKeepAlive(System.currentTimeMillis());
                }

            }
        }, 1000, TimeUnit.MILLISECONDS);


        channelFuture = new ServerBootstrap().channel(transportMethod.getServerChannel())
                .childHandler(new ChannelInitializer<Channel>() {

                    private Player player;

                    @Override
                    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                        removePlayer((Player) ctx.pipeline().last());
                        MinecraftServer.this.callEvent("onPlayerLeave", player);
                        super.channelUnregistered(ctx);
                    }

                    @Override
                    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
                        removePlayer((Player) ctx.pipeline().last());
                        MinecraftServer.this.callEvent("onPlayerLeave", player);
                        super.channelInactive(ctx);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                        if (player != null) {
                            //noinspection UnnecessaryUnicodeEscape
                            player.disconnect("&4\u274C &c" + cause.getMessage());
                        }

                    }

                    @Override
                    public void initChannel(@NotNull Channel channel) {
                        channel.config().setOption(ChannelOption.IP_TOS, 0x18);

                        try {
                            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException ignored) {
                        }

                        try {
                            channel.config().setOption(ChannelOption.SO_KEEPALIVE, true);
                        } catch (ChannelException ignored) {
                        }

                        ChannelPipeline pipeline = channel.pipeline();

                        player = new Player(MinecraftServer.this, channel);

                        pipeline.addLast("Timeout", new ReadTimeoutHandler(20, TimeUnit.SECONDS))
                                .addFirst("PacketFrameDecoder", new PacketFrameDecoder())
                                .addLast("PacketCompression", PacketCompression.builder()
                                        .server(MinecraftServer.this)
                                        .validateDecompression(true)
                                        .build())
                                .addLast("PacketConverter", new PacketConverterServer(player))
                                .addLast("OutboundStream", new PacketOutboundStream(player))
                                .addLast("InboundStream", new PacketInboundHandler(player));

                        synchronized (clientList) {
                            clientList.add(player);
                        }
                    }
                }).group(transportMethod.getGroup())
                .localAddress(Optional.ofNullable(this.getAddress()).orElse("0.0.0.0"), Optional.of(this.getPort()).orElse(25565))
                .bind();

        channelFuture.sync();
    }

    @Synchronized("clientList")
    public void removePlayer(Player player) {
        clientList.remove(player);
    }

    public void close(boolean wait, final Runnable callback) {
        if (this.channelFuture == null || !this.channelFuture.channel().isOpen()) return;

        if (keepAliveTask != null) {
            keepAliveTask.cancel();
        }

        ChannelFuture future = this.channelFuture.channel().close();
        this.channelFuture = null;

        if (wait) {
            try {
                future.sync();
                if (callback != null) callback.run();
            } catch (Exception e) {
                Logger.error("Failed to asynchronously close connection listener.", e);
            }

            return;
        }

        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                if (callback != null) callback.run();
                return;
            }
            Logger.error("Failed to asynchronously close connection listener.", future1.cause());
        });


    }

    @Override
    public void close() {
    }

    public World getMainLevel() {
        return worlds.get(0);
    }

    @Synchronized("clientList")
    public Player getPlayer(String name) {
        for (Player player : this.clientList) {
            if (player == null) continue;
            if (player.getName() == null) continue;
            if (!player.getName().equalsIgnoreCase(name)) continue;
            return player;
        }
        return null;
    }

    @Synchronized("clientList")
    public Player getPlayer(UUID uuid) {
        for (Player player : this.clientList) {
            if (player == null) continue;
            if (player.getUuid() == null) continue;
            if (!player.getUuid().equals(uuid)) continue;
            return player;
        }
        return null;
    }

    public boolean isOnline(String name) {
        return this.getPlayer(name) != null;
    }

    public boolean isOnline(UUID uuid) {
        return this.getPlayer(uuid) != null;
    }

    @SneakyThrows
    @Synchronized("eventHandlers")
    public void callEvent(String event, Object... objects) {
        Method method = EventHandler.class.getMethod(event, Arrays.stream(objects).map(Object::getClass).toArray(Class[]::new));

        for (EventHandler handler : this.eventHandlers) {
            method.invoke(handler, objects);
        }
    }


}
