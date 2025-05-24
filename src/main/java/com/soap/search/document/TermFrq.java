package com.soap.search.document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Soap
 * @Date 2025/5/22 13:57
 * @Version 1.0
 */
public class TermFrq {
    //词
    private String term;
    //文档
    private List<Integer> docnum=new ArrayList<>();
    //词频
    private List<Integer> frq=new ArrayList<>();

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<Integer> getDocnum() {
        return docnum;
    }

    public void setDocnum(List<Integer> docnum) {
        this.docnum = docnum;
    }

    public List<Integer> getFrq() {
        return frq;
    }

    public void setFrq(List<Integer> frq) {
        this.frq = frq;
    }
}
