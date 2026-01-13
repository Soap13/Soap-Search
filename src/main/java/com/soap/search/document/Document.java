package com.soap.search.document;

import com.soap.search.store.DocumentWriter;
import com.soap.search.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档
 * @author: soap
 */
public class Document {
    private static final Logger Log = LogManager.getLogger(Document.class);

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

    public int initAnalyserFile(String filePath) throws IOException, InvocationTargetException, IllegalAccessException {
       if(initFile(new File(filePath))){
           List<Document> docList=new ArrayList<>();
           DocumentWriter writer=new DocumentWriter();
           int docNum=writer.writeDocument(this);
           docList.add(this);
           writer.writeTermFrq(docList);
           writer.writeTermOffSetGolomb2(docList);
           return docNum;
       }
       return -1;
    }

    public String getFileContent(String filePath) {
        if(filePath.endsWith(".md")){
            return getMDContent(filePath);
        }else if(filePath.endsWith(".pdf")){
            return extractTextFromFile(filePath);
        }else{
            return getTxtContent(filePath);
        }
    }
    /**
     * 读取文件内容
     * @param filePath
     * @return
     */
    public String getTxtContent(String filePath) {
        try (FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            StringBuilder sb=new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
               sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            Log.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * 读取MD文件内容
     * @param filePath
     * @return
     */
    public String getMDContent(String filePath) {
        String markdown = getFileContent(filePath);
        Parser parser = Parser.builder().build();
        TextContentRenderer renderer = TextContentRenderer.builder().build();
        return renderer.render(parser.parse(markdown));
    }

    /**
     * 从本地 PDF 文件提取文本
     */
    public static String extractTextFromFile(String pdfPath){
        try (PDDocument document = Loader.loadPDF(new File(pdfPath))) {
            if (document.isEncrypted()) {
                // 可选：处理加密 PDF（需密码）
                // document.decrypt("password");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            // 可选：设置页码范围
            // stripper.setStartPage(1);
            // stripper.setEndPage(3);
            return stripper.getText(document);
        }catch (IOException e){
            Log.error(e.getMessage(),e);
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
