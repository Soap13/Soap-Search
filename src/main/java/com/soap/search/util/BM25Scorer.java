package com.soap.search.util;

/**
 * @author Soap
 * @Date 2025/5/24 8:52
 * @Version 1.0
 */
import java.util.*;

public class BM25Scorer {

    private static final double k1 = 1.5;
    private static final double b = 0.75;

    /**
     * 计算 BM25 分数
     *
     * @param termFrequency      查询词在当前文档中的出现次数 (tf)
     * @param docLength          当前文档的总词数 (length of the document)
     * @param avgDocLength       平均文档长度
     * @param docsWithTermCount  包含该查询词的文档数量
     * @param totalDocs          文档总数
     * @return BM25 score
     */
    public static double computeScore(int termFrequency, int docLength,
                                      double avgDocLength, int docsWithTermCount,
                                      int totalDocs) {
        double idf = Math.log(1 + (double)(totalDocs - docsWithTermCount + 0.5) / (docsWithTermCount + 0.5));
        double numerator = termFrequency * (k1 + 1);
        double denominator = termFrequency + k1 * (1 - b + b * docLength / avgDocLength);
        return idf * numerator / denominator;
    }

    /**
     * 批量计算多个文档的 BM25 分数
     *
     * @param queryTerm         查询词
     * @param docs              所有文档（每个文档是词频 Map）
     * @param docsWithTermCount 包含该查询词的文档数量
     * @param totalDocs         总文档数
     * @return 每个文档的 BM25 分数
     */
    public static Map<String, Double> scoreDocuments(String queryTerm,
                                                     Map<String, Map<String, Integer>> docs,
                                                     int docsWithTermCount,
                                                     int totalDocs) {
        Map<String, Double> scores = new HashMap<>();
        double avgDocLength = calculateAvgDocLength(docs);

        for (Map.Entry<String, Map<String, Integer>> entry : docs.entrySet()) {
            String docId = entry.getKey();
            Map<String, Integer> termFreqs = entry.getValue();
            int tf = termFreqs.getOrDefault(queryTerm, 0);
            int docLength = termFreqs.values().stream().mapToInt(Integer::intValue).sum();

            double score = computeScore(tf, docLength, avgDocLength, docsWithTermCount, totalDocs);
            scores.put(docId, score);
        }

        return scores;
    }

    private static double calculateAvgDocLength(Map<String, Map<String, Integer>> docs) {
        int totalLength = 0;
        for (Map<String, Integer> termFreqs : docs.values()) {
            totalLength += termFreqs.values().stream().mapToInt(Integer::intValue).sum();
        }
        return (double) totalLength / docs.size();
    }

    // 示例使用
    public static void main(String[] args) {
        Map<String, Map<String, Integer>> documents = new HashMap<>();

        // 构造三个示例文档
        documents.put("doc1", Map.of("bm25", 3, "search", 2, "ranking", 1));
        documents.put("doc2", Map.of("bm25", 1, "algorithm", 2, "model", 1));
        documents.put("doc3", Map.of("bm25", 5, "information", 1, "retrieval", 2));

        int docsWithBm25 = 3; // 所有文档都包含 "bm25"
        int totalDocs = 3;

        Map<String, Double> scores = scoreDocuments("bm25", documents, docsWithBm25, totalDocs);

        scores.forEach((docId, score) -> System.out.println(docId + ": " + score));
    }
}
