package com.soap.search.store;

import com.google.common.collect.Lists;
import com.soap.search.document.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 文档读取
 */
public class DocumentReader {
    private static final Logger Log = LogManager.getLogger(DocumentReader.class);
    private BitSet bs;
    private int curentDocNum=-1;
    private ExecutorService executorService;

     public DocumentReader() throws IOException {
         Log.info("线程池初始化预热...");
         executorService = Executors.newFixedThreadPool(DocConstant.TERM_OFFSET_THREAD);
         // 预热线程
         for (int i = 0; i < DocConstant.TERM_OFFSET_THREAD; i++) {
             executorService.submit(() -> {});
         }
    }

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
     //IndexReader dfReader=new IndexReader(DocConstant.TERM_FRQ_PATH);
     IndexReaderPage dfReader=new IndexReaderPage(DocConstant.TERM_FRQ_PATH);
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
     * 词频读入 多线程
     * @return
     */
    public List<TermFrq> readTermFrqThread() throws IOException {
        long startTime = System.currentTimeMillis(); // 记录开始时间
        Log.info("词频开始提取...");
        Map<String,TermFrq> frqMap=new ConcurrentHashMap<String,TermFrq>();

        //词频位置
        IndexReaderPage dfoReader=new IndexReaderPage(DocConstant.TERM_OFFSET_PATH);
        ChecksumIndexInput inputo=new ChecksumIndexInput(dfoReader);
        long length=inputo.getFilePointer();//文件长度
        if(length>0) {
//            //开多少个线程
//            int threadNum=(int)length/DocConstant.TERM_OFFSET_THREAD;
//            if((int)length%DocConstant.TERM_OFFSET_THREAD>0){
//                threadNum++;
//            }
            Log.info("词频个数:"+ length/4);
            List<Integer>termOffsetList=new ArrayList<Integer>();
            for(int i=0;i<length/4;i++){
                termOffsetList.add(inputo.readInt());
            }
            int chunkSize = (int) Math.ceil((double) termOffsetList.size() / DocConstant.TERM_OFFSET_THREAD);
            List<List<Integer>> partitions = Lists.partition(termOffsetList, chunkSize);
            for(int i=0;i<partitions.size()-1;i++){
                partitions.get(i).add(partitions.get(i+1).get(0));
            }
            File f=new File(DocConstant.TERM_FRQ_PATH);
            partitions.get( partitions.size()-1).add((int)f.length());

            // 创建固定大小的线程池
//            ExecutorService executorService = Executors.newFixedThreadPool(partitions.size());
            // 使用原子计数器模拟任务编号
            AtomicInteger taskCounter = new AtomicInteger(1);

            // 提交 50 个任务到线程池
            for (int i = 0; i < partitions.size(); i++) {
                int finalI = i;
                executorService.submit(new Task(taskCounter.getAndIncrement(),  partitions.get(i),frqMap));
            }
            // 第一步：关闭任务提交通道，已提交的任务继续执行
            executorService.shutdown();
            try {
                // 第二步：等待所有任务完成，最多等待一段时间
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    // 如果超时还没结束，则强制关闭
                    executorService.shutdownNow();
                }
            } catch (InterruptedException ex) {
                // 当前线程被中断时，也尝试强制关闭
                executorService.shutdownNow();
                Thread.currentThread().interrupt(); // 重新设置中断标志
            }
            Log.info("词频提取结束...");
        }
        List<TermFrq> termFrqList = frqMap.values().stream()
                .map(obj -> (TermFrq) obj)
                .collect(Collectors.toList());
        Log.info("===词频提取耗时：{}ms", (System.currentTimeMillis() - startTime)); // 记录开始时间);
        return termFrqList;
    }

    // 模拟任务类
    static class Task implements Runnable {
        private final int taskId;
        private final List<Integer> termList;
        private final Map<String,TermFrq> frqMap;
        public Task(int taskId,List<Integer> termList,Map<String,TermFrq> frqMap) {
            this.taskId = taskId;
            this.termList = termList;
            this.frqMap=frqMap;
        }

        @Override
        public void run() {
            Log.info("线程 [" + Thread.currentThread().getName() + "] 正在处理任务 #" + taskId);
            try {
                int start = termList.get(0);
                int end=termList.get(termList.size()-1);
                IndexReaderPage dfReaderPage=new IndexReaderPage(DocConstant.TERM_FRQ_PATH,(end-start));
                dfReaderPage.seek(start);
                dfReaderPage.readAllFile();

                ChecksumIndexInput input=new ChecksumIndexInput(dfReaderPage);
                //input.seek(termList.get(0));//记录的位置因为事顺序的
                for(int i=0;i<termList.size()-1;i++){
                    //Log.info("线程 [{}] 正在处理任务 #{}索引位置：{}", Thread.currentThread().getName(),taskId,termList.get(i));
                    String term = input.readString();
                    TermFrq termFrq = frqMap.get(term);
                    if (termFrq == null) {
                        termFrq = new TermFrq();
                        frqMap.put(term, termFrq);
                    }
                    termFrq.setTerm(term);
                    int size = input.readVInt();
                    for (int j = 0; j< size; j++) {
                        termFrq.getDocnum().add(input.readVInt());
                        termFrq.getFrq().add(input.readVInt());
                    }
                    //reqList.add(termFrq);
                }
                input.close();
            } catch (IOException e) {
                Log.error("线程 [" + Thread.currentThread().getName() + "] 运行异常"+e.getMessage(),e);
            }
            Log.info("线程 [" + Thread.currentThread().getName() + "] 完成任务 #" + taskId);
        }
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
