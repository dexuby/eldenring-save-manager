package dev.dexuby.eldenringsavemanager;

public class ByteReader {

    private final byte[] bytes;
    private int offset = 0;

    public ByteReader(final byte[] bytes) {

        this.bytes = bytes;

    }

    public void skip(final int length) {

        this.offset += length;

    }

    public void jump(final int offset) {

        this.offset = offset;

    }

    public byte readByte() {

        return this.bytes[this.offset++];

    }

    public byte peakByte() {

        return this.bytes[this.offset];

    }

    public byte peakByte(final int offset) {

        return this.bytes[this.offset + offset];

    }

    public byte[] readBytes(final int amount) {

        final byte[] out = new byte[amount];
        for (int i = 0; i < amount; i++)
            out[i] = this.readByte();

        return out;

    }

    public byte[] peakBytes(final int amount) {

        final byte[] out = new byte[amount];
        for (int i = 0; i < amount; i++)
            out[i] = this.peakByte(i);

        return out;

    }

    public boolean readBoolean() {

        return this.readByte() == 0x1;

    }

    public short readShort() {

        return (short) (this.readByte() << 8 | this.readByte() & 0xFF);

    }

    public int readBigEndianInteger() {

        return (this.readByte() << 24 | (this.readByte() & 0xFF) << 16 | (this.readByte() & 0xFF) << 8 | this.readByte() & 0xFF);

    }

    public long readBigEndianLong() {

        return ((long) this.readBigEndianInteger() << 32) | this.readBigEndianInteger();

    }

    public int peakBigEndianInteger() {

        return this.peakBigEndianInteger(0);

    }

    public int peakBigEndianInteger(int offset) {

        return (this.peakByte(offset++) << 24 | (this.peakByte(offset++) & 0xFF) << 16 | (this.peakByte(offset++) & 0xFF) << 8 | this.peakByte(offset) & 0xFF);

    }

    public long peakBigEndianLong() {

        return ((long) this.peakBigEndianInteger() << 32) | this.peakBigEndianInteger(4);

    }

    public int readLittleEndianInteger() {

        return (this.readByte() & 0xFF | (this.readByte() & 0xFF) << 8 | (this.readByte() & 0xFF) << 16 | this.readByte() << 24);

    }

    public long readLittleEndianLong() {

        return this.readLittleEndianInteger() | ((long) this.readLittleEndianInteger() << 32);

    }

    public int peakLittleEndianInteger() {

        return this.peakLittleEndianInteger(0);

    }

    public int peakLittleEndianInteger(int offset) {

        return (this.peakByte(offset++) & 0xFF | (this.peakByte(offset++) & 0xFF) << 8 | (this.peakByte(offset++) & 0xFF) << 16 | this.peakByte(offset) << 24);

    }

    public long peakLittleEndianLong() {

        return this.peakLittleEndianInteger() | ((long) this.peakLittleEndianInteger(4) << 32);

    }

    public int getOffset() {

        return this.offset;

    }

    public int getLength() {

        return this.bytes.length;

    }

}
