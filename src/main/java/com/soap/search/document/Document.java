package com.soap.search.document;

import com.soap.search.util.DateUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档
 * @author: soap
 */
public class Document {
    /**
     * 文档ID 存储得是个bitset
     * 在删除标志位的时候方便点 0表示删除，1表示不删除
     * 1byte=8bit 1024*8 = 8192
     */
    private int docNum=-1;
    //文件类型
    private String fileType;
    //域开始偏移量
    private int fieldOffset=-1;

    private List<Field> fields=new ArrayList<>();
    private int position=-1;
    private Double score=0d;
    /**
     * 文件域默认初始化
     * @param file
     * @return
     */
    public boolean initFile(File file){
        if(file==null){
            return false;
        }else{
            fields.add(new Field("path", file.getPath(), (byte)(DocConstant.FIELD_STORED|DocConstant.FIELD_NOT_ANALYZED)));
            fields.add(new Field("modified", DateUtil.getDateStr(file.lastModified(), DateUtil.DATE_TIME_FORMAT),(byte)(DocConstant.FIELD_STORED|DocConstant.FIELD_NOT_ANALYZED)));
            String contents=getFileContent(file.getPath());
            fields.add(new Field("contents", contents,(byte)(DocConstant.FIELD_NOT_STORED|DocConstant.FIELD_ANALYZED)));
        }
        return true;
    }

    /**
     * 读取文件内容
     * @param filePath
     * @return
     */
    public String getFileContent(String filePath) {
        try (FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            StringBuilder sb=new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
               sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace(); // 异常处理
        }
        return null;
    }

    public int getDocNum() {
        return docNum;
    }

    public void setDocNum(int docNum) {
        this.docNum = docNum;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getFieldOffset() {
        return fieldOffset;
    }

    public void setFieldOffset(int fieldOffset) {
        this.fieldOffset = fieldOffset;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
