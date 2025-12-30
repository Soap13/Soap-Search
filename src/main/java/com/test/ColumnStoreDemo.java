package com.test;

import java.io.*;
import java.util.*;

/**
 * 简单的列存储实现示例
 */
public class ColumnStoreDemo {

    // 列存储表结构
    static class ColumnTable {
        private Map<String, List<Object>> columns = new HashMap<>();
        private int rowCount = 0;

        // 添加列
        public void addColumn(String columnName) {
            if (!columns.containsKey(columnName)) {
                columns.put(columnName, new ArrayList<>());
            }
        }

        // 添加行数据
        public void addRow(Map<String, Object> row) {
            for (String colName : columns.keySet()) {
                Object value = row.getOrDefault(colName, null);
                columns.get(colName).add(value);
            }
            rowCount++;
        }

        // 获取指定列的所有数据
        public List<Object> getColumn(String columnName) {
            return columns.get(columnName);
        }

        // 获取指定行的数据
        public Map<String, Object> getRow(int rowIndex) {
            if (rowIndex >= rowCount) return null;

            Map<String, Object> row = new HashMap<>();
            for (String colName : columns.keySet()) {
                List<Object> colData = columns.get(colName);
                if (rowIndex < colData.size()) {
                    row.put(colName, colData.get(rowIndex));
                }
            }
            return row;
        }

        // 获取列数
        public int getColumnCount() {
            return columns.size();
        }

        // 获取行数
        public int getRowCount() {
            return rowCount;
        }

        // 打印表内容
        public void printTable() {
            System.out.println("Column Store Table:");
            System.out.println("Rows: " + rowCount + ", Columns: " + columns.size());

            // 打印列名
            System.out.print("Index\t");
            for (String colName : columns.keySet()) {
                System.out.print(colName + "\t");
            }
            System.out.println();

            // 打印数据
            for (int i = 0; i < rowCount; i++) {
                System.out.print(i + "\t");
                for (String colName : columns.keySet()) {
                    List<Object> colData = columns.get(colName);
                    System.out.print(colData.get(i) + "\t");
                }
                System.out.println();
            }
        }
    }

    // 演示方法
    public static void main(String[] args) {
        ColumnStoreDemo demo = new ColumnStoreDemo();
        demo.runDemo();
    }

    public void runDemo() {
        // 创建列存储表
        ColumnTable table = new ColumnStoreDemo.ColumnTable();

        // 定义列
        table.addColumn("id");
        table.addColumn("name");
        table.addColumn("age");
        table.addColumn("salary");

        // 添加数据行
        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", 1);
        row1.put("name", "张三");
        row1.put("age", 25);
        row1.put("salary", 5000.0);
        table.addRow(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("id", 2);
        row2.put("name", "李四");
        row2.put("age", 30);
        row2.put("salary", 8000.0);
        table.addRow(row2);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("id", 3);
        row3.put("name", "王五");
        row3.put("age", 28);
        row3.put("salary", 7000.0);
        table.addRow(row3);

        // 打印整个表
        table.printTable();

        System.out.println("\n--- 查询操作 ---");

        // 查询特定列
        System.out.println("年龄列: " + table.getColumn("age"));
        System.out.println("薪资列: " + table.getColumn("salary"));

        // 查询特定行
        System.out.println("第2行: " + table.getRow(1));

        System.out.println("\n--- 性能优势演示 ---");
        // 列存储优势：快速聚合计算
        List<Object> salaries = table.getColumn("salary");
        double totalSalary = 0;
        for (Object salary : salaries) {
            totalSalary += (Double) salary;
        }
        System.out.println("总薪资: " + totalSalary);
        System.out.println("平均薪资: " + totalSalary / salaries.size());
    }
}
