package com.soap.search.util;

import java.util.ArrayList;
import java.util.List;

public class NumberUtil {

    /**
     * 将列表中的每个数转换为减去前一个数的差值
     * 第一个数减去0
     * @param list 输入的整数列表
     * @return 转换后的列表
     */
    public static List<Integer> calculateDifferences(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> result = new ArrayList<>(list.size());
        int previous = 0;

        for (Integer current : list) {
            int difference = current - previous;
            result.add(difference);
            previous = current;
        }

        return result;
    }

    /**
     * 将 List<Integer> 转换为 int[]，每个元素为原数减去前一个数的差值
     * @param list 输入的整数列表
     * @return 转换后的整数数组
     */
    public static int[] calculateDifferencesToArray(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return new int[0];
        }

        int[] result = new int[list.size()];
        int previous = 0;

        for (int i = 0; i < list.size(); i++) {
            int current = list.get(i);
            result[i] = current - previous;
            previous = current;
        }

        return result;
    }

    /**
     * 将差值列表还原为原始数值列表
     * 每个数等于前一个数加上当前数，第一个数加0
     * @param list 差值列表
     * @return 还原后的原始数值列表
     */
    public static List<Integer> restoreFromDifferences(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> result = new ArrayList<>(list.size());
        int current = 0;

        for (Integer difference : list) {
            current = current + difference;
            result.add(current);
        }

        return result;
    }

    /**
     * 将差值数组还原为原始数值数组
     * 每个数等于前一个数加上当前数，第一个数加0
     * @param array 差值数组
     * @return 还原后的原始数值数组
     */
    public static int[] restoreFromDifferencesArray(int[] array) {
        if (array == null || array.length == 0) {
            return new int[0];
        }

        int[] result = new int[array.length];
        int current = 0;

        for (int i = 0; i < array.length; i++) {
            current = current + array[i];
            result[i] = current;
        }

        return result;
    }


    /**
     * 根据递增数组计算最优的 M
     * @param original 递增的 DocID 数组
     * @return 最优参数 M
     */
    public static int estimateM(int[] original) {
        if (original.length == 0) return 16;
        if (original.length == 1) {
            // 如果只有一个数，让 M 等于这个数向上取最近的 2 的幂
            // 这样商 q=0 (1 bit)，余数占 log2(M) 位，总长度最接近原始位宽
            return nextPowerOfTwo(original[0]);
        }

        // 1. 计算平均差值 (Mean of Deltas)
        // 最后一个数减去第一个数，再除以间隔数，就是平均差值
        double totalDelta = original[original.length - 1] - original[0];
        double avgDelta = totalDelta / (original.length - 1);

        // 2. 使用近似公式 M ≈ 0.69 * Mean
        double optimalM = 0.69 * avgDelta;

        // 3. 向上取最接近的 2 的幂 (可选，但强烈建议)
        // 这样可以使 Golomb 编码退化为 Rice 编码，位运算速度极快
        return nextPowerOfTwo((int) Math.round(optimalM));
    }

    /**
     * 寻找到大于等于 n 的最近的 2 的幂
     */
    private static int nextPowerOfTwo2(int n) {
        if (n <= 1) return 1;
        int p = 1;
        while (p < n) {
            p <<= 1;
        }
        return p;
    }

    /**
     * 返回大于等于 n 且最接近 n 的 2 的幂
     */
    public static int nextPowerOfTwo(int n) {
        if (n <= 0) return 1;
        n--; // 防止 n 本身就是 2 的幂
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return n + 1;
    }

    public static int estimateSmartM(int[] original) {
        // 1. 先算平均差值
        long avgDelta = (original[original.length-1] - original[0]) / original.length;

        // 2. 经验公式：M 取 2 的幂，且尽量覆盖平均差值的 60%~100%
        int m = nextPowerOfTwo((int)(avgDelta * 0.69));

        // 3. 针对小数组，不要让 M 太小导致商溢出
        return Math.max(m, 1024);
    }

    /**
     * 通过模拟编码，寻找能让总长度最短的最优 M 值
     */
    public static int findBestM(int[] original) {
        if (original == null || original.length < 2) return 16;

        // 1. 准备差值序列
        int[] deltas = new int[original.length - 1];
        for (int i = 0; i < deltas.length; i++) {
            deltas[i] = original[i + 1] - original[i];
        }

        // 2. 确定搜索范围：从平均差值的 0.5 倍到 1.5 倍
        double avgDelta = 0;
        for (int d : deltas) avgDelta += d;
        avgDelta /= deltas.length;

        // 3. 候选集：测试所有 2 的幂（Rice 编码优化）
        // 范围从 2^1 (2) 到 2^24 (16,777,216)
        int bestM = 16;
        long minTotalBits = Long.MAX_VALUE;

        for (int k = 1; k <= 24; k++) {
            int currentM = 1 << k;
            long currentBits = calculateTotalBits(deltas, currentM);

            if (currentBits < minTotalBits) {
                minTotalBits = currentBits;
                bestM = currentM;
            }
        }

        return bestM;
    }

    /**
     * 通过模拟编码，寻找能让总长度最短的最优 M 值
     */
    public static int findBestM(List<Integer> original) {
        if (original == null || original.size() < 2) return 16;

        // 1. 准备差值序列
        int[] deltas = new int[original.size() - 1];
        for (int i = 0; i < deltas.length; i++) {
            deltas[i] = original.get(i + 1) - original.get(i);
        }

        // 2. 确定搜索范围：从平均差值的 0.5 倍到 1.5 倍
        double avgDelta = 0;
        for (int d : deltas) avgDelta += d;
        avgDelta /= deltas.length;

        // 3. 候选集：测试所有 2 的幂（Rice 编码优化）
        // 范围从 2^1 (2) 到 2^24 (16,777,216)
        int bestM = 16;
        long minTotalBits = Long.MAX_VALUE;

        for (int k = 1; k <= 24; k++) {
            int currentM = 1 << k;
            long currentBits = calculateTotalBits(deltas, currentM);

            if (currentBits < minTotalBits) {
                minTotalBits = currentBits;
                bestM = currentM;
            }
        }

        return bestM;
    }

    /**
     * 核心估算逻辑：计算在给定 M 下，编码所有差值需要的比特总数
     */
    private static long calculateTotalBits(int[] deltas, int m) {
        int k = (int) (Math.log(m) / Math.log(2));
        long totalBits = 0;

        for (int d : deltas) {
            int q = d / m;
            // 商的比特数: q 个 0 + 1 个 1
            int qBits = q + 1;
            // 余数的比特数: 对于 2 的幂，固定为 k 位
            int rBits = k;

            totalBits += (qBits + rBits);
        }
        return totalBits;
    }
    public static void main(String[] args) {
        int[] original = {92983, 223590, 265727, 265733, 273498, 359875, 378328, 511699,
                511817, 586545, 587665, 595195, 655757, 787989, 853898, 855234,
                868758, 868770, 945664, 1019222};

        int m = findBestM(original);
        System.out.println("平均差值约为: " + (original[original.length-1] - original[0]) / (original.length-1));
        System.out.println("推荐的最优 M 值: " + m);
    }
}
