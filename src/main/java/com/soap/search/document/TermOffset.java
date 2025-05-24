package com.soap.search.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Soap
 * @Date 2025/5/22 22:12
 * @Version 1.0
 */
public class TermOffset {
    //词
    private String term;
    /*
     * 词位置
     * key=docnum:fieldnum
     */
    private Map<String,List<Integer>> fo=new HashMap<String,List<Integer>>();

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Map<String, List<Integer>> getFo() {
        return fo;
    }

    public void setFo(Map<String, List<Integer>> fo) {
        this.fo = fo;
    }
}
