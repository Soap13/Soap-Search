package com.soap.search.algorithm;

/**
 * @author Soap
 * @Date 2025/6/23 17:52
 * @Version 1.0
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PrefixCompression {

    // 保存压缩后的词条：前缀长度 + 后缀
    public static class CompressedTerm {
        public int prefixLength;
        public String suffix;

        public CompressedTerm(int prefixLength, String suffix) {
            this.prefixLength = prefixLength;
            this.suffix = suffix;
        }

        @Override
        public String toString() {
            return "{" + prefixLength + ", " + suffix + "}";
        }
    }

    // 压缩词汇表
    public static List<CompressedTerm> compressTerms(List<String> terms) {
        Collections.sort(terms); // 确保是字典序排列
        List<CompressedTerm> compressedList = new ArrayList<>();
        String prevTerm = "";

        for (String term : terms) {
            int prefixLen = sharedPrefixLength(prevTerm, term);
            String suffix = term.substring(prefixLen);
            compressedList.add(new CompressedTerm(prefixLen, suffix));
            prevTerm = term; // 更新为当前词，供下一轮使用
        }

        return compressedList;
    }

    // 解压缩词汇
    public static List<String> decompressTerms(List<CompressedTerm> compressedList) {
        List<String> originalTerms = new ArrayList<>();
        String prevTerm = "";

        for (CompressedTerm ct : compressedList) {
            int prefixLen = ct.prefixLength;
            String suffix = ct.suffix;
            String currentTerm = prevTerm.substring(0, Math.min(prefixLen, prevTerm.length())) + suffix;
            originalTerms.add(currentTerm);
            prevTerm = currentTerm;
        }

        return originalTerms;
    }

    // 计算两个字符串的最长公共前缀长度
    private static int sharedPrefixLength(String s1, String s2) {
        int len = 0;
        int minLen = Math.min(s1.length(), s2.length());
        while (len < minLen && s1.charAt(len) == s2.charAt(len)) {
            len++;
        }
        return len;
    }

    // 测试主方法
    public static void main(String[] args) {
        List<String> terms = Arrays.asList("apple", "application", "applet", "apply", "banana", "band");

        System.out.println("原始词汇列表：");
        System.out.println(terms);

        List<CompressedTerm> compressed = compressTerms(terms);
        System.out.println("\n压缩后结果（前缀长度 + 后缀）：");
        System.out.println(compressed);

        List<String> restored = decompressTerms(compressed);
        System.out.println("\n解压缩恢复出的词汇列表：");
        System.out.println(restored);
    }
}