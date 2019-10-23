package com.xuecheng.filesystem.service;


import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.fileSystemReposiyory;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class fileSystemService {
    @Autowired
    private fileSystemReposiyory fileSystemReposiyory;
    @Value("${xuecheng.fastdfs.tracker_servers}")
    private String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    private int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    private int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    private String charset;
    public UploadFileResult updateFile(MultipartFile multipartFile, String businesskey, String filetag, String metadata){
        if(multipartFile==null) ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        String fileId = fastDFSUpdate(multipartFile);
        FileSystem fileSystem = saveFileInMongodb(multipartFile, businesskey, filetag, metadata, fileId);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    private FileSystem saveFileInMongodb(MultipartFile multipartFile, String businesskey, String filetag, String metadata, String fileId) {
        Map map =null;
        if(StringUtils.isEmpty(fileId)) ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        if(!StringUtils.isEmpty(metadata)) {
             map = JSON.parseObject(metadata, Map.class);
        }
        FileSystem fileSystem=new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setMetadata(map);
        fileSystem.setFileName(multipartFile.getName());
        fileSystem.setFiletag(filetag);
        fileSystem.setFileSize(multipartFile.getSize());
        fileSystem.setFileType(multipartFile.getContentType());
        fileSystemReposiyory.save(fileSystem);
        return fileSystem;
    }

    private String fastDFSUpdate(MultipartFile multipartFile) {
        fastdfsConfig();
        TrackerClient trackerClient = new TrackerClient();
        try {
            TrackerServer connection = trackerClient.getConnection();
            StorageServer storeStorage = trackerClient.getStoreStorage(connection);
            StorageClient1 storageClient1 = new StorageClient1(connection, storeStorage);
            String originalFilename = multipartFile.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileId = storageClient1.upload_file1(multipartFile.getBytes(), ext, null);
            System.out.println("fileId="+fileId);
            return fileId;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fastdfsConfig(){

        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
