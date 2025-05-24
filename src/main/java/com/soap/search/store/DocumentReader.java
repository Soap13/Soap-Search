package com.soap.search.store;

import com.soap.search.document.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档读取
 */
public class DocumentReader {
    private static final Logger Log = LogManager.getLogger(DocumentReader.class);
    private BitSet bs;
    private int curentDocNum=-1;

 public Document ReadDocument(int docIndex) throws IOException {
     long startTime = System.currentTimeMillis(); // 记录开始时间
     Log.info("开始读取文档：{}",docIndex);
     Document doc=new Document();
     if(null==bs) {
         bs=DocumentCommon.getCurrentDocNum();
     }
     if(bs.get(docIndex)) {
         IndexReader reader = new IndexReader(DocConstant.DOC_FIELD_PATH);
         if(reader.getFilePointer()<=docIndex*4){
             Log.error("文档:[{}]域位置信息丢失...",docIndex);
             return null;//抛异常还是返回null？
         }
         reader.seek(docIndex * 4);
         int position = reader.readInt();
         IndexReader dfReader=new IndexReader(DocConstant.FIELD_PATH);
         ChecksumIndexInput input=new ChecksumIndexInput(dfReader);
         doc.setDocNum(docIndex);
         doc.setFieldOffset(position);
         input.seek(position);
         int numFields = input.readInt();
         for(int i=0;i<numFields;i++){
             String key=input.readString();
             byte  type = input.readByte();
             Field f=new Field(key,"",type);
             String value="";
             if((type & 0b00000001)!=0){
                 f.setValue(input.readString());
             }
             doc.getFields().add(f);
             Log.info("域信息："+f.getName()+":"+f.getValue()+" store:"+f.isStore()+" index:"+f.isIndex());
         }
         reader.close();
         input.close();
         return doc;
     }else{
         Log.info("文档编号：{}不存在...",docIndex);
     }
     Log.info("===文档读取耗时：{}ms",(System.currentTimeMillis()-startTime)); // 记录开始时间);
     return null;
 }

    /**
     * 词频读入
     * @return
     */
 public List<TermFrq> readTermFrq() throws IOException {
     long startTime = System.currentTimeMillis(); // 记录开始时间
     Log.info("词频开始提取...");
     //List<TermFrq> frqList=new ArrayList<>();
     Map<String,TermFrq> frqMap=new HashMap<String,TermFrq>();
     IndexReader dfReader=new IndexReader(DocConstant.TERM_FRQ_PATH);
     ChecksumIndexInput input=new ChecksumIndexInput(dfReader);
     while(input.getFilePointer()>input.length()){
         String term=input.readString();
         TermFrq termFrq=frqMap.get(term);
         if(termFrq==null){
             termFrq=new TermFrq();
             frqMap.put(term,termFrq);
         }
         termFrq.setTerm(term);
         int size=input.readVInt();
         for(int i=0;i<size;i++) {
             termFrq.getDocnum().add(input.readVInt());
             termFrq.getFrq().add(input.readVInt());
         }
         //reqList.add(termFrq);
     }
     input.close();
     Log.info("词频提取结束...");
     List<TermFrq> termFrqList = frqMap.values().stream()
             .map(obj -> (TermFrq) obj)
             .collect(Collectors.toList());
     Log.info("===词频提取耗时：{}ms",(System.currentTimeMillis()-startTime)); // 记录开始时间);
     return termFrqList;
 }

    /**
     * 词偏移量和位置分开吧
     */
    public Map<String,TermOffset> readTermOffSet() throws IOException {
        Log.info("词位置开始提取...");
        long startTime = System.currentTimeMillis(); // 记录开始时间
        Map<String,TermOffset> toMap=new HashMap<String, TermOffset>();
        IndexReader toReader=new IndexReader(DocConstant.TERM_OFFSET);
        ChecksumIndexInput input=new ChecksumIndexInput(toReader);
        while(input.getFilePointer()>input.length()){
            String term=input.readString();
            TermOffset to=toMap.get(term);
            if(to==null){
                to=new TermOffset();
                toMap.put(term,to);
            }
            to.setTerm(term);
            int size=input.readVInt();//词-域个数
            for(int i=0;i<size;i++) {
                String key=input.readString();//词-域
                List<Integer>list=to.getFo().get(key);
                if(null==list){
                    list=new ArrayList<Integer>();
                    to.getFo().put(key,list);
                }
                int offsetSize=input.readVInt();
                int lastPostion=0;
                for(int j=0;j<offsetSize;j++){
                    int diff=input.readVInt();
                    list.add(diff+lastPostion);
                    lastPostion+=diff;
                }
            }
        }
        input.close();
        Log.info("词位置提取结束...");
        Log.info("===词位置提取耗时：{}ms",(System.currentTimeMillis()-startTime)); // 记录开始时间);
        return toMap;
    }
}
