package com.soap.search.store;

public class GolombByteCodec {
    public static final int M=16;//选择 $M=16$ 或 $32$ 效果较好
    /**
     * 对字符串进行编码
     * 逻辑：String -> ASCII/Unicode Int -> Golomb Bits -> byte[]
     */
    public static byte[] encodeString(String text, int m) {
        BitWriter writer = new BitWriter(text.length()); // 预估空间
        int k = (int) (Math.log(m) / Math.log(2));
        int c = (int) Math.pow(2, k + 1) - m;

        for (char ch : text.toCharArray()) {
            int n = (int) ch;
            int q = n / m;
            int r = n % m;

            // 1. 编码商 q (Unary 编码: q个0 + 1个1)
            for (int i = 0; i < q; i++) writer.writeBit(0);
            writer.writeBit(1);

            // 2. 编码余数 r (Truncated Binary 编码)
            if (r < c) {
                writer.writeBits(r, k);
            } else {
                writer.writeBits(r + c, k + 1);
            }
        }
        return writer.toByteArray();
    }

    /**
     * 对字节数组进行解码
     * @param encodedData 编码后的字节数组
     * @param m 参数 M
     * @param expectedLen 期望恢复的字符长度（或者直到比特流结束）
     */
    public static String decodeString(byte[] encodedData, int m, int expectedLen) {
        BitReader reader = new BitReader(encodedData);
        StringBuilder sb = new StringBuilder();
        int k = (int) (Math.log(m) / Math.log(2));
        int c = (int) Math.pow(2, k + 1) - m;

        for (int i = 0; i < expectedLen; i++) {
            // 1. 解码商 q
            int q = 0;
            int bit;
            while ((bit = reader.readBit()) == 0) {
                q++;
            }
            if (bit == -1) break; // 流结束

            // 2. 解码余数 r
            int r;
            int firstKBits = reader.readBits(k);
            if (firstKBits < c) {
                r = firstKBits;
            } else {
                int nextBit = reader.readBit();
                r = ((firstKBits << 1) | nextBit) - c;
            }

            sb.append((char) (q * m + r));
        }
        return sb.toString();
    }

    /**
     * 无需长度参数的解码
     * 逻辑：通过判断剩余比特是否能构成最基本的编码（商的1位停止位 + 余数的k位）来停止
     */
    public static String decodeString(byte[] encodedData, int m) {
        BitReader reader = new BitReader(encodedData);
        StringBuilder sb = new StringBuilder();

        int k = (int) (Math.log(m) / Math.log(2));
        int c = (int) Math.pow(2, k + 1) - m;

        // 只要剩余比特可能包含一个最小的字符编码就继续
        // 最小编码长度通常是 1(商停止位) + k(余数位)
        while (reader.remaining() >= (1 + k)) {
            // 1. 解码商 q
            int q = 0;
            int bit = reader.readBit();

            // 如果读到末尾全是0导致的补位，这里会一直读到-1
            while (bit == 0) {
                q++;
                bit = reader.readBit();
            }

            // 遇到末尾补位产生的全0，bit会变成-1，此时直接退出
            if (bit == -1) break;

            // 2. 解码余数 r
            int r;
            int firstKBits = reader.readBits(k);
            if (firstKBits == -1) break; // 剩余位不足以凑齐余数

            if (firstKBits < c) {
                r = firstKBits;
            } else {
                if (reader.remaining() < 1) break; // 还需要读一位，但没了
                int nextBit = reader.readBit();
                r = ((firstKBits << 1) | nextBit) - c;
            }

            sb.append((char) (q * m + r));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String input = "Golomb 123";
        int m = 16; // 建议选 2 的幂以优化性能

        // 编码
        byte[] compressed = encodeString(input, m);

        System.out.println("原始字符串: " + input);
        System.out.println("压缩后字节数: " + compressed.length + " bytes");
        System.out.print("十六进制内容: ");
        for (byte b : compressed) System.out.printf("%02X ", b);
        System.out.println();

        // 解码
        String output = decodeString(compressed, m, input.length());
        String output2 = decodeString(compressed, m);
        System.out.println("解码后字符串: " + output2);
    }
}