package com.soap.search.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * IK分词器
 * @author Soap
 * @Date 2025/5/22 14:32
 * @Version 1.0
 */
public class IKUtil {
    private static final Logger Log = LogManager.getLogger(IKUtil.class);
    //构建IK分词器，使用smart分词模式
    private static final Analyzer analyzer = new IKAnalyzer(true);
    private IKUtil() {}
    public static Map<String,ArrayList<Integer>> IDAnalyzer(String str) {
        Map<String,ArrayList<Integer>>termMap=new HashMap<String, ArrayList<Integer>>();
        //构建IK分词器，使用smart分词模式
        //Analyzer analyzer = new IKAnalyzer(true);
        //获取Lucene的TokenStream对象
        TokenStream ts = null;
        try {
            ts = analyzer.tokenStream("",
                    new StringReader(str));
            //获取词元位置属性
            OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
            //获取词元文本属性
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            //获取词元文本属性
            TypeAttribute type = ts.addAttribute(TypeAttribute.class);

            //重置TokenStream（重置StringReader）
            ts.reset();
            //迭代获取分词结果
            while (ts.incrementToken()) {
                //System.out.println(offset.startOffset() + " - " + offset.endOffset() + " : " + term.toString() + " | " + type.type());
                if(termMap.containsKey(term.toString())){
                    ArrayList<Integer>list=termMap.get(term.toString());
                    list.add(offset.startOffset());
                }else{
                    ArrayList<Integer>list=new ArrayList<Integer>();
                    list.add(offset.startOffset());
                    termMap.put(term.toString(),list);
                }
            }
            //关闭TokenStream（关闭StringReader）
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } catch (IOException e) {
            Log.error(e.getMessage(),e);
        } finally {
            //释放TokenStream的所有资源
            if (ts != null) {
                try {
                    ts.close();
                } catch (IOException e) {
                    Log.error(e.getMessage(),e);
                }
            }
        }
        return termMap;
    }

    /**
     * 用于搜索切词的结果
     * @param str
     * @return
     */
    public static Set<String> strAnalyzer(String str) {
        Set<String> result=new HashSet<>();
        //构建IK分词器，使用smart分词模式
        //Analyzer analyzer = new IKAnalyzer(true);
        //获取Lucene的TokenStream对象
        TokenStream ts = null;
        try {
            ts = analyzer.tokenStream("",
                    new StringReader(str));
            //获取词元文本属性
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            //重置TokenStream（重置StringReader）
            ts.reset();
            //迭代获取分词结果
            while (ts.incrementToken()) {
                result.add(term.toString());
            }
            //关闭TokenStream（关闭StringReader）
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } catch (IOException e) {
            Log.error(e.getMessage(),e);
        } finally {
            //释放TokenStream的所有资源
            if (ts != null) {
                try {
                    ts.close();
                } catch (IOException e) {
                    Log.error(e.getMessage(),e);
                }
            }
        }
        return result;
    }
}
