package com.soap.search.store;

import com.soap.search.util.NumberUtil;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GolombCodec {
    public static final int M=16;//选择 $M=16$ 或 $32$ 效果较好
    /**
     * 编码：数字数组 -> 字节数组
     * @param numbers 输入的非负整数序列
     * @param m Golomb 参数 (推荐 2 的幂，如 16, 32)
     * @return 压缩后的字节数组
     */
    public static byte[] encode(int[] numbers, int m) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 内部位缓冲区：处理不满 8 位的情况
        int currentByte = 0;
        int bitCount = 0;

        int k = (int) (Math.log(m) / Math.log(2));
        int c = (int) Math.pow(2, k + 1) - m;

        for (int n : numbers) {
            int q = n / m;
            int r = n % m;

            // 1. 写入商 q (一元码：q个0 + 1个1)
            for (int i = 0; i < q; i++) {
                // 写入比特 0
                currentByte <<= 1;
                if (++bitCount == 8) {
                    baos.write(currentByte);
                    currentByte = 0; bitCount = 0;
                }
            }
            // 写入比特 1 (停止位)
            currentByte = (currentByte << 1) | 1;
            if (++bitCount == 8) {
                baos.write(currentByte);
                currentByte = 0; bitCount = 0;
            }

            // 2. 写入余数 r (截断二进制码)
            int rVal = (r < c) ? r : r + c;
            int rBits = (r < c) ? k : k + 1;

            for (int i = rBits - 1; i >= 0; i--) {
                currentByte = (currentByte << 1) | ((rVal >> i) & 1);
                if (++bitCount == 8) {
                    baos.write(currentByte);
                    currentByte = 0; bitCount = 0;
                }
            }
        }

        // 3. 处理末尾补位 (Padding)
        if (bitCount > 0) {
            currentByte <<= (8 - bitCount);
            baos.write(currentByte);
        }

        return baos.toByteArray();
    }

    /**
     * 解码：字节数组 -> 数字列表
     * 不需要预知长度，自动识别末尾补位 0 并停止
     */
    public static List<Integer> decode(byte[] data, int m) {
        List<Integer> result = new ArrayList<>();
        int k = (int) (Math.log(m) / Math.log(2));
        int c = (int) Math.pow(2, k + 1) - m;

        // 位读取状态变量
        int byteIdx = 0;
        int bitIdx = 7; // 从最高位开始读

        while (byteIdx < data.length) {
            // --- 1. 解码商 q ---
            int q = 0;
            boolean foundStopBit = false;

            while (byteIdx < data.length) {
                int bit = (data[byteIdx] >> bitIdx) & 1;
                // 更新指针
                if (--bitIdx < 0) { bitIdx = 7; byteIdx++; }

                if (bit == 1) {
                    foundStopBit = true;
                    break;
                }
                q++;
            }

            // 如果没搜到 1 就撞到了数据末尾，说明剩下的全是补位 0
            if (!foundStopBit) break;

            // --- 2. 解码余数 r ---
            // 先读 k 位
            int v = 0;
            for (int i = 0; i < k; i++) {
                if (byteIdx >= data.length) break;
                int bit = (data[byteIdx] >> bitIdx) & 1;
                v = (v << 1) | bit;
                if (--bitIdx < 0) { bitIdx = 7; byteIdx++; }
            }

            int r;
            if (v < c) {
                r = v;
            } else {
                // 需要再读 1 位
                if (byteIdx >= data.length) break;
                int extraBit = (data[byteIdx] >> bitIdx) & 1;
                r = ((v << 1) | extraBit) - c;
                if (--bitIdx < 0) { bitIdx = 7; byteIdx++; }
            }

            result.add(q * m + r);
        }

        return result;
    }
    // --- 解码部分：不指定长度读取 ---
    public int readGolombInt(int m) throws IOException {
        int k = (int) (Math.log(m) / Math.log(2));
        int c = (int) Math.pow(2, k + 1) - m;

        // 1. 解码商 q
        int q = 0;
        int bit = readBit();

        // 核心逻辑：如果在寻找 1 的过程中读到了流末尾 (-1)
        // 说明之前的 0 全是补位，没有后续数据了
        while (bit == 0) {
            q++;
            bit = readBit();
        }

        // 如果 bit 为 -1，说明这串 0 后面没有 1，是无效数据
        if (bit == -1) return -1;

        // 2. 解码余数 r
        int v = readBits(k);
        if (v == -1) return -1;

        int r;
        if (v < c) {
            r = v;
        } else {
            int nextBit = readBit();
            if (nextBit == -1) return -1;
            r = ((v << 1) | nextBit) - c;
        }

        return q * m + r;
    }

    private int currentByte;
    private int bitsLeft = 0;
    private IndexInput in;
    public int readBit() throws IOException {
        if (bitsLeft == 0) {
            currentByte = in.readByte();
            if (currentByte == -1) return -1;
            bitsLeft = 8;
        }
        bitsLeft--;
        return (currentByte >> bitsLeft) & 1;
    }

    public int readBits(int n) throws IOException {
        int val = 0;
        for (int i = 0; i < n; i++) {
            int b = readBit();
            if (b == -1) return -1;
            val = (val << 1) | b;
        }
        return val;
    }

    // --- 测试入口 ---
    public static void main(String[] args) {
        int[] original = {92983,
                223590,
                265727,
                265733,
                273498,
                359875,
                378328,
                511699,
                511817,
                586545,
                587665,
                595195,
                655757,
                787989,
                853898,
                855234,
                868758,
                868770,
                945664,
                1019222
        };
        // 对于这组数据，平均差值约为 50,000，取 M = 32768 (2^15) 比较合适
        int m = 32768;

        m=NumberUtil.findBestM(Arrays.copyOfRange(original, 1, original.length));
        // original=new int[]{1085419, 1095077};
        original=NumberUtil.calculateDifferencesToArray( Arrays.stream(original)
                .boxed()
                .collect(Collectors.toList()));
        int[] result = Arrays.copyOfRange(original, 1, original.length);
        System.out.println("M: " + m);
        byte[] encoded = encode(NumberUtil.calculateDifferencesToArray( Arrays.stream(result)
                .boxed()
                .collect(Collectors.toList())), m);
        System.out.println("Encoded bytes: " + encoded.length);

        List<Integer> decoded = decode(encoded, m);
        System.out.println("Decoded numbers: " + decoded);
    }
}