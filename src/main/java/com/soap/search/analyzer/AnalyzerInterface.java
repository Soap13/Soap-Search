package com.soap.search.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public interface AnalyzerInterface {
    /**
     * key 词
     * value 词出现的位置
     * @param text
     * @return
     * @throws IOException
     */
    public Map<String, ArrayList<Integer>> StringAnalyzer(String text) throws IOException;

    /**
     * 用于搜索切词的结果
     * @param str
     * @return
     */
    public  Set<String> strAnalyzer(String str) throws IOException;
}
