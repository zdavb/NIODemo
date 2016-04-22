package com.dacas.main;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * Created by dave on 2016/4/22.
 */
//使用NIO去读取文件
public class Main {
    public static void main(String[] args) {
        makeStatics("copyFileWithBuffered");
        makeStatics("copyFileWithChannel");
        makeStatics("copyFileWithMMap");
    }
    public static void makeStatics(String methodName){
        try {
            String originalPath = "C:\\Users\\dave\\Desktop\\nikita.mp4";
            String targetPath = "C:\\Users\\dave\\Desktop\\nikita2.mp4";
            Method method = Main.class.getMethod(methodName, String.class, String.class);
            long startTime = new Date().getTime();
            for(int i = 0;i<10;i++)
                method.invoke(null, originalPath, targetPath);
            long endTime = new Date().getTime();
            System.out.println(methodName+" consume average time:"+(endTime-startTime)/10+"ms");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static void copyFileWithChannel(String original,String target){
        try {
            FileInputStream input = new FileInputStream(original);
            FileOutputStream out = new FileOutputStream(target);

            ByteChannel inputChannel = input.getChannel();
            ByteChannel outputChannel = out.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(4096);
            while (inputChannel.read(buffer)!=-1){
                buffer.flip();
                outputChannel.write(buffer);
                buffer.flip();
            }
            input.close();
            out.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    //利用BufferedReader和BufferedWriter来复制文件
    public static void copyFileWithBuffered(String original,String target){
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(original));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(target));

            byte[] bytes = new byte[4096];
            int count;
            while ((count = inputStream.read(bytes))!=-1){
                outputStream.write(bytes, 0, count);
            }
            inputStream.close();
            outputStream.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }

    }
    //利用Linux的MMap来实现，java在NIO中提供了RandomAccessFile来实现这个功能
    public static void copyFileWithMMap(String original,String target){
        try {
            File originalFile = new File(original);
            RandomAccessFile input = new RandomAccessFile(originalFile,"r");
            RandomAccessFile output = new RandomAccessFile(target,"rw");

            MappedByteBuffer inputbuffer = input.getChannel().map(FileChannel.MapMode.READ_ONLY,0,originalFile.length());
            MappedByteBuffer outBuffer = output.getChannel().map(FileChannel.MapMode.READ_WRITE,0,originalFile.length());

            while(inputbuffer.hasRemaining()){
                byte ch = inputbuffer.get();
                outBuffer.put(ch);
            }
            input.close();
            output.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
