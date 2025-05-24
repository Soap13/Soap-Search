package com.soap.search.document;

public class Index {
    /**
     * 每个类型对应一个 bit位置
     * 0 0 0 0 0 0 0 0
     * Field.Index.NO 则表示不被索引。 Field.Store.Yes 则表示存储此域，Field.Store.NO 则表示不存储此域
     * Field.Index.ANALYZED 则表示不但被索引，
     * Field.Index.NOT_ANALYZED 表示虽然被索引，但是不分词
     *  倒数第二位：1 表示保存词向量，0 为不保存词向量 Field.TermVector.YES 表示保存词向量。  Field.TermVector.NO 表示不保存词向量
     *  倒数第三位：1 表示在词向量中保存位置信息 Field.TermVector.WITH_POSITIONS
     *  倒数第四位：1 表示在词向量中保存偏移量信息 Field.TermVector.WITH_OFFSETS
     *  倒数第五位：1 表示不保存标准化因子 Field.Index.ANALYZED_NO_NORMS Field.Index.NOT_ANALYZED_NO_NORMS
     *  倒数第六位：是否保存 payload
     */
    public static final byte NO = (byte) (0); // 00000001
    public static final byte ANALYZED = (byte) (0b1); // 00000010
    public static final byte TOKENIZED = ANALYZED;
    public static final byte NOT_ANALYZED = (byte) (1 << 3); // 00001000
    public static final byte UN_TOKENIZED = NOT_ANALYZED;
    public static final byte NOT_ANALYZED_NO_NORMS = (byte) (1 << 5); // 00100000
    public static final byte NO_NORMS = NOT_ANALYZED_NO_NORMS;
    public static final byte ANALYZED_NO_NORMS = (byte) (1 << 5); // 10000000

    public static boolean hasFlag(byte allFlags, byte flagToCheck) {
        return (allFlags & flagToCheck) != 0;
    }
}
