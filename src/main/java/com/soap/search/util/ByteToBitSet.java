package com.soap.search.util;

/**
 * @author Soap
 * @Date 2025/5/23 10:42
 * @Version 1.0
 */
import java.util.BitSet;

public class ByteToBitSet {
    public static BitSet convert(byte value) {
        BitSet bitSet = new BitSet(8); // 一个 byte 有 8 bits

        for (int i = 0; i < 8; i++) {
            if ((value & (1 << i)) != 0) {
                bitSet.set(i);
            }
        }

        return bitSet;
    }

    public static byte toByte(BitSet bitSet) {
        byte value = 0;
        for (int i = 0; i < 8; i++) {
            if (bitSet.get(i)) {
                value |= (1 << i); // 按位或还原
            }
        }
        return value;
    }

    /**
     * bytes to bitset
     * @param bytes
     * @return
     */
    public static BitSet convert(byte[] bytes) {
        BitSet bitSet = new BitSet(bytes.length * 8);
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < 8; j++) {
                if ((bytes[i] & (1 << j)) != 0) {
                    bitSet.set(i * 8 + j);
                }
            }
        }
        return bitSet;
    }
    public static void main(String[] args) {
        byte b = (byte) 0b10101010; // 示例 byte 值
        BitSet bitSet = convert(b);

        System.out.println("BitSet: " + bitSet); // 输出设置为 true 的位
        System.out.println("所有为1的位索引:");
        bitSet.stream().forEach(System.out::println);
    }
}
