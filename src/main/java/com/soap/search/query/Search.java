package com.soap.search.query;

import com.soap.search.document.Document;
import com.soap.search.document.TermFrq;
import com.soap.search.store.DocumentReader;
import com.soap.search.util.BM25Scorer;
import com.soap.search.util.IKUtil;
import com.soap.search.util.SkipList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Soap
 * @Date 2025/5/23 15:13
 * @Version 1.0
 */
public class Search {
    private static final Logger Log = LogManager.getLogger(Search.class);

    //单例模式
    private static final Search search=new Search();

    // 创建锁对象 读写锁
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    //跳表搜索
    private SkipList<String, TermFrq> skipList = new SkipList<>();

    private  Search(){}
    public static Search getSearch() throws IOException {
        return search;
    }

    /**
     * 重置枷锁了，但是查询那里没同步
     * @throws IOException
     */
    public void resetSkipList(boolean isTherad) throws IOException {
        try {
            if (lock.writeLock().tryLock(20, TimeUnit.SECONDS)) {
                long startTime = System.currentTimeMillis(); // 记录开始时间
                try {
                    skipList = new SkipList<>();//重置下
                    DocumentReader reader = new DocumentReader();
                    List<TermFrq> termList = isTherad? reader.readTermFrqThread() : reader.readTermFrq();
                    for (int i = 0; i < termList.size(); i++) {
                        TermFrq t = termList.get(i);
                        skipList.insertNode(t.getTerm(), t);
                    }
                    Log.info("===跳表加载耗时：{}ms", (System.currentTimeMillis() - startTime)); // 记录开始时间);
                } finally {
                    lock.writeLock().unlock();
                }
            }
        } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
            Log.error("线程被中断",e);
    }
    }

    public List<Document> search(String key) throws IOException {
        lock.readLock().lock();
        List<Document> docList = new ArrayList<>();
        try {
            long startTime = System.currentTimeMillis(); // 记录开始时间
            Log.info("查询：{}", key);
            List<List<Integer>> docNums = new ArrayList<>();
            List<Integer> result = new ArrayList<>();
            Set<String> strSet = IKUtil.strAnalyzer(key);
            for (String str : strSet) {
                TermFrq node = skipList.getNode(str);
                if (node != null) {
                    docNums.add(node.getDocnum());
                    if (result.size() == 0) {
                        result = node.getDocnum();
                    } else {
                        Collection<Integer> union = CollectionUtils.intersection(result, node.getDocnum());
                        result = new ArrayList<>(union);
                    }
                }
            }

            for (Integer docNum : result) {
                DocumentReader reader = new DocumentReader();
                docList.add(reader.ReadDocument(docNum));
            }
            Log.info("===搜搜耗时：{}ms", (System.currentTimeMillis() - startTime)); // 记录开始时间);
        }finally {
            lock.readLock().unlock();
        }
        return docList;
    }


    public List<Document> searchScore(String key) throws IOException {
        lock.readLock().lock();
        List<Document> docList = new ArrayList<>();
        try {
            long startTime = System.currentTimeMillis(); // 记录开始时间
            Log.info("查询：{}", key);
            //List<List<Integer>> docNums = new ArrayList<>();
            List<Integer> result = new ArrayList<>();
            Map<String,TermFrq> termFrqMap=new HashMap<>();
            Set<String> strSet = IKUtil.strAnalyzer(key);
            for (String str : strSet) {
                TermFrq node = skipList.getNode(str);
                if (node != null) {
                    //docNums.add(node.getDocnum());
                    if (result.size() == 0) {
                        result = node.getDocnum();
                    } else {
                        Collection<Integer> union = CollectionUtils.union(result, node.getDocnum());
                        //Collection<Integer> union = CollectionUtils.intersection(result, node.getDocnum());
                        result = new ArrayList<>(union);
                    }
                    termFrqMap.put(str,node);
                }
            }
            Map<Integer,Document> docMap=new HashMap<>();
            Map<String, Map<String, Integer>> documents = new HashMap<>();
            for (Integer docNum : result) {
                DocumentReader reader = new DocumentReader();
                Document doc = reader.ReadDocument(docNum);
                docList.add(doc);
                docMap.put(docNum,doc);

                Map<String, Integer> termMap = new HashMap<>();
                for (String str : strSet) {
                    TermFrq t=termFrqMap.get(str);
                    int index=t.getDocnum().indexOf(docNum);
                    if(index>-1) {
                        termMap.put(str, t.getFrq().get(index));
                    }
                }
                documents.put(String.valueOf(docNum), termMap);
            }

            for (String str : strSet) {
                Map<String, Double> scores = BM25Scorer.scoreDocuments(str,documents, docList.size(), docList.size());
                for (Map.Entry<String, Double> entry : scores.entrySet()) {
                    Document doc= docMap.get(Integer.valueOf(entry.getKey()));
                    doc.setScore(doc.getScore()+entry.getValue());
                }
            }
            //默认升序，这个降序
            docList.sort(Comparator.comparingDouble(Document::getScore).reversed());
            Log.info("===搜搜耗时：{}ms", (System.currentTimeMillis() - startTime)); // 记录开始时间);
        }finally {
            lock.readLock().unlock();
        }
        return docList;
    }

}
