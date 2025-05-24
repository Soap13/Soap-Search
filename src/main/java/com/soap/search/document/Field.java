package com.soap.search.document;

import com.soap.search.util.ByteToBitSet;

import java.util.BitSet;

/**
 * 域信息
 * @author Soap
 */
public class Field {
    private String name;
    private String value;

    //是否存储
    private boolean isStore;
    //是否索引
    private boolean isAnalyzed;
    //是否搜索
    private boolean isIndex;

    //存储索引信息
    private BitSet bs=new BitSet(8);

    /**
     * 初始化一个域 其实可以bit+逻辑| 实现
     * 单独写清晰点吧
     * @param name
     * @param value
     * @param fieldBit
     */
    public Field(String name, String value, byte fieldBit) {
        this.name = name;
        this.value = value;
        this.bs=ByteToBitSet.convert(fieldBit);
        if((fieldBit & 0b00000001)!=0)isStore=true;
        if((fieldBit & 0b00000010)!=0)isAnalyzed=true;
        if((fieldBit & 0b00000100)!=0)isIndex=true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isStore() {
        return isStore;
    }

    public void setStore(boolean store) {
        isStore = store;
    }

    public boolean isIndex() {
        return isIndex;
    }

    public void setIndex(boolean index) {
        isIndex = index;
    }
    public BitSet getBs() {
        return bs;
    }

    public boolean isAnalyzed() {
        return isAnalyzed;
    }

    public void setAnalyzed(boolean analyzed) {
        isAnalyzed = analyzed;
    }

    public void setBs(BitSet bs) {
        this.bs = bs;
    }
}
