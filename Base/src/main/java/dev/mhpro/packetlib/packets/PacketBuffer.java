package dev.mhpro.packetlib.packets;

import dev.mhpro.packetlib.data.BlockPosition;
import dev.mhpro.packetlib.data.Position;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.util.BlockPositionSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.UUID;

@AllArgsConstructor
public class PacketBuffer extends ByteBuf {
    private ByteBuf buffer;


    // Minecraft ---------------------------------------------------------------------------------------------------->

    public static PacketBuffer empty() {
        return new PacketBuffer(Unpooled.buffer());
    }

    public static PacketBuffer empty(int length) {
        return new PacketBuffer(Unpooled.buffer(length));
    }

    public static PacketBuffer string(String value) {
        PacketBuffer packetBuffer = PacketBuffer.empty(value.length());
        packetBuffer.writeCharSequence(value, StandardCharsets.UTF_8);
        return packetBuffer;
    }

    public int readVarInt() {
        int i = 0;
        int maxRead = Math.min(5, buffer.readableBytes());

        for (int j = 0; j < maxRead; j++) {
            int k = buffer.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128) {
                return i;
            }
        }

        buffer.readBytes(maxRead);

        throw new IllegalArgumentException("Cannot read VarInt");
    }

    public String readString(int maxLength) {
        int length = this.readVarInt();
        if (length > maxLength * 3)
            throw new IllegalArgumentException("String buffer is longer than maximum allowed length");
        String string = (String) buffer.readCharSequence(length, StandardCharsets.UTF_8);
        if (string.length() > maxLength)
            throw new IllegalArgumentException("String is longer than maximum allowed length");
        return string;
    }

    public String readString() {
        return (String) buffer.readCharSequence(this.readVarInt(), StandardCharsets.UTF_8);
    }

    public @NotNull UUID readUUID() {
        return new UUID(buffer.readLong(), buffer.readLong());
    }

    public void writeVarInt(int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                writeByte(value);
                return;
            }
            writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
    }

    public void writeString(@NotNull String string) {
        writeVarInt(string.length());
        writeCharSequence(string, StandardCharsets.UTF_8);
    }

    public void writeUUID(@NotNull UUID uuid) {
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
    }

    public long readVarLong() {
        int value = 0;
        int size = 0;
        int b;
        while (((b = buffer.readByte()) & 0x80) == 0x80) {
            value |= (b & 0x7F) << size++ * 7;
            if (size > 10) {
                throw new IllegalArgumentException("VarLong too long (length must be <= 10)");
            }
        }

        return value | (b & 0x7FL) << size * 7;
    }

    public byte[] readBytes() {
        return ByteBufUtil.getBytes(buffer);
    }

    public boolean isEmpty() {
        return this.readableBytes() <= 0;
    }

    public void writeComponent(Component component) {
        this.writeString(GsonComponentSerializer.gson().serialize(component));
    }

    public void writeVarLong(long value) {
        while (true) {
            if ((value & ~((long) 0x7F)) == 0) {
                writeByte((int) value);
                return;
            }

            writeByte((int) (value & 0x7F | 0x80));
            value >>>= 7;
        }

    }

    public void writePosition(Position position) {
        writeDouble(position.getX());
        writeDouble(position.getY());
        writeDouble(position.getZ());

        writeFloat(position.getYaw());
        writeFloat(position.getPitch());
    }

    public void writeBlockPosition(BlockPosition position, ProtocolVersion version) {
        BlockPositionSerializer.write(buffer, position, version);
    }

    @SneakyThrows
    public void writeTag(CompoundBinaryTag tag) {
        BinaryTagIO.writer().write(tag, new OutputStream() {
            @Override
            public void write(int b) {
                buffer.writeByte(b);
            }
        });
    }

    @SneakyThrows
    @Nullable
    public CompoundBinaryTag readTag() {
        return BinaryTagIO.reader().read(new InputStream() {
            @Override
            public int read() {
                return buffer.readUnsignedByte();
            }
        });
    }

    public void writeUnsignedByte(int id) {
        buffer.writeByte(id);
    }

    public BlockPosition readBlockPosition(ProtocolVersion version) {
        return BlockPositionSerializer.read(buffer, version);
    }

    public BitSet readBitSet(int size) {
        byte[] bytes = new byte[-Math.floorDiv(-size, 8)];
        buffer.readBytes(bytes);
        return BitSet.valueOf(bytes);
    }

    public void writeFixedBitSet(BitSet bits, int size) {
        if (bits.length() > size) {
            throw new StackOverflowError("BitSet too large (expected " + size + " got " + bits.size() + ")");
        }
        buffer.writeBytes(Arrays.copyOf(bits.toByteArray(), -Math.floorDiv(-size, 8)));
    }

    public Position readPosition() {
        return new Position(readDouble(), readDouble(), readDouble(), readFloat(), readFloat());
    }

    public @NotNull Component readComponent() {
        return GsonComponentSerializer.colorDownsamplingGson()
                .deserialize(readString());
    }

    public @NotNull Component readLegacyComponent() {
        return LegacyComponentSerializer.legacySection().deserialize(readString());
    }

    public byte[] readBytesArray() {
        byte[] array = new byte[readVarInt()];
        buffer.readBytes(array);
        return array;
    }

    public int[] readIntArray() {
        int[] array = new int[readVarInt()];
        for (int i = 0; i < array.length; i++) {
            array[i] = readVarInt();
        }
        return array;
    }

    public long[] readLongArray() {
        long[] array = new long[readVarInt()];
        for (int i = 0; i < array.length; i++) {
            array[i] = readLong();
        }
        return array;
    }

    public String[] readStringArray() {
        String[] array = new String[readVarInt()];
        for (int i = 0; i < array.length; i++) {
            array[i] = readString();
        }
        return array;
    }

    public void writeStringsArray(String[] stringArray) {
        writeVarInt(stringArray.length);
        for (String str : stringArray) {
            writeString(str);
        }
    }
    // ByteBuf ---------------------------------------------------------------------------------------------------->

    public void writeVarIntArray(int[] array) {
        writeVarInt(array.length);
        for (int i : array) {
            writeVarInt(i);
        }
    }

    public void writeLongArray(long[] array) {
        writeVarInt(array.length);
        for (long i : array) {
            writeLong(i);
        }
    }

    public void writeByteArray(byte[] array) {
        writeVarInt(array.length);
        writeBytes(array);
    }

    @Override
    public int capacity() {
        return buffer.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        return buffer.capacity(newCapacity);
    }

    @Override
    public int maxCapacity() {
        return buffer.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return buffer.alloc();
    }

    @Override
    @Deprecated
    public ByteOrder order() {
        return buffer.order();
    }

    @Override
    @Deprecated
    public ByteBuf order(ByteOrder endianness) {
        return buffer.order(endianness);
    }

    @Override
    public ByteBuf unwrap() {
        return buffer.unwrap();
    }

    @Override
    public boolean isDirect() {
        return buffer.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return buffer.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return buffer.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return buffer.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        return buffer.readerIndex(readerIndex);
    }

    @Override
    public int writerIndex() {
        return buffer.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        return buffer.writerIndex(writerIndex);
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        return buffer.setIndex(readerIndex, writerIndex);
    }

    @Override
    public int readableBytes() {
        return buffer.readableBytes();
    }

    @Override
    public int writableBytes() {
        return buffer.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return buffer.maxWritableBytes();
    }

    @Override
    public int maxFastWritableBytes() {
        return buffer.maxFastWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return buffer.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return buffer.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return buffer.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return buffer.isWritable(size);
    }

    @Override
    public ByteBuf clear() {
        return buffer.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return buffer.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return buffer.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return buffer.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return buffer.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return buffer.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return buffer.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        return buffer.ensureWritable(minWritableBytes);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return buffer.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return buffer.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return buffer.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return buffer.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return buffer.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return buffer.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return buffer.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return buffer.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return buffer.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return buffer.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return buffer.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return buffer.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return buffer.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return buffer.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return buffer.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return buffer.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return buffer.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return buffer.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return buffer.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return buffer.getFloat(index);
    }

    @Override
    public float getFloatLE(int index) {
        return buffer.getFloatLE(index);
    }

    @Override
    public double getDouble(int index) {
        return buffer.getDouble(index);
    }

    @Override
    public double getDoubleLE(int index) {
        return buffer.getDoubleLE(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        return buffer.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        return buffer.getBytes(index, dst, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        return buffer.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        return buffer.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        return buffer.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        return buffer.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        return buffer.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return buffer.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return buffer.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return buffer.getCharSequence(index, length, charset);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        return buffer.setBoolean(index, value);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        return buffer.setByte(index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        return buffer.setShort(index, value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        return buffer.setShortLE(index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        return buffer.setMedium(index, value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        return buffer.setMediumLE(index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        return buffer.setInt(index, value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        return buffer.setIntLE(index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        return buffer.setLong(index, value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        return buffer.setLongLE(index, value);
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        return buffer.setChar(index, value);
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        return buffer.setFloat(index, value);
    }

    @Override
    public ByteBuf setFloatLE(int index, float value) {
        return buffer.setFloatLE(index, value);
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        return buffer.setDouble(index, value);
    }

    @Override
    public ByteBuf setDoubleLE(int index, double value) {
        return buffer.setDoubleLE(index, value);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        return buffer.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        return buffer.setBytes(index, src, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        return buffer.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        return buffer.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        return buffer.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        return buffer.setBytes(index, src);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return buffer.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return buffer.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return buffer.setBytes(index, in, position, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        return buffer.setZero(index, length);
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return buffer.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return buffer.readBoolean();
    }

    @Override
    public byte readByte() {
        return buffer.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return buffer.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return buffer.readShort();
    }

    @Override
    public short readShortLE() {
        return buffer.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return buffer.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return buffer.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return buffer.readMedium();
    }

    @Override
    public int readMediumLE() {
        return buffer.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return buffer.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return buffer.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return buffer.readInt();
    }

    @Override
    public int readIntLE() {
        return buffer.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return buffer.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return buffer.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return buffer.readLong();
    }

    @Override
    public long readLongLE() {
        return buffer.readLongLE();
    }

    @Override
    public char readChar() {
        return buffer.readChar();
    }

    @Override
    public float readFloat() {
        return buffer.readFloat();
    }

    @Override
    public float readFloatLE() {
        return buffer.readFloatLE();
    }

    @Override
    public double readDouble() {
        return buffer.readDouble();
    }

    @Override
    public double readDoubleLE() {
        return buffer.readDoubleLE();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return buffer.readBytes(length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return buffer.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return buffer.readRetainedSlice(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        return buffer.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        return buffer.readBytes(dst, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        return buffer.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        return buffer.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        return buffer.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        return buffer.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        return buffer.readBytes(out, length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return buffer.readBytes(out, length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return buffer.readCharSequence(length, charset);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return buffer.readBytes(out, position, length);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        return buffer.skipBytes(length);
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        return buffer.writeBoolean(value);
    }

    @Override
    public ByteBuf writeByte(int value) {
        return buffer.writeByte(value);
    }

    @Override
    public ByteBuf writeShort(int value) {
        return buffer.writeShort(value);
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        return buffer.writeShortLE(value);
    }

    @Override
    public ByteBuf writeMedium(int value) {
        return buffer.writeMedium(value);
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        return buffer.writeMediumLE(value);
    }

    @Override
    public ByteBuf writeInt(int value) {
        return buffer.writeInt(value);
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        return buffer.writeIntLE(value);
    }

    @Override
    public ByteBuf writeLong(long value) {
        return buffer.writeLong(value);
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        return buffer.writeLongLE(value);
    }

    @Override
    public ByteBuf writeChar(int value) {
        return buffer.writeChar(value);
    }

    @Override
    public ByteBuf writeFloat(float value) {
        return buffer.writeFloat(value);
    }

    @Override
    public ByteBuf writeFloatLE(float value) {
        return buffer.writeFloatLE(value);
    }

    @Override
    public ByteBuf writeDouble(double value) {
        return buffer.writeDouble(value);
    }

    @Override
    public ByteBuf writeDoubleLE(double value) {
        return buffer.writeDoubleLE(value);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        return buffer.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        return buffer.writeBytes(src, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        return buffer.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        return buffer.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        return buffer.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        return buffer.writeBytes(src);
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return buffer.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return buffer.writeBytes(in, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return buffer.writeBytes(in, position, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        return buffer.writeZero(length);
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return buffer.writeCharSequence(sequence, charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return buffer.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return buffer.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return buffer.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return buffer.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return buffer.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return buffer.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return buffer.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return buffer.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        return buffer.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return buffer.copy(index, length);
    }

    @Override
    public ByteBuf slice() {
        return buffer.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return buffer.retainedSlice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return buffer.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return buffer.retainedSlice(index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return buffer.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return buffer.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return buffer.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return buffer.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return buffer.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return buffer.internalNioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return buffer.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return buffer.nioBuffers(index, length);
    }

    @Override
    public boolean hasArray() {
        return buffer.hasArray();
    }

    @Override
    public byte[] array() {
        return buffer.array();
    }

    @Override
    public int arrayOffset() {
        return buffer.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return buffer.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return buffer.memoryAddress();
    }

    @Override
    public boolean isContiguous() {
        return buffer.isContiguous();
    }

    @Override
    public String toString(Charset charset) {
        return buffer.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return buffer.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return buffer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return buffer.equals(obj);
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return this.buffer.compareTo(buffer);
    }

    @Override
    public String toString() {
        return buffer.toString();
    }

    @Override
    public ByteBuf retain(int increment) {
        return buffer.retain(increment);
    }

    @Override
    public ByteBuf retain() {
        return buffer.retain();
    }

    @Override
    public ByteBuf touch() {
        return buffer.touch();
    }

    @Override
    public ByteBuf touch(Object hint) {
        return buffer.touch(hint);
    }

    @Override
    public int refCnt() {
        return buffer.refCnt();
    }

    @Override
    public boolean release() {
        return buffer.release();
    }

    @Override
    public boolean release(int decrement) {
        return buffer.release(decrement);
    }

    public void writeLegacyComponent(Component component) {
        writeString(LegacyComponentSerializer
                .legacyAmpersand()
                .serialize(component));
    }
}
