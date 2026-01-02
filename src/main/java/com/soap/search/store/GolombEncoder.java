package com.soap.search.store;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class GolombEncoder {
    private final int m, k, c;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    // 关键状态：记录当前字节中还没写满的位
    private int currentByte = 0;
    private int bitCount = 0;

    public GolombEncoder(int m) {
        this.m = m;
        // 预计算参数以提升性能
        this.k = (int) (Math.log(m) / Math.log(2));
        this.c = (int) Math.pow(2, k + 1) - m;
    }

    /**
     * 编码单个数字：将数字的比特流追加到内部内存缓冲区
     */
    public void encode(int n) {
        int q = n / m;
        int r = n % m;

        // 1. 写入商 (Unary编码: q个0 + 1个1)
        for (int i = 0; i < q; i++) {
            appendBit(0);
        }
        appendBit(1);

        // 2. 写入余数 (Truncated Binary编码)
        int rVal, rLen;
        if (r < c) {
            rVal = r;
            rLen = k;
        } else {
            rVal = r + c;
            rLen = k + 1;
        }

        // 按位写入余数（从高位到低位）
        for (int i = rLen - 1; i >= 0; i--) {
            appendBit((rVal >> i) & 1);
        }
    }

    /**
     * 内部方法：操作位并按需存入字节数组
     */
    private void appendBit(int bit) {
        currentByte = (currentByte << 1) | (bit & 1);
        bitCount++;

        if (bitCount == 8) {
            buffer.write(currentByte);
            currentByte = 0;
            bitCount = 0;
        }
    }

    /**
     * 获取最终的字节数组
     * 此时会处理不满 8 位的补位
     */
    public byte[] toByteArray() {
        byte[] completedBytes = buffer.toByteArray();

        // 如果还有残余的位没凑够 1 字节，需要最后补齐并多加一个字节
        if (bitCount > 0) {
            byte[] result = Arrays.copyOf(completedBytes, completedBytes.length + 1);
            result[result.length - 1] = (byte) (currentByte << (8 - bitCount));
            return result;
        }

        return completedBytes;
    }

    public static void main(String[] args) {
        GolombEncoder encoder = new GolombEncoder(16);

        // 随时追加数字
        encoder.encode(10);
        encoder.encode(25);
        encoder.encode(3);

        byte[] result = encoder.toByteArray();
        System.out.println("编码完成，总字节数: " + result.length);
        // 配合之前的 Decoder 即可实现逐个数字解码
        List<Integer> decoded = GolombCodec.decode(result, 16);
        System.out.println("解码结果: " + decoded);
    }
}