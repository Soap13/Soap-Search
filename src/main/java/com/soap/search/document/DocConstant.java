package com.soap.search.document;

/**
 * 文档常量类
 * @author Soap
 * @date 2018/4/16
 */
public class DocConstant {
    public static final int VERSION=1;//文档版本

    public static final String DOC_PATH="./df";//文档保存的路径
    public static final String FIELD_PATH="./ff";//域保存的路径
    public static final String DOC_FIELD_PATH="./dff";//文档和域的偏移位置
    public static final String TERM_FRQ_PATH="./tf";//词频文件
    public static final String TERM_OFFSET="./tp";//词偏移量

    public static final byte FIELD_STORED = 0b00000001;//是否存储
    public static final byte FIELD_NOT_STORED = 0b00000000;

    public static final byte FIELD_ANALYZED = 0b00000010;//不但被索引，而且被分词，
    public static final byte FIELD_NOT_ANALYZED = 0b00000000;//虽然被索引，但是不分词

    public static final byte FIELD_INDEXED = 0b00000100;//存储了 参与搜索
    public static final byte FIELD_NOT_INDEXED = 0b00000000;//存储了 但不参与搜索

    public static final int DOC_ID_LENGTH =1024; //1028*8=8224
}
