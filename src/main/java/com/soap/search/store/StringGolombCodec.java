package com.soap.search.store;

import java.util.stream.Collectors;

public class StringGolombCodec {

    public static final int M=16;//选择 $M=16$ 或 $32$ 效果较好
    /**
     * 对整个字符串进行 Golomb 编码
     * @param text 输入字符串
     * @param m 参数 M
     * @return 01 比特流字符串
     */
    public static String encodeString(String text, int m) {
        StringBuilder bitStream = new StringBuilder();
        for (char c : text.toCharArray()) {
            bitStream.append(encodeInt((int) c, m));
        }
        return bitStream.toString();
    }

    /**
     * 对比特流进行解码，还原字符串
     */
    public static String decodeString(String bitStream, int m) {
        StringBuilder result = new StringBuilder();
        int[] pos = {0}; // 使用数组传递指针位置，模拟引用传递

        while (pos[0] < bitStream.length()) {
            int value = decodeInt(bitStream, m, pos);
            result.append((char) value);
        }
        return result.toString();
    }

    // --- 底层整数编解码逻辑 ---

    private static String encodeInt(int n, int m) {
        StringBuilder sb = new StringBuilder();
        int q = n / m;
        int r = n % m;

        // 1. 商的编码 (Unary: q个0 + 1个1)
        for (int i = 0; i < q; i++) sb.append('0');
        sb.append('1');

        // 2. 余数的编码 (Truncated Binary)
        int k = (int) (Math.log(m) / Math.log(2));
        int c = (int) Math.pow(2, k + 1) - m;

        if (r < c) {
            sb.append(padLeft(Integer.toBinaryString(r), k));
        } else {
            sb.append(padLeft(Integer.toBinaryString(r + c), k + 1));
        }
        return sb.toString();
    }

    private static int decodeInt(String bits, int m, int[] pos) {
        // 1. 解码商 q
        int q = 0;
        while (pos[0] < bits.length() && bits.charAt(pos[0]) == '0') {
            q++;
            pos[0]++;
        }
        pos[0]++; // 跳过 '1'

        // 2. 解码余数 r
        int k = (int) (Math.log(m) / Math.log(2));
        int c = (int) Math.pow(2, k + 1) - m;

        // 读取 k 位
        int firstKBits = Integer.parseInt(bits.substring(pos[0], pos[0] + k), 2);
        pos[0] += k;

        if (firstKBits < c) {
            return q * m + firstKBits;
        } else {
            // 再多读 1 位
            int nextBit = bits.charAt(pos[0]) - '0';
            pos[0]++;
            int totalBits = (firstKBits << 1) | nextBit;
            return q * m + (totalBits - c);
        }
    }

    // 工具函数：二进制补齐
    private static String padLeft(String s, int len) {
        if (len <= 0) return "";
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < len) sb.insert(0, '0');
        return sb.toString();
    }

    public static void main(String[] args) {
        String original = "Hello! 123";
        int m = 16; // 选取适合字符范围的 M 值

        String encoded = encodeString(original, m);
        System.out.println("原始字符串: " + original);
        System.out.println("M 参数: " + m);
        System.out.println("编码后长度: " + encoded.length() + " bits");
        System.out.println("编码比特流: " + encoded);

        String decoded = decodeString(encoded, m);
        System.out.println("解码后字符串: " + decoded);
    }
}