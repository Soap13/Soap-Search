package com.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    private static final int MAX_IN_CLAUSE_SIZE = 1000; // Oracle IN子句最大限制

    /**
     * 读取文件并生成SQL IN子句（支持超过1000条）
     * @param filePath 输入文件路径
     * @param fieldName 字段名，默认为f.SOURCE_ID
     * @return 生成的SQL IN子句
     */
    public static String generateSqlInClause(String filePath, String fieldName) {
        List<String> values = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // 假设每行都是字符串值，添加引号
                    values.add("'" + line + "'");
                }
            }
        } catch (IOException e) {
            System.err.println("读取文件时出错: " + e.getMessage());
            return "";
        }

        if (values.isEmpty()) {
            return "";
        }

        // 如果值的数量不超过1000，使用原来的逻辑
        if (values.size() <= MAX_IN_CLAUSE_SIZE) {
            StringBuilder sql = new StringBuilder();
            sql.append(fieldName).append(" IN (");
            sql.append(String.join(", ", values));
            sql.append(")");
            return sql.toString();
        }

        // 如果值的数量超过1000，分组处理
        List<String> clauses = new ArrayList<>();
        for (int i = 0; i < values.size(); i += MAX_IN_CLAUSE_SIZE) {
            int end = Math.min(i + MAX_IN_CLAUSE_SIZE, values.size());
            List<String> subList = values.subList(i, end);

            StringBuilder sql = new StringBuilder();
            sql.append(fieldName).append(" IN (");
            sql.append(String.join(", ", subList));
            sql.append(")");
            clauses.add(sql.toString());
        }

        // 使用OR连接多个IN子句
        return String.join(" OR ", clauses);
    }

    public static void main(String[] args) {
//        if (args.length < 1) {
//            System.out.println("用法: java SqlInGenerator <文件路径> [字段名]");
//            System.out.println("字段名默认为 f.SOURCE_ID");
//            return;
//        }

        String filePath = "C:\\Users\\Soap0\\Desktop\\sszy.txt";
        String fieldName = args.length > 1 ? args[1] : "f.SOURCE_ID";

        String result = generateSqlInClause(filePath, fieldName);
        System.out.println(result);
    }
}
