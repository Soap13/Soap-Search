package com.test;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * 基于文件的列存储实现
 */
public class FileColumnStore {
    private String basePath;

    public FileColumnStore(String basePath) {
        this.basePath = basePath;
        new File(basePath).mkdirs();
    }

    // 将列数据写入文件
    public void writeColumn(String columnName, List<Object> data) throws IOException {
        String filePath = basePath + "/" + columnName + ".col";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
        }
    }

    // 从文件读取列数据
    @SuppressWarnings("unchecked")
    public List<Object> readColumn(String columnName) throws IOException, ClassNotFoundException {
        String filePath = basePath + "/" + columnName + ".col";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<Object>) ois.readObject();
        }
    }

    // 演示文件存储
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileColumnStore store = new FileColumnStore("./column_store");

        // 模拟数据
        List<Object> ids = Arrays.asList(1, 2, 3, 4, 5);
        List<Object> names = Arrays.asList("张三", "李四", "王五", "赵六", "钱七");
        List<Object> ages = Arrays.asList(25, 30, 28, 35, 27);

        // 写入列数据
        store.writeColumn("id", ids);
        store.writeColumn("name", names);
        store.writeColumn("age", ages);

        // 读取列数据
        List<Object> readIds = store.readColumn("id");
        List<Object> readNames = store.readColumn("name");
        List<Object> readAges = store.readColumn("age");

        System.out.println("ID列: " + readIds);
        System.out.println("姓名列: " + readNames);
        System.out.println("年龄列: " + readAges);
    }
}
