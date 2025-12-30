package com.soap.algorithm;

/**
 * @author Soap
 * @Date 2025/6/23 17:06
 * @Version 1.0
 */
import java.util.*;

class HuffmanNode {
    char data;
    int frequency;
    HuffmanNode left, right;

    public HuffmanNode(char data, int frequency) {
        this.data = data;
        this.frequency = frequency;
        left = right = null;
    }

    // 判断是否是叶子节点
    public boolean isLeaf() {
        return left == null && right == null;
    }
}

public class HuffmanCoding {

    private static Map<Character, Integer> buildFrequencyTable(String text) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }

    private static HuffmanNode buildHuffmanTree(Map<Character, Integer> frequency) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.frequency));

        for (Map.Entry<Character, Integer> entry : frequency.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();

            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;

            pq.add(parent);
        }

        return pq.poll(); // 根节点
    }

    private static void buildCodes(Map<Character, String> huffCode, HuffmanNode node, String code) {
        if (node != null) {
            if (node.isLeaf()) {
                huffCode.put(node.data, code);
            }
            buildCodes(huffCode, node.left, code + "0");
            buildCodes(huffCode, node.right, code + "1");
        }
    }

    private static String encodeText(String text, Map<Character, String> huffCode) {
        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(huffCode.get(c));
        }
        return encoded.toString();
    }

    private static String decodeText(HuffmanNode root, String encodedStr) {
        StringBuilder decoded = new StringBuilder();
        HuffmanNode current = root;

        for (int i = 0; i < encodedStr.length(); i++) {
            char bit = encodedStr.charAt(i);
            current = (bit == '0') ? current.left : current.right;

            if (current.isLeaf()) {
                decoded.append(current.data);
                current = root; // 回到根节点，继续解码下一个字符
            }
        }

        return decoded.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要压缩的文本：");
        String input = scanner.nextLine();

        // Step 1: 构建频率表
        Map<Character, Integer> frequency = buildFrequencyTable(input);

        // Step 2: 构建霍夫曼树
        HuffmanNode root = buildHuffmanTree(frequency);

        // Step 3: 生成霍夫曼编码
        Map<Character, String> huffCode = new HashMap<>();
        buildCodes(huffCode, root, "");

        // Step 4: 打印每个字符的编码
        System.out.println("\n字符对应的霍夫曼编码：");
        for (Map.Entry<Character, String> entry : huffCode.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        // Step 5: 编码原始文本
        String encoded = encodeText(input, huffCode);
        System.out.println("\n编码后的二进制字符串：\n" + encoded);

        // Step 6: 解码
        String decoded = decodeText(root, encoded);
        System.out.println("\n解码后的原始文本：\n" + decoded);

        // Step 7: 比较压缩率
        double originalBits = input.length() * 8;
        double encodedBits = encoded.length();
        double compressionRatio = (originalBits - encodedBits) / originalBits * 100;

        System.out.printf("\n压缩率：%.2f%%\n", compressionRatio);
    }
}