package com.soap.search.store;

/**
 * 改进后的 BitReader
 */
class BitReader {
    private final byte[] data;
    private int bitPos = 0;

    public BitReader(byte[] data) {
        this.data = data;
    }

    public int readBit() {
        if (bitPos >= data.length * 8) return -1;
        int bit = (data[bitPos / 8] >> (7 - (bitPos % 8))) & 1;
        bitPos++;
        return bit;
    }

    public int readBits(int numBits) {
        int value = 0;
        for (int i = 0; i < numBits; i++) {
            int bit = readBit();
            if (bit == -1) return -1; // 提前结束
            value = (value << 1) | bit;
        }
        return value;
    }

    // 剩余比特数
    public int remaining() {
        return (data.length * 8) - bitPos;
    }
}
