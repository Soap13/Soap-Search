package com.soap.search.store;

import com.soap.search.document.DocConstant;
import com.soap.search.util.ByteToBitSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;

/**
 * @author Soap
 * @Date 2025/5/22 11:00
 * @Version 1.0
 */
public class DocumentCommon {
    private static final Logger Log = LogManager.getLogger(DocumentCommon.class);
    public static BitSet getCurrentDocNum() throws IOException {
        return getCurrentDocNum(DocConstant.DOC_PATH);
    }
    public static BitSet getCurrentDocNum(String filePath) throws IOException {
        Log.info("读取文档路径:{}",filePath);
        File f=new File(filePath);
        if(!f.exists()){
            Log.info("文件:{},不存在",filePath);
            return new BitSet();
        }
        //首先到当前已有的文档编号
        IndexReader reader = new IndexReader(DocConstant.DOC_PATH);
        ChecksumIndexInput input = new ChecksumIndexInput(reader);
        //得到数据长度
        int version = input.readInt();
        int length = input.readVInt();//长度
        byte[] docBytes = new byte[DocConstant.DOC_ID_LENGTH];
        input.readBytes(docBytes, 0, length);
        BitSet bs = ByteToBitSet.convert(docBytes);
        input.close();
        return bs;
    }
}
