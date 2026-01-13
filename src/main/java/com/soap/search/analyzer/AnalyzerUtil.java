package com.soap.search.analyzer;

import com.soap.search.conf.ConfUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class AnalyzerUtil {
    private static final Logger Log = LogManager.getLogger(AnalyzerUtil.class);
    private static AnalyzerInterface analyzer;
    private static void  initAnalyzer() throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(analyzer == null){
            synchronized (AnalyzerUtil.class){
                if(analyzer == null){
                    String analyzerName = ConfUtil.getProperty("analyzer.class");
                    Class<?> clazz = Class.forName(analyzerName);
                    analyzer= (AnalyzerInterface) clazz.getDeclaredConstructor().newInstance();
                }
            }
        }
    }
    public static Map<String, ArrayList<Integer>> StringAnalyzer(String text) throws IOException {
         try{
             initAnalyzer();
         } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException | InstantiationException |
                  IllegalAccessException e) {
             Log.error(e.getMessage(),e);
             throw new RuntimeException(e);
         }
         return analyzer.StringAnalyzer(text);
    }

    public static Set<String> strAnalyzer(String str) throws IOException{
        try{
            initAnalyzer();
        } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            Log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
        return analyzer.strAnalyzer(str);
    }

}
