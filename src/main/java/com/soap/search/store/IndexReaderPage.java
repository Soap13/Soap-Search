package com.soap.search.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.rmi.server.ExportException;

public class IndexReaderPage extends IndexInput {
    private static final Logger Log = LogManager.getLogger(IndexReaderPage.class);
    RandomAccessFile raf;

    private byte[] currentBuffer;
    private int currentBufferIndex;

    private int bufferStart;
    private long bufferLength;
    private long fileLength;

    public IndexReaderPage(String fieldPath) throws IOException {
        File f=new File(fieldPath);
        if(f.exists()) {
            raf = new RandomAccessFile(fieldPath, "r");
        }else{
            Log.error("词频文件:{},不存在请创建",fieldPath);
            throw new ExportException("不存在文件:"+fieldPath);
        }
        //currentBuffer = new byte[(int)f.length()];
        currentBufferIndex = 0;
        bufferLength = 0;
        bufferStart = 0;
        fileLength=f.length();
        readAllFile();
    }
    @Override
    public byte readByte() throws IOException {
        bufferLength++;
        return currentBuffer[(int)bufferLength-1];
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
        bufferLength += len;
        if(bufferLength<=fileLength){
            raf.read(b, offset, len);
            System.arraycopy(currentBuffer, (int)bufferLength - len, b, offset,len);
        }
    }

    public void readAllFile() throws IOException {
        long startTime = System.currentTimeMillis(); // 记录开始时间
        long position = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (position < fileLength) {
            // 对齐到页边界
            long alignedPos = alignToPage(position);
            // 读取一页大小数据
            ByteBuffer buffer = ByteBuffer.allocate(IndexWriter.BUFFER_SIZE);
            raf.getChannel().position(alignedPos);
            int bytesRead = raf.getChannel().read(buffer);

            // 处理 buffer 数据...
            if (bytesRead > 0) {
                buffer.flip();
                position+=buffer.remaining();
                appendBuffer(baos, buffer);
               // Log.info("读取位置:{},实际读取字节数:{}",alignedPos, bytesRead);
            }
            position = alignedPos + IndexWriter.BUFFER_SIZE; // 移动到下一页
        }
        currentBuffer=baos.toByteArray();
        Log.info("===文件提取耗时：{}ms",(System.currentTimeMillis()-startTime)); // 记录开始时间);
    }

    private static void appendBuffer(ByteArrayOutputStream output, ByteBuffer buffer) {
        byte[] array = new byte[buffer.remaining()];
        buffer.get(array);
        output.write(array, 0, array.length);
    }
    private static long alignToPage(long offset) {
        return (offset / IndexWriter.BUFFER_SIZE) * IndexWriter.BUFFER_SIZE;
    }

    @Override
    public void close() throws IOException {
          if(null!=raf){
              raf.close();
          }
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