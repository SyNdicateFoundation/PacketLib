package dev.mhpro.packetlib.decoders;

import dev.mhpro.packetlib.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;

import java.util.List;

public class PacketFrameDecoder extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf buf, ByteBuf out) {
        PacketBuffer buffer = new PacketBuffer(out);
        buffer.writeVarInt(buf.readableBytes());
        buffer.writeBytes(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.printf("Error in %s%n", this.getClass().getSimpleName());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (!ctx.channel().isActive()) {
            in.clear();
            return;
        }

        VarIntByteDecoder reader = new VarIntByteDecoder();
        int varIntEnd = in.forEachByte(reader);

        if (varIntEnd == -1) return;

        if (reader.getResult() == VarIntByteDecoder.DecodeResult.SUCCESS) {
            int readVarInt = reader.getReadVarInt();
            int bytesRead = reader.getBytesRead();
            if (readVarInt < 0) {
                throw new DecoderException("Bad data length");
            } else if (readVarInt == 0) {
                in.readerIndex(varIntEnd + 1);
            } else {
                int minimumRead = bytesRead + readVarInt;
                if (in.isReadable(minimumRead)) {
                    out.add(in.retainedSlice(varIntEnd + 1, readVarInt));
                    in.skipBytes(minimumRead);
                }
            }
        } else if (reader.getResult() == VarIntByteDecoder.DecodeResult.TOO_BIG) {
            throw new DecoderException("Too big data");
        }

    }
}