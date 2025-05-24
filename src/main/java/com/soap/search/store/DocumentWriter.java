package com.soap.search.store;

import com.soap.search.document.DocConstant;
import com.soap.search.document.Document;
import com.soap.search.document.Field;
import com.soap.search.document.TermFrq;
import com.soap.search.util.IKUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档写入
 */
public class DocumentWriter {
    private static final Logger  Log = LogManager.getLogger(DocumentWriter.class);
    private BitSet bs;
    private int curentDocNum=-1;

 public void writeDocument(Document doc) throws IOException {
     File docFile=new File(DocConstant.DOC_PATH);
     if(!docFile.exists()){
         initDocNum(0);
         this.bs=new BitSet(1);
         curentDocNum=0;
     }

     if(null==bs) {
         bs=DocumentCommon.getCurrentDocNum();
     }
     //文档号+1
     for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
         curentDocNum = i+1;
     }
     Log.info("当前文档编号:{}",curentDocNum);
     if(curentDocNum!=-1) {
         doc.setDocNum(curentDocNum);
         this.bs=new BitSet(curentDocNum);
         initDocNum(curentDocNum);
         Log.info("文档编号:{}设置结束",curentDocNum);
         long position=initField(doc,true);
         Log.info("文档编号:{},域的开始地址:{}设置结束",curentDocNum,position);
         initDocFieldPosition(curentDocNum,(int)position);
         Log.info("文档编号:{},域关联设置结束",curentDocNum);
     }
 }

    /**
     * 设置文档编号
     * @param docNum
     * @throws IOException
     */
 public void initDocNum(int docNum) throws IOException {
     Log.info("init 文档编号:{}",docNum);
     BitSet currBS=DocumentCommon.getCurrentDocNum();
     IndexWriter initDoc=new IndexWriter(DocConstant.DOC_PATH,false);
     ChecksumIndexOutput output=new ChecksumIndexOutput(initDoc);
     output.writeInt(DocConstant.VERSION);//版本号
     //BitSet bs=new BitSet(DocConstant.DOC_ID_LENGTH);
     //bs.set(0);
     currBS.set(docNum);
     byte[] w=currBS.toByteArray();
     output.writeVInt(w.length);
     output.writeBytes(w, w.length);
     output.close();
 }

    /**
     * 写入
     * @param doc
     * @param isAppend
     * @throws IOException
     */
 public long initField(Document doc,boolean isAppend) throws IOException {
     //写域信息
     IndexWriter writer=new IndexWriter(DocConstant.FIELD_PATH,isAppend);
     ChecksumIndexOutput output=new ChecksumIndexOutput(writer);
     long position = output.getFilePointer();//得到位置
     output.writeInt(doc.getFields().size());//写域的总数
     for(int i=0;i<doc.getFields().size();i++){
         Field f=doc.getFields().get(i);
         output.writeString(f.getName());
         BitSet bs=f.getBs();
         output.writeByte(bs.toByteArray()[0]);
         if(f.isStore()){
             output.writeString(f.getValue());
         }
     }
     long endPosition=output.getFilePointer();
     output.close();
     Log.info("文档编号:{},域的开始地址:{}",doc.getDocNum(),position);
     return position;
 }

 public void initDocFieldPosition(int docNum,int fieldPostion) throws IOException { //写关联关系表
     Log.info("文档编号:{},域的开始地址:{}",docNum,fieldPostion);
     IndexWriter writerDF=new IndexWriter(DocConstant.DOC_FIELD_PATH,true);
     ChecksumIndexOutput outputDF=new ChecksumIndexOutput(writerDF);
     //outputDF.seek((docNum-1)*4); //写数据跳转这里被限制住了
     outputDF.writeInt(fieldPostion);
     outputDF.close();
 }

    /**
     * 重写跟新的方式
     * @param docList
     * @return
     * @throws IOException
     */
 public Map<String, ArrayList<Integer>> writeTermFrq(List<Document> docList) throws IOException {
     long startTime = System.currentTimeMillis(); // 记录开始时间
     Log.info("词频开始提取...");
     Map<String, ArrayList<Integer>> termMap = null;
     List<TermFrq> reqList=new ArrayList<>();
     for(Document doc:docList) {
         Log.info("文档:{},词频开始提取...",doc.getDocNum());
         for (int i = 0; i < doc.getFields().size(); i++) {
             Field f = doc.getFields().get(i);
             if (f.isAnalyzed()) { //是否分词
                 termMap = IKUtil.IDAnalyzer(f.getValue());
//                 for (Map.Entry<String, ArrayList<Integer>> en : termMap.entrySet()) {
//                    Log.info(en.getKey() + " " + en.getValue().size());
//                 }
             } else {
                 termMap = new HashMap<String, ArrayList<Integer>>();
                 termMap.put(f.getValue(), new ArrayList<Integer>());
                 termMap.get(f.getValue()).add(0);
             }
         }
         for (Map.Entry<String, ArrayList<Integer>> en : termMap.entrySet()) {
             TermFrq req = new TermFrq();
             req.setTerm(en.getKey());
             req.getDocnum().add(doc.getDocNum());
             req.getFrq().add(en.getValue().size());
             reqList.add(req);
         }
         termMap.clear();
     }
     Log.info("词频开始写入...");
     IndexWriter writerDF = new IndexWriter(DocConstant.TERM_FRQ_PATH, true);
     ChecksumIndexOutput outputDF = new ChecksumIndexOutput(writerDF);
     for (TermFrq tf:reqList) {
         outputDF.writeString(tf.getTerm());
         outputDF.writeVInt(tf.getDocnum().size());
         for (int i=0;i<tf.getDocnum().size();i++) {
             outputDF.writeVInt(tf.getDocnum().get(i));
             outputDF.writeVInt(tf.getFrq().get(i));
         }
     }
     outputDF.close();
     Log.info("词频写入结束...");
     Log.info("===词频写入耗时：{}ms",(System.currentTimeMillis()-startTime)); // 记录开始时间);
     return termMap;
 }

    /**
     * 词偏移量和位置分开吧
     * @param docList
     */
 public void writeTermOffSet(List<Document> docList) throws IOException, InvocationTargetException, IllegalAccessException {
     long startTime = System.currentTimeMillis(); // 记录开始时间
     Log.info("词位置开始提取...");
     Map<String, ArrayList<Integer>> termMap = null;
     Map<String,Map<String,List<Integer>>>termOffset=new HashMap<>();
     for(Document doc:docList) {
         Log.info("文档:{},词位置开始提取...",doc.getDocNum());
         for (int i = 0; i < doc.getFields().size(); i++) {
             Field f = doc.getFields().get(i);
             if (f.isAnalyzed()) { //是否分词
                 termMap = IKUtil.IDAnalyzer(f.getValue());
//                 for (Map.Entry<String, ArrayList<Integer>> en : termMap.entrySet()) {
//                     Log.info(en.getKey() + " " + en.getValue().size());
//                 }
             } else {
                 termMap = new HashMap<String, ArrayList<Integer>>();
                 termMap.put(f.getValue(), new ArrayList<Integer>());
                 termMap.get(f.getValue()).add(0);
             }

             Map<String, ArrayList<Integer>> copyMap = termMap.entrySet()
                     .stream()
                     .collect(Collectors.toMap(
                             Map.Entry::getKey,
                             e -> new ArrayList<>(e.getValue())
                     ));


             String termOf=doc.getDocNum()+":"+i;
             for(Map.Entry<String,ArrayList<Integer>> en:copyMap.entrySet()){
                 Map<String,List<Integer>> position=termOffset.get(en.getKey());
                 if(position==null){
                     position=new HashMap<>();
                     termOffset.put(en.getKey(),position);
                 }
                 if(null==position.get(termOf)){
                     position.put(termOf,new ArrayList<Integer>());
                 }
                 position.get(termOf).addAll(en.getValue());//注意顺序
                 termOffset.put(en.getKey(),position);
             }
             termMap.clear();
         }
     }
     Log.info("词位置开始写入...");
     IndexWriter writerDF = new IndexWriter(DocConstant.TERM_OFFSET, true);
     ChecksumIndexOutput outputDF = new ChecksumIndexOutput(writerDF);
     for(Map.Entry<String,Map<String,List<Integer>>> en:termOffset.entrySet()){
         String term=en.getKey();
         outputDF.writeString(term);//词
         outputDF.writeVInt(en.getValue().keySet().size());//域个数
         for(Map.Entry<String,List<Integer>> en1:en.getValue().entrySet()){
             String df=en1.getKey();
             outputDF.writeString(df);
             int last=0;
             outputDF.writeVInt(en1.getValue().size());
             for(int i=0;i<en1.getValue().size();i++){
                 int current=en1.getValue().get(i)-last;
                 outputDF.writeVInt(current);
                 last=en1.getValue().get(i);
             }
         }
     }
     outputDF.close();
     Log.info("词位置写入结束...");
     Log.info("===词位置写入耗时：{}ms",(System.currentTimeMillis()-startTime)); // 记录开始时间);
 }
}
