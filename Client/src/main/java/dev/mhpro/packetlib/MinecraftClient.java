package dev.mhpro.packetlib;

import dev.mhpro.packetlib.decoders.PacketFrameDecoder;
import dev.mhpro.packetlib.enums.HandshakeState;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.events.EventHandler;
import dev.mhpro.packetlib.handlers.PacketInboundHandler;
import dev.mhpro.packetlib.handlers.PacketOutboundStream;
import dev.mhpro.packetlib.objects.iPacketPlayer;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.client.handshake.ClientHandshake;
import dev.mhpro.packetlib.packets.client.login.ClientLoginStart;
import dev.mhpro.packetlib.util.TCPInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@Builder
public class MinecraftClient extends ChannelInitializer<Channel> implements iPacketPlayer, Closeable {
    private final List<EventHandler> eventHandlers = Collections.synchronizedList(new ArrayList<>());
    @Builder.Default
    private HandshakeState currentState = HandshakeState.HANDSHAKE;
    @Builder.Default
    private ProtocolVersion version = ProtocolVersion.v1_19_4;
    private String name;
    private UUID uuid;
    private ChannelFuture channelFuture;
    private InetSocketAddress currentTarget;
    private Channel channel;

    @SneakyThrows
    public MinecraftClient connect(String target, int port) {
        if (this.currentTarget != null) return this;

        if (version == null || version == ProtocolVersion.ALL) {
            throw new IllegalArgumentException("Version of player cannot be null or All");
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Username of player cannot be null or Empty");
        }

        this.currentTarget = new InetSocketAddress(target, port);

        TCPInfo.TransportMethod transportMethod = TCPInfo.OSTransportMethod();

        this.channelFuture = new Bootstrap()
                .group(transportMethod.getGroup())
                .channel(transportMethod.getClientChannel())
                .handler(this)
                .connect(target, port);

        channelFuture.sync();

        return this;
    }

    @SneakyThrows
    @Synchronized("eventHandlers")
    public void callEvent(String event, Object... objects) {
        Method method = EventHandler.class.getMethod(event, Arrays.stream(objects).map(Object::getClass).toArray(Class[]::new));

        for (EventHandler handler : this.eventHandlers) {
            method.invoke(handler, objects);
        }
    }


    public MinecraftClient event(EventHandler... events) {
        this.eventHandlers.addAll(Arrays.asList(events));
        return this;
    }

    @Override
    protected void initChannel(@NotNull Channel ch) {
        this.channel = ch;

        ch.pipeline().addLast("Timeout", new ReadTimeoutHandler(20, TimeUnit.SECONDS))
                .addFirst("PacketFrameDecoder", new PacketFrameDecoder())
//                                .addLast("PacketCompression", PacketCompression.builder()
//                                        .server(MinecraftServer.this)
//                                        .validateDecompression(true)
//                                        .build())
//                .addLast("PacketConverter", new PacketConverterClient(this))
                .addLast("OutboundStream", new PacketOutboundStream(this))
                .addLast("InboundStream", new PacketInboundHandler(this))
                .addLast((new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
                        MinecraftClient.this.sendPacket(new ClientHandshake(version, currentTarget.getHostString(), (short) currentTarget.getPort(), HandshakeState.LOGIN, version.getProtocol()));
                        MinecraftClient.this.sendPacket(new ClientLoginStart(MinecraftClient.this.name,
                                MinecraftClient.this.uuid != null ? uuid : null,
                                System.currentTimeMillis(), false, new byte[0], new byte[0])
                        );
                        super.channelActive(ctx);
                    }
                }));
    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    @Override
    public void sendPacket(Packet... packets) {
        if (!this.isConnected()) {
            throw new RuntimeException("Client session closed before sent packet!");
        }

        for (Packet packet : packets) {
            this.channel.writeAndFlush(packet);
        }

    }

    @Override
    public void close() {

    }

    public MinecraftClient connect(String target) {
        return this.connect(target, 25565);
    }
}
