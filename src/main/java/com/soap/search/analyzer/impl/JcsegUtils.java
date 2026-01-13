package com.soap.search.analyzer.impl;

import com.soap.search.analyzer.AnalyzerInterface;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.DictionaryFactory;
import org.lionsoul.jcseg.extractor.SummaryExtractor;
import org.lionsoul.jcseg.extractor.impl.TextRankSummaryExtractor;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;
import org.lionsoul.jcseg.sentence.SentenceSeg;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JcsegUtils implements AnalyzerInterface {
    private static final SegmenterConfig config;
    private static final ADictionary dic;
    private static final ISegment seg;

    static {
        // 创建配置实例,使用默认配置传递true
        config = new SegmenterConfig(true);

        // 解码获取正确的中文路径
        String lexiconPath = URLDecoder.decode(
                JcsegUtils.class.getClassLoader().getResource("lexicon").getPath(),
                StandardCharsets.UTF_8
        );
        System.out.println(lexiconPath);
        //2, 设置自定义词库路径集合
//        config.setLexiconPath(new String[]{
//                lexiconPath+"/add.lex",
////                lexiconPath+"/lex-admin.lex",
//        });

        config.setClearStopwords(true);    //设置不过滤停止词
        config.setAppendCJKSyn(false);      //设置关闭同义词追加
        config.setKeepUnregWords(false);    //设置去除不识别的词条
        config.setEnSecondSeg(false);       //关闭英文自动二次切分

        //创建默认单例词库实现，并且按照config配置加载词库
        dic = DictionaryFactory.createSingletonDictionary(config);
        //依据给定的ADictionary和SegmenterConfig来创建ISegment

        //1, 通过ISegment.XX_MODE参数
        //ISegment.COMPLEX_MODE表示创建ComplexSeg复杂ISegment分词对象
        //ISegment.SIMPLE_MODE表示创建SimpleSeg简易Isegmengt分词对象.
        //ISegment.DETECT_MODE表示创建DetectSeg Isegmengt分词对象.
        //ISegment.SEARCH_MODE表示创建SearchSeg Isegmengt分词对象.
        //ISegment.DELIMITER_MODE表示创建DelimiterSeg Isegmengt分词对象.
        //ISegment.NLP_MODE表示创建NLPSeg Isegmengt分词对象.
        //ISegment.NGRAM_MODE表示创建NGramSeg Isegmengt分词对象.
        //ISegment seg = ISegment.Type.fromIndex(mode).factory.create(config, dic);


        //2, 通过调用直接的模式函数
        // ISegment.COMPLEX为指向ComplexSeg的构造器函数接口
        // ISegment.SIMPLE为指向ComplexSeg的构造器函数接口
        // ISegment.DETECT为指向ComplexSeg的构造器函数接口
        // ISegment.MOST为指向ComplexSeg的构造器函数接口
        // ISegment.DELIMITER为指向ComplexSeg的构造器函数接口
        // ISegment.NLP为指向ComplexSeg的构造器函数接口
        // ISegment.NGRAM为指向ComplexSeg的构造器函数接口
        seg = ISegment.NLP.factory.create(config, dic);
    }

    /**
     * 分词
     * @param text 文本
     * @return 分词结果
     * @throws IOException io异常
     */
    public static String wordSegmentationTest(String text) throws IOException {
        //备注：以下代码可以反复调用，seg为非线程安全
        //设置要被分词的文本
        seg.reset(new StringReader(text));
        //获取分词结果
        IWord word;
        StringBuilder sb = new StringBuilder();
        while ((word = seg.next()) != null) {
            sb.append(word.getValue()).append("|");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 分词
     * @param text 文本
     * @return 分词结果
     * @throws IOException io异常
     */
    @Override
    public  Map<String,ArrayList<Integer>> StringAnalyzer(String text) throws IOException {
        Map<String, ArrayList<Integer>> termMap=new HashMap<String, ArrayList<Integer>>();
        //备注：以下代码可以反复调用，seg为非线程安全
        //设置要被分词的文本
        seg.reset(new StringReader(text));
        //获取分词结果
        IWord word;
        StringBuilder sb = new StringBuilder();
        while ((word = seg.next()) != null) {
            termMap.computeIfAbsent(word.getValue(), k -> new ArrayList<>())
                    .add(word.getPosition());
        }
        return termMap;
    }

    @Override
    public Set<String> strAnalyzer(String str)throws IOException{
        Set<String>wordSet=new HashSet<>();
        seg.reset(new StringReader(str));
        //获取分词结果
        IWord word;
        while ((word = seg.next()) != null) {
            wordSet.add(word.getValue());
        }
        return wordSet;
    }
    /**
     * 截取摘要
     * @param text 文本
     * @param length 摘要长度
     * @return 摘要
     * @throws IOException io异常
     */
    public static String getSummary(String text, int length) throws IOException {
        SummaryExtractor extractor = new TextRankSummaryExtractor(seg, new SentenceSeg());
        return extractor.getSummary(new StringReader(text), length);
    }

    public static void main(String[] args) throws IOException {
        String text = JcsegUtils.wordSegmentationTest("taiyang福贵我是张三,张三，小程序来自黑龙江哈尔滨哈尔冰。性别男，爱好女，联系方式16666661234。");
        System.out.println(text);

        String summary = JcsegUtils.getSummary("路上只我一个人，背着手踱着。这一片天地好像是我的;我也像超出了平常旳自己，到了另一世界里。我爱热闹，也爱冷静;爱群居，也爱独处。像今晚上，一个人在这苍茫旳月下，什么都可以想，什么都可以不想，便觉是个自由的人。白天里一定要做的事，一定要说的话，现在都可不理。这是独处的妙处，我且受用这无边的荷香月色好了。\n" +
                "\n" +
                "曲曲折折的荷塘上面，弥望旳是田田的叶子。叶子出水很高，像亭亭旳舞女旳裙。层层的叶子中间，零星地点缀着些白花，有袅娜(niǎo,nuó)地开着旳，有羞涩地打着朵儿旳;正如一粒粒的明珠，又如碧天里的星星，又如刚出浴的美人。微风过处，送来缕缕清香，仿佛远处高楼上渺茫的歌声似的。这时候叶子与花也有一丝的颤动，像闪电般，霎时传过荷塘的那边去了。叶子本是肩并肩密密地挨着，这便宛然有了一道凝碧的波痕。叶子底下是脉脉(mò)的流水，遮住了，不能见一些颜色;而叶子却更见风致了。\n" +
                "\n" +
                "月光如流水一般，静静地泻在这一片叶子和花上。薄薄的青雾浮起在荷塘里。叶子和花仿佛在牛乳中洗过一样;又像笼着轻纱的梦。虽然是满月，天上却有一层淡淡的云，所以不能朗照;但我以为这恰是到了好处——酣眠固不可少，小睡也别有风味的。月光是隔了树照过来的，高处丛生的灌木，落下参差的斑驳的黑影，峭楞楞如鬼一般;弯弯的杨柳的稀疏的倩影，却又像是画在荷叶上。塘中的月色并不均匀;但光与影有着和谐的旋律，如梵婀(ē)玲(英语violin小提琴的译音)上奏着的名曲。\n" +
                "\n" +
                "荷塘的四面，远远近近，高高低低都是树，而杨柳最多。这些树将一片荷塘重重围住;只在小路一旁，漏着几段空隙，像是特为月光留下的。树色一例是阴阴的，乍看像一团烟雾;但杨柳的丰姿，便在烟雾里也辨得出。树梢上隐隐约约的是一带远山，只有些大意罢了。树缝里也漏着一两点路灯光，没精打采的，是渴睡人的眼。这时候最热闹的，要数树上的蝉声与水里的蛙声;但热闹是它们的，我什么也没有。", 64);
        System.out.println(summary);
    }
}