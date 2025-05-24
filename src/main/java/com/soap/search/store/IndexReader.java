package com.soap.search.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class IndexReader extends IndexInput {
    private static final Logger Log = LogManager.getLogger(IndexReader.class);
    RandomAccessFile raf;

    private byte[] currentBuffer;
    private int currentBufferIndex;

    private long bufferStart;
    private long bufferLength;
    private long fileLength;

    public IndexReader(String fieldPath) throws IOException {
        raf = new RandomAccessFile(fieldPath, "r");
        currentBuffer = new byte[IndexWriter.BUFFER_SIZE];
        currentBufferIndex = 0;
        bufferLength = 0;
        bufferStart = 0;
        File file=new File(fieldPath);
        fileLength=file.length();
    }
    @Override
    public byte readByte() throws IOException {
        bufferLength++;
        return raf.readByte();
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
        bufferLength += len;
        raf.read(b, offset, len);
    }

    @Override
    public void close() throws IOException {
          raf.close();
    }

    @Override
    public long getFilePointer() {
        return fileLength;
    }

    @Override
    public void seek(long pos) throws IOException {
        raf.seek(pos);
    }

    @Override
    public long length() {
        return bufferLength;
    }


}