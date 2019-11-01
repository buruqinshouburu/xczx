package com.xuecheng.manage_media;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FileTest {
    /**
     * 视频文件拆分
     * @throws IOException
     */
    @Test
    public void testchunk() throws IOException {
        //源文件地址
        File sourceFile = new File("F:\\develop\\lucene.mp4");
        //分块目录地址
        String chunkPath="F:\\develop\\chunk\\";
        File chunkFolder = new File(chunkPath);
        if(!chunkFolder.exists()){
            chunkFolder.mkdirs();
        }
        //分块大小
        long chunkSize=1024*1024*8;
        //分块数量
        long ceil = (long) Math.ceil(sourceFile.length()* 1.0 / chunkSize );
        if(ceil<=0) ceil=1;
        //缓冲区大小
        byte[] bys=new byte[1024*8];
        //使用RandomAccessFile访问文件
        RandomAccessFile raf_read=new RandomAccessFile(sourceFile,"r");
        //开始分块
        for (long i = 0; i < ceil; i++) {
            //创建分块文件
            File file = new File(chunkPath + i);
            boolean newFile = file.createNewFile();
            if(newFile){
                //写文件
                RandomAccessFile raf_write=new RandomAccessFile(file,"rw");
                int len=-1;
                while ((len=raf_read.read(bys))!=-1){
                    raf_write.write(bys,0,len);
                    if(file.length()>chunkSize) break;
                }
                raf_write.close();
            }
        }
        raf_read.close();
    }

    /**
     * 视频文件合并
     */
    @Test
    public void testMerge() throws IOException {
        //块目录文件
        File chunkFolder = new File("F:\\develop\\chunk\\");
        //合并文件
        File mergeFile = new File("F:\\develop\\merge.mp4");
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write=new RandomAccessFile(mergeFile,"rw");
        //用指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] bys=new byte[1024*8];
        //分块列表 拿到所有分块文件
        File[] fileArray = chunkFolder.listFiles();
        //转成集合便于排序
        ArrayList<File> fileList = new ArrayList<>(Arrays.asList(fileArray));
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){ return 1;}
               return -1;
            }
        });
        //合并文件
        for (File filechunk : fileList) {
            RandomAccessFile raf_read=new RandomAccessFile(filechunk,"r");
                int len =-1;
               while ((len=raf_read.read(bys))!=-1){
                   raf_write.write(bys,0,len);
               }
               raf_read.close();
            }
            raf_write.close();
        }
    }

