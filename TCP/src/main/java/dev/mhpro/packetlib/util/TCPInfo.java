package dev.mhpro.packetlib.util;

import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.incubator.channel.uring.IOUring;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class TCPInfo {
    public static TransportMethod OSTransportMethod() {
        if (isClassAvailable("io.netty.incubator.channel.uring.IOUring") && IOUring.isAvailable())
            return TransportMethod.IO_URING;
        if (isClassAvailable("io.netty.channel.epoll.Epoll") && Epoll.isAvailable()) return TransportMethod.EPOLL;
        if (isClassAvailable("io.netty.channel.kqueue.KQueue") && KQueue.isAvailable()) return TransportMethod.KQUEUE;
        return TransportMethod.NIO;
    }

    private static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }


    @RequiredArgsConstructor
    public enum TransportMethod {
        NIO("io.netty.channel.socket.nio.NioServerSocketChannel", "io.netty.channel.nio.NioEventLoopGroup", "io.netty.channel.socket.nio.NioSocketChannel"),
        EPOLL("io.netty.channel.epoll.EpollServerSocketChannel", "io.netty.channel.epoll.EpollEventLoopGroup", "io.netty.channel.epoll.EpollSocketChannel"),
        KQUEUE("io.netty.channel.kqueue.KQueueServerSocketChannel", "io.netty.channel.kqueue.KQueueEventLoopGroup", "io.netty.channel.kqueue.KQueueSocketChannel"),
        IO_URING("io.netty.incubator.channel.uring.IOUringServerSocketChannel", "io.netty.incubator.channel.uring.IOUringEventLoopGroup", "io.netty.incubator.channel.uring.IOUringSocketChannel");

        private final String server, group, client;
        private Class<? extends MultithreadEventLoopGroup> groupClass;
        private Class<? extends ServerSocketChannel> serverChannel;
        private Class<? extends SocketChannel> channelClass;

        @SuppressWarnings("unchecked")
        @SneakyThrows
        public @NotNull MultithreadEventLoopGroup getGroup() {
            if (groupClass == null) {
                groupClass = (Class<? extends MultithreadEventLoopGroup>) Class.forName(this.group);
            }
            return groupClass.getConstructor().newInstance();
        }

        @SuppressWarnings("unchecked")
        @SneakyThrows
        public Class<? extends ServerSocketChannel> getServerChannel() {
            if (serverChannel == null) {
                serverChannel = (Class<? extends ServerSocketChannel>) Class.forName(this.server);
            }
            return serverChannel;
        }

        @SneakyThrows
        @SuppressWarnings("unchecked")
        public Class<? extends SocketChannel> getClientChannel() {
            if (channelClass == null) {
                channelClass = (Class<? extends SocketChannel>) Class.forName(this.client);
            }
            return channelClass;
        }
    }
}
