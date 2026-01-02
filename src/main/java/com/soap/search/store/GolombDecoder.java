package com.soap.search.store;

public class GolombDecoder {
    private final byte[] data;
    private final int m, k, c;

    // 指针状态：当前读取到第几个字节，以及该字节内的第几位
    private int byteIdx = 0;
    private int bitIdx = 7; // 从最高位 (MSB) 开始读

    public GolombDecoder(byte[] data, int m) {
        this.data = data;
        this.m = m;
        // 预计算参数
        this.k = (int) (Math.log(m) / Math.log(2));
        this.c = (int) Math.pow(2, k + 1) - m;
    }

    /**
     * 解码单个数字
     * @return 返回解码出的整数。如果已经到达字节流末尾且无法构成有效数字，返回 -1
     */
    public int decode() {
        // 1. 解码商 q (Unary 编码识别)
        int q = 0;
        boolean stopBitFound = false;

        while (hasMoreBits()) {
            int bit = readBit();
            if (bit == 1) {
                stopBitFound = true;
                break;
            }
            q++;
        }

        // 如果还没读到停止位 1 就没比特了，说明剩下的全是补位 0
        if (!stopBitFound) {
            return -1;
        }

        // 2. 解码余数 r (截断二进制编码识别)
        int v = 0;
        for (int i = 0; i < k; i++) {
            if (!hasMoreBits()) return -1;
            v = (v << 1) | readBit();
        }

        int r;
        if (v < c) {
            r = v;
        } else {
            // 需要再多读一位
            if (!hasMoreBits()) return -1;
            int extraBit = readBit();
            r = ((v << 1) | extraBit) - c;
        }

        return q * m + r;
    }

    /**
     * 读取一个比特并移动指针
     */
    private int readBit() {
        int bit = (data[byteIdx] >> bitIdx) & 1;

        bitIdx--;
        if (bitIdx < 0) {
            bitIdx = 7;
            byteIdx++;
        }
        return bit;
    }

    /**
     * 检查是否还有剩余比特
     */
    private boolean hasMoreBits() {
        return byteIdx < data.length;
    }

    // --- 测试代码 ---
    public static void main(String[] args) {
        // 假设这是通过 GolombEncoder 编码出来的字节数组
        // 这里手动模拟一个编码后的 byte[] (M=16, 编码了数字 10 和 25)
        byte[] testData = { (byte) 0b11010011, (byte) 0b00100000 };
        int m = 16;

        GolombDecoder decoder = new GolombDecoder(testData, m);

        int val;
        while ((val = decoder.decode()) != -1) {
            System.out.println("解码出数字: " + val);
        }
    }
}