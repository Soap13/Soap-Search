package com.soap.search.document;

import com.soap.search.conf.ConfUtil;
import com.soap.search.store.DocumentCommon;
import com.soap.search.util.ByteToBitSet;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * 文档常量类
 * @author Soap
 * @date 2018/4/16
 */
public class DocConstant {
    public static final int VERSION=1;//文档版本

    private static final String DOC_PATH_SUFFIX=".df";//文档保存的路径
    private static final String FIELD_PATH_SUFFIX=".ff";//域保存的路径
    private static final String DOC_FIELD_PATH_SUFFIX=".dff";//文档和域的偏移位置
    private static final String TERM_FRQ_PATH_SUFFIX=".tf";//词频文件
    private static final String TERM_OFFSET_INDEX_SUFFIX=".tfo";//词偏移文件
    private static final String TERM_OFFSET_SUFFIX=".tp";//词偏移量

    public static final byte FIELD_STORED = 0b00000001;//是否存储
    public static final byte FIELD_NOT_STORED = 0b00000000;

    public static final byte FIELD_ANALYZED = 0b00000010;//不但被索引，而且被分词，
    public static final byte FIELD_NOT_ANALYZED = 0b00000000;//虽然被索引，但是不分词

    public static final byte FIELD_INDEXED = 0b00000100;//存储了 参与搜索
    public static final byte FIELD_NOT_INDEXED = 0b00000000;//存储了 但不参与搜索

    public static final int DOC_ID_LENGTH =1024; //1024*8=8192

    public static final int TERM_OFFSET_THREAD = 3;//词频个一组

    public static String getPath(){
        return ConfUtil.getProperty("term.path");
    }

    public static int getCurrentSegmentName() throws IOException {
        BitSet bs=DocumentCommon.getCurrentDocNum();
        int docNum= ByteToBitSet.bitSetToInt(bs);
        int segmentNum= Integer.parseInt(ConfUtil.getProperty("term.blockSize"));
        return docNum/segmentNum;
    }

    public static int getCurrentSegmentName(int docNum) throws IOException {
        int segmentNum= Integer.parseInt(ConfUtil.getProperty("term.blockSize"));
        return docNum/segmentNum;
    }

    public static int getCurrentSegmentOffset(int docNum) throws IOException {
        int segmentNum= Integer.parseInt(ConfUtil.getProperty("term.blockSize"));
        return docNum%segmentNum;
    }

    public static String getDocPath(){
         return getPath()+DOC_PATH_SUFFIX;
    }
    public static String getFieldPath() throws IOException {
        return getPath()+getCurrentSegmentName()+FIELD_PATH_SUFFIX;
    }
    public static String getDocFieldPath() throws IOException {
        return getPath()+getCurrentSegmentName()+DOC_FIELD_PATH_SUFFIX;
    }
    public static String getTermFreqPath() throws IOException {
        return getPath()+getCurrentSegmentName()+TERM_FRQ_PATH_SUFFIX;
    }
    public static String getTermOffsetIndexPath() throws IOException {
        return getPath()+getCurrentSegmentName()+TERM_OFFSET_INDEX_SUFFIX;
    }
    public static String getTermOffsetPath() throws IOException {
        return getPath()+getCurrentSegmentName()+TERM_OFFSET_SUFFIX;
    }


    public static Set<String> getFieldPaths() throws IOException {
        Set<String> set=new HashSet<>();
        for(int i=0;i<getCurrentSegmentName();i++){
            set.add(getPath()+i+FIELD_PATH_SUFFIX);
        }
        return set;
    }
    public static Set<String> getDocFieldPaths() throws IOException {
        Set<String> set=new HashSet<>();
        for(int i=0;i<getCurrentSegmentName();i++){
            set.add(getPath()+i+DOC_FIELD_PATH_SUFFIX);
        }
        return set;
    }
    public static Set<String> getTermFreqPaths() throws IOException {
        Set<String> set=new HashSet<>();
        for(int i=0;i<getCurrentSegmentName();i++){
            set.add(getPath()+i+TERM_FRQ_PATH_SUFFIX);
        }
        return set;
    }
    public static Set<String> getTermOffsetIndexPaths() throws IOException {
        Set<String> set=new HashSet<>();
        for(int i=0;i<getCurrentSegmentName();i++){
            set.add(getPath()+i+TERM_OFFSET_INDEX_SUFFIX);
        }
        return set;
    }
    public static Set<String> getTermOffsetPaths() throws IOException {
        Set<String> set=new HashSet<>();
        for(int i=0;i<getCurrentSegmentName();i++){
            set.add(getPath()+i+TERM_OFFSET_SUFFIX);
        }
        return set;
    }

    public static String getFieldPath(int docNum) throws IOException {
        return getPath()+getCurrentSegmentName(docNum)+FIELD_PATH_SUFFIX;
    }
    public static String getDocFieldPath(int docNum) throws IOException {
        return getPath()+getCurrentSegmentName(docNum)+DOC_FIELD_PATH_SUFFIX;

    }
    public static String getTermFreqPath(int docNum) throws IOException {
        return getPath()+getCurrentSegmentName(docNum)+TERM_FRQ_PATH_SUFFIX;
    }
    public static String getTermOffsetIndexPath(int docNum) throws IOException {
        return getPath()+getCurrentSegmentName(docNum)+TERM_OFFSET_INDEX_SUFFIX;
    }
    public static String getTermOffsetPath(int docNum) throws IOException {
        return getPath()+getCurrentSegmentName(docNum)+TERM_OFFSET_SUFFIX;
    }
}