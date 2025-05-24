package com.soap.search.store;

import java.io.*;
import java.util.Arrays;

public class IndexWriter extends IndexOutput {
    public static final String WRITE_LOCK_NAME = "write.lock";

    private OutputStream outputStream;

    static final int BUFFER_SIZE = 1024;

    private byte[] currentBuffer;
    private int currentBufferIndex;
    private long bufferStart;
    private long bufferLength;
    public IndexWriter(String fieldPath,boolean isAppend) throws FileNotFoundException {
        File file=new File(fieldPath);
        this.outputStream=new FileOutputStream(fieldPath,isAppend);
        currentBuffer=new byte[BUFFER_SIZE];
        currentBufferIndex=0;
        bufferLength=0;
        bufferStart=isAppend?file.length():0;
    }
    @Override
    public void writeByte(byte b) throws IOException {
        if (currentBufferIndex == currentBuffer.length) {
            flush();
        }
        currentBuffer[currentBufferIndex++] = b;
        bufferLength++;
    }
    @Override
    public void writeBytes(byte[] b, int offset, int len) throws IOException {
        assert b != null;
        while (len > 0) {
            if(currentBufferIndex==currentBuffer.length){
                flush();
            }
            int remainInBuffer = currentBuffer.length - currentBufferIndex;
            int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
            System.arraycopy(b, offset, currentBuffer, currentBufferIndex, bytesToCopy);
            offset += bytesToCopy;
            len -= bytesToCopy;
            currentBufferIndex += bytesToCopy;
            bufferLength+=bytesToCopy;
        }
    }

    @Override
    public void flush() throws IOException {
       outputStream.write(currentBuffer,0,currentBufferIndex);
       currentBuffer= Arrays.copyOf(new byte[0], BUFFER_SIZE);
       currentBufferIndex=0;
    }

    @Override
    public void close() throws IOException {
        flush();
        outputStream.close();
    }

    @Override
    public long getFilePointer() {
        return bufferStart+bufferLength;
    }

    @Override
    public void seek(long pos) throws IOException {
        System.out.println("暂不支持位置写入...");
    }

    @Override
    public long length() throws IOException {
        return bufferLength;
    }
}