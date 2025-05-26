package com.soap.search.test;

import com.soap.search.document.*;
import com.soap.search.query.Search;
import com.soap.search.store.*;
import com.soap.search.util.IKUtil;
import com.soap.search.util.SkipList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class FileTest {
    private static final Logger Log = LogManager.getLogger(FileTest.class);
    public static void main(String[] args) throws IOException, InvocationTargetException, IllegalAccessException {
        FileTest f=new FileTest();
        //f.test2();
        //f.test3();
//        f.test4();
        //f.test5(2);
        //f.testIK();
//        f.testTermFreq();
//        f.search();
        //f.searchTerOffet();

//        f.writerDoc("D:/go_work/文章/小说/平凡的世界.txt");
//        f.writerDoc("D:/go_work/文章/小说/活着.txt");
//        f.writerDoc("D:/go_work/文章/小说/围城.txt");
//        f.writerDoc("D:/go_work/文章/小说/张爱玲文集.txt");
//        f.initDoc("D:/go_work/文章/小说");
        f.testSearch(false);
//      f.printSearch();
    }

    public void initDoc(String fpath){
        Path folder = Paths.get(fpath);
        try (Stream<Path> stream = Files.list(folder)) {
            stream
                    .filter(path -> !Files.isDirectory(path)) // 过滤掉目录
                    .forEach(path -> {
                                try {
                                    System.out.println("File: " + path.getFileName());
                                    writerDoc(path.toString());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printSearch() throws IOException {
        DocumentReader reader=new DocumentReader();
        Document doc = reader.ReadDocument(2);
        for(Field fe:doc.getFields()){
            Log.info(fe.getName()+" "+fe.getValue());
        }

        List<TermFrq> termList=reader.readTermFrq();
        for(int i=0;i<termList.size();i++){
            TermFrq t=termList.get(i);
            Log.info("词：{}，文档{},词频{}",t.getTerm(),Arrays.toString(t.getDocnum().toArray()),Arrays.toString(t.getFrq().toArray()));
        }
    }

    public void testSearch(boolean isThread) throws IOException {
        Search search=Search.getSearch();
        search.resetSkipList(isThread);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            String[] commandList = command.split(" ");
            //List<Document>docList=search.search(commandList[0]);

            List<Document>docList=search.searchScore(commandList[0]);

            Log.info("---结果---");
            for(Document doc:docList){
                Log.info("-----------------");
                Log.info("文档：{},得分{}",doc.getDocNum(),doc.getScore());
               for(Field f:doc.getFields()){
                   Log.info("文档：{}，字段名：{}，字段值：{}",doc.getDocNum(),f.getName(),f.getValue());
               }
                Log.info("-----------------");
            }
        }
    }

    public void searchTerOffet() throws IOException {
        DocumentReader reader=new DocumentReader();
        Map<String, TermOffset> termMap=reader.readTermOffSet();
        for(Map.Entry<String, TermOffset> en:termMap.entrySet()){
            for(Map.Entry<String, List<Integer>>to:en.getValue().getFo().entrySet()){
                Log.info("词：[{}]，文档:[{}],词频:[{}]",en.getKey(),to.getKey(),Arrays.toString(to.getValue().toArray()));
            }
        }
    }

    public void search() throws IOException {
        DocumentReader reader=new DocumentReader();
        SkipList<String, TermFrq> skipList = new SkipList<>();
        List<TermFrq> termList=reader.readTermFrq();
        for(int i=0;i<termList.size();i++){
            TermFrq t=termList.get(i);
            Log.info("词：{}，文档{},词频{}",t.getTerm(),Arrays.toString(t.getDocnum().toArray()),Arrays.toString(t.getFrq().toArray()));
            skipList.insertNode(t.getTerm(),t);
        }
        String key="躺着";
        if (!skipList.searchNode(key)) {
            System.out.println("Key: " + key + " not exists!");
        }else {
            TermFrq node = skipList.getNode(key);
            Log.info("结果===词：{}，文档{},词频{}",node.getTerm(),Arrays.toString(node.getDocnum().toArray()),Arrays.toString(node.getFrq().toArray()));
        }
    }

    public void testTermFreq() throws IOException, InvocationTargetException, IllegalAccessException {

        DocumentReader reader=new DocumentReader();
        List<Document> docList=new ArrayList<>();
        for(int i=0;i<5;i++) {
            Document doc = reader.ReadDocument(i);
            if(doc!=null){
                docList.add(doc);
            }else{
                break;
            }
        }

        Document doc1=new Document();
        doc1.initFile(new File("D:/go_work/文章/小说/平凡的世界.txt"));
        DocumentWriter writer=new DocumentWriter();
        writer.writeDocument(doc1);
        docList.add(doc1);

        writer.writeTermFrq(docList);

//        List<TermFrq> termList=reader.readTermFrq();
//        for(int i=0;i<termList.size();i++){
//            TermFrq t=termList.get(i);
//            Log.info("词：{}，文档{},词频{}",t.getTerm(),Arrays.toString(t.getDocnum().toArray()),Arrays.toString(t.getFrq().toArray()));
//        }

        writer.writeTermOffSet(docList);

//        Map<String, TermOffset> termMap=reader.readTermOffSet();
//        for(Map.Entry<String, TermOffset> en:termMap.entrySet()){
//            for(Map.Entry<String, List<Integer>>to:en.getValue().getFo().entrySet()){
//              Log.info("词：[{}]，文档:[{}],词偏移量:[{}]",en.getKey(),to.getKey(),Arrays.toString(to.getValue().toArray()));
//            }
//        }
    }
    public void writerDoc(String path) throws IOException, InvocationTargetException, IllegalAccessException { Document doc1=new Document();
        List<Document> docList=new ArrayList<>();
        doc1.initFile(new File(path));
        DocumentWriter writer=new DocumentWriter();
        writer.writeDocument(doc1);
        docList.add(doc1);
        writer.writeTermFrq(docList);
        writer.writeTermOffSet(docList);

    }

    public void testIK(){
        Document doc=new Document();
        doc.initFile(new File("D:/go_work/文章/小说/活着.txt"));
        Map<String, ArrayList<Integer>>termMap=IKUtil.IDAnalyzer(doc.getFields().get(2).getValue());
        for(Map.Entry<String,ArrayList<Integer>> en:termMap.entrySet()){
            System.out.println(en.getKey()+" "+en.getValue().size());
        }
    }
    public void test4() throws IOException {
        Document doc=new Document();
        doc.initFile(new File("D:/go_work/文章/小说/活着.txt"));
        DocumentWriter writer=new DocumentWriter();
        writer.writeDocument(doc);
    }
    public void test5(int i) throws IOException {
        DocumentReader reader=new DocumentReader();
        reader.ReadDocument(i);
    }
    public void test2(){
        BitSet bs1=new BitSet(8);
        bs1.set(2,4);
        bs1.set(7);
        int n=bs1.nextSetBit(5);//从这位开始的下一个
        System.out.println(n);
        System.out.println(bs1);
        System.out.println(bs1.length()+" "+bs1.cardinality());//长度，非0的个数
        System.out.println(bs1.get(0));//返回的时true false
    }

    /**
     * 测试文档编号内容写入
     */
    public void test3() throws IOException {
        IndexWriter writer=new IndexWriter(DocConstant.DOC_PATH,false);
        ChecksumIndexOutput output=new ChecksumIndexOutput(writer);
        output.writeInt(DocConstant.VERSION);//版本号
        BitSet bs=new BitSet(DocConstant.DOC_ID_LENGTH);
        bs.set(0);
        //bs.set(8,15);
        byte[] w=bs.toByteArray();
        output.writeVInt(w.length);
        output.writeBytes(w, w.length);
        output.close();
        //DocumentWriter dw =new DocumentWriter();
       // dw.writeDocument(null,null);
    }

    public void test1() throws IOException {
        Map<String,String> testMap=new HashMap<>();
        testMap.put("1","1");
        testMap.put("2","2");
        testMap.put("3","3");
        testMap.put("4","4");
        testMap.put("5","5");
        String fieldPath="./fp";
        IndexWriter writer=new IndexWriter(fieldPath,false);
        ChecksumIndexOutput output=new ChecksumIndexOutput(writer);
        output.writeStringStringMap(testMap);
        long length=output.getFilePointer();
        System.out.println(output.length());
        output.close();

        System.out.println("写文件结束"+length);

        testMap.clear();
        testMap.put("第二次写入","1234454");
        writer=new IndexWriter(fieldPath,true);
        System.out.println(writer.getFilePointer());
        System.out.println(writer.length());
        output=new ChecksumIndexOutput(writer);
        output.writeStringStringMap(testMap);
        long l2length=output.getFilePointer();
        writer.close();
        System.out.println("写文件结束"+l2length);

        IndexReader reader=new IndexReader(fieldPath);
        ChecksumIndexInput input=new ChecksumIndexInput(reader);
        input.seek(length);
        testMap=input.readStringStringMap();
        //testMap.putAll(input.readStringStringMap());
        for(Map.Entry<String,String> en:testMap.entrySet()){
            System.out.println(en.getKey()+" "+en.getValue());
        }
        System.out.println("读文件结束");
    }
}
