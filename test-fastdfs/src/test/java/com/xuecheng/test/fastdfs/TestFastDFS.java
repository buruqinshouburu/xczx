package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {
    //文件上传
    @Test
    public void test01() {
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);
            //创建客户端
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker Server
            TrackerServer connection = trackerClient.getConnection();
            if(connection==null){
                System.out.println("getConnection return null");
                return;
            }
            //获取一个storage server
            StorageServer storeStorage = trackerClient.getStoreStorage(connection);
            if(storeStorage==null){
                System.out.println("getStoreStorage return null");
            }
            //创建一个storage存储客户端
            StorageClient1 storageClient = new StorageClient1(connection,storeStorage);
            NameValuePair[] meta_list = null; //new NameValuePair[0];
            String item="G:\\chromedownload\\4.jpg";
            String fileid = storageClient.upload_file1(item, "jpeg", meta_list);
            //fileid=group1/M00/00/00/rBApMl2vv6KAJBHzAAMt7JLCTBc87.jpeg
            System.out.println("Upload local file " + item + " ok, fileid=" + fileid);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
    //文件查询
    @Test
    public void test02(){
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient tc = new TrackerClient();
            TrackerServer connection = tc.getConnection();
            StorageServer storageServer=null;
            StorageClient storageClient = new StorageClient(connection,storageServer);
            FileInfo group1 = storageClient.query_file_info("group1", "M00/00/00/rBApMl2vv6KAJBHzAAMt7JLCTBc87.jpeg");
            System.out.println(group1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
    //下载文件
    @Test
    public void test03(){
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();
            StorageServer storageServer=null;
            StorageClient storageClient = new StorageClient(connection, storageServer);
            byte[] group1s = storageClient.download_file("group1", "M00/00/00/rBApMl2vv6KAJBHzAAMt7JLCTBc87.jpeg");
            FileOutputStream fos=new FileOutputStream(new File("G:\\3.jpeg"));
            fos.write(group1s);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

}
