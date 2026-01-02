package com.soap.search.store;

import java.util.*;

/**
 * 将比特位写入字节数组
 */
class BitWriter {
    private byte[] buffer;
    private int bitPos = 0;

    public BitWriter(int byteCapacity) {
        this.buffer = new byte[byteCapacity];
    }

    public void writeBit(int bit) {
        if (bitPos / 8 >= buffer.length) {
            buffer = Arrays.copyOf(buffer, buffer.length * 2);
        }
        if (bit == 1) {
            // 大端位序：从字节的高位向低位填充
            buffer[bitPos / 8] |= (1 << (7 - (bitPos % 8)));
        }
        bitPos++;
    }

    public void writeBits(int value, int numBits) {
        for (int i = numBits - 1; i >= 0; i--) {
            writeBit((value >> i) & 1);
        }
    }

    public byte[] toByteArray() {
        int bytesUsed = (bitPos + 7) / 8;
        return Arrays.copyOf(buffer, bytesUsed);
    }
}