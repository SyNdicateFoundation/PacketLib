package dev.mhpro.packetlib.util;

import dev.mhpro.packetlib.data.BlockPosition;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;


@UtilityClass
public final class BlockPositionSerializer {
    private final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private final int PACKED_X_LENGTH = 1 + log2(smallestEncompassingPowerOfTwo(30000000));
    private final int PACKED_Z_LENGTH = PACKED_X_LENGTH;
    private final int PACKED_Y_LENGTH = 64 - PACKED_X_LENGTH - PACKED_Z_LENGTH;
    private final long PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
    private final int Z_OFFSET = PACKED_Y_LENGTH;
    private final int X_OFFSET = PACKED_Y_LENGTH + PACKED_Z_LENGTH;
    private final long PACKED_Z_MASK = (1L << PACKED_Z_LENGTH) - 1L;
    private final long PACKED_X_MASK = (1L << PACKED_X_LENGTH) - 1L;

    public BlockPosition read(ByteBuf byteBuf, ProtocolVersion protocolVersion) {
        long val = byteBuf.readLong();

        if (ProtocolVersion.v1_14.moreOrEqual(protocolVersion)) {
            return new BlockPosition((int) (val >> 38), (int) ((val >> 26) & 0xFFF), (int) (val << 38 >> 38));
        }

        return new BlockPosition(getX(val), getY(val),getZ(val));
    }

    public void write(ByteBuf byteBuf, BlockPosition position, ProtocolVersion protocolVersion) {
        if (ProtocolVersion.v1_14.moreOrEqual(protocolVersion)) {
            byteBuf.writeLong(((long) position.getX() & 67108863) << 38 | ((long) position.getY() & 4095) << 26 | ((long) position.getZ() & 67108863));
            return;
        }

        byteBuf.writeLong(asLong(position.getX(), position.getY(), position.getZ()));
    }

    private long asLong(int x, int y, int z) {
        long i = 0L;
        i = i | ((long) x & PACKED_X_MASK) << X_OFFSET;
        i = i | ((long) y & PACKED_Y_MASK);
        return i | ((long) z & PACKED_Z_MASK) << Z_OFFSET;
    }

    private int getZ(long val) {
        return (int) (val << 64 - Z_OFFSET - PACKED_Z_LENGTH >> 64 - PACKED_Z_LENGTH);
    }

    public int getY(long val) {
        return (int) (val << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
    }

    private int getX(long val) {
        return (int) (val << 64 - X_OFFSET - PACKED_X_LENGTH >> 64 - PACKED_X_LENGTH);
    }

    private int smallestEncompassingPowerOfTwo(int val) {
        int i = val - 1;
        i = i | i >> 1;
        i = i | i >> 2;
        i = i | i >> 4;
        i = i | i >> 8;
        i = i | i >> 16;
        return i + 1;
    }

    private boolean isPowerOfTwo(int val) {
        return val != 0 && (val & val - 1) == 0;
    }

    private int log2(int val) {
        return ceillog2(val) - (isPowerOfTwo(val) ? 0 : 1);
    }

    private int ceillog2(int val) {
        val = isPowerOfTwo(val) ? val : smallestEncompassingPowerOfTwo(val);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) ((long) val * 125613361L >> 27) & 31];
    }

}
