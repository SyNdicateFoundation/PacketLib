package dev.mhpro.packetlib.server.codec;

import dev.mhpro.packetlib.iPacketLibServer;
import dev.mhpro.packetlib.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import lombok.Builder;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


@Builder
public class PacketCompression extends ByteToMessageCodec<ByteBuf> {
    private static final int MAX_COMPRESSED_SIZE = 0x200000;
    private final Deflater deflater = new Deflater();
    private final Inflater inflater = new Inflater();
    private final byte[] buffer = new byte[0x2000];
    private final boolean validateDecompression;
    private final iPacketLibServer server;

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.deflater.end();
        this.inflater.end();
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.printf("Error in %s%n", this.getClass().getSimpleName());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        if (!ctx.channel().isActive()) {
            in.clear();
            return;
        };

        if (server.getCompressionThreshold() < 0) {
            out.writeBytes(in);
            return;
        }

        PacketBuffer buffer = new PacketBuffer(out);
        int size = in.readableBytes();

        buffer.writeVarInt(Math.max(size, 0));

        if (size < this.server.getCompressionThreshold() || this.server.getCompressionThreshold() <=0) {
            buffer.writeBytes(in);
            return;
        }

        byte[] bytes = new byte[size];
        in.readBytes(bytes);

        this.deflater.setInput(bytes, 0, size);
        this.deflater.finish();

        while (!this.deflater.finished()) {
            int length = this.deflater.deflate(this.buffer);
            buffer.writeBytes(this.buffer, 0, length);
        }


        this.deflater.reset();

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!ctx.channel().isActive()) {
            in.clear();
            return;
        };

        if (server.getCompressionThreshold() < 0) {
            out.add(in.readBytes(in.readableBytes()));
            return;
        }

        PacketBuffer buffer = new PacketBuffer(in);

        int size = buffer.readVarInt();

        if (size == 0) {
            out.add(in.readBytes(in.readableBytes()));
            return;
        }

        if (server.getCompressionThreshold() <= 0) {
            out.add(in.readBytes(in.readableBytes()));
            return;
        }

        if (validateDecompression) {
            if (size < this.server.getCompressionThreshold()) {
                throw new DecoderException("Badly compressed packet: size of " + size + " is below threshold of " + this.server.getCompressionThreshold() + ".");
            }

            if (size > MAX_COMPRESSED_SIZE) {
                throw new DecoderException("Badly compressed packet: size of " + size + " is larger than protocol maximum of " + MAX_COMPRESSED_SIZE + ".");
            }
        }

        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);

        this.inflater.setInput(bytes);
        byte[] inflated = new byte[size];

        this.inflater.inflate(inflated);
        out.add(Unpooled.wrappedBuffer(inflated));
        this.inflater.reset();
    }
}
