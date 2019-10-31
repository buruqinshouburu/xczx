package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.controller.MediaUploadController;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class mediaService {
    private final static Logger LOGGER = LoggerFactory.getLogger(MediaUploadController .class);
    @Value("${xc-service-manage-media.upload-location}")
    String upload_path;
    @Autowired
    MediaFileRepository mediaFileRepository;
    /**   
     * 得到文件路径   
     * 根据文件md5得到文件路径      
     * 规则：   
     *  一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     * @param fileMd5 文件md5值
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    private String getFilePath(String fileMd5, String fileExt) {
        return upload_path+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+"."+fileExt;
    }
    //创建文件目录
    private boolean createFileFolder(String fileMd5) {
        //创建上传文件目录
        String fileFolderPath=getFileFolder(fileMd5);
        File fileFolder = new File(fileFolderPath);
        if(!fileFolder.exists()){
            //创建文件夹
            boolean mkdirs = fileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }
    //得到文件所在目录
    private String getFileFolder(String fileMd5) {
        return upload_path+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/";
    }
    //得到块文件所在路径
    private String getChunkfileFolderPath(String fileMd5) {
        return getFileFolder(fileMd5)+"/"+"chunks"+"/";
    }
    //创建块文件目录
    private boolean createChunkFileFolder(String fileMd5) {
        String chunckFileFolder=getChunkfileFolderPath(fileMd5);
        File file = new File(chunckFileFolder);
        boolean mkdirs=false;
        if(!file.exists()) {
            mkdirs = file.mkdirs();
            return mkdirs;
        }
        return true;
    }
    //块文件排序
    private List<File> getChunkFiles(File chunkfileFold) {
        File[] listFiles = chunkfileFold.listFiles();
        List<File> chunkFiles=new ArrayList<File>(Arrays.asList(listFiles));
        Collections.sort(chunkFiles, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName()))
                return 1;
                return -1;
            }
        });
        return chunkFiles;
    }
    //合并文件
    private File mergeFile(File mergeFile, List<File> chunkFiles) {
        RandomAccessFile rof_write=null;
        byte[] bys=new byte[1024*8];
        try {
            rof_write=new RandomAccessFile(mergeFile,"rw");
            for (File chunkFile : chunkFiles) {
                RandomAccessFile rof_read=new RandomAccessFile(chunkFile,"r");
                int len=-1;
                while ((len=rof_read.read(bys))!=-1){
                    rof_write.write(bys,0,len);
                }
                rof_read.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                rof_write.close();
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("merge file error:{}",e.getMessage());
                return null;
            }
        }
        return mergeFile;
    }
    //md5校验
    private boolean cheakFileMd5(File mergeFile, String fileMd5) {
        if(mergeFile==null|| StringUtils.isEmpty(fileMd5)) return false;
        //进行md5校验
        FileInputStream mergeFileInputstream=null;
        try {
            mergeFileInputstream=new FileInputStream(mergeFile);
            //得到文件的md5
            String s = DigestUtils.md5Hex(mergeFileInputstream);
            //比较md5
            if(s.equals(fileMd5)){
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("checkFileMd5 error,file is:{},md5 is: {}",mergeFile.getAbsoluteFile(),fileMd5);
        } finally {
            try {
                mergeFileInputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //获取文件相对路径
    private String getFileFolderRelativePath(String fileMd5, String fileExt) {
        return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/";
    }


    //文件注册上传
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //检测文件是否上传
        //1.得到文件路径
       String filePath= getFilePath(fileMd5,fileExt);
        File file = new File(filePath);
        //2、查询数据库文件是否存在
        Optional<MediaFile> byId = mediaFileRepository.findById(fileMd5);
        if(byId.isPresent()&&file.exists())  ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        boolean fileFolder=createFileFolder(fileMd5);
        if(!fileFolder){
            //目录创建失败
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_CREATEFOLDER_FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //文件注册上分块检查
    public CheckChunkResult chunChunk(String fileMd5, Integer chunk, Long chunkSize) {
        //得到块文件所在路径
        String chunkfileFolderPath=getChunkfileFolderPath(fileMd5);
        //块文件的文件名称以1,2,3..序号命名，没有扩展名
        File file = new File(chunkfileFolderPath + "chunk");
        if(file.exists()){
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK,true);
        }
        return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK,false);
    }

    //上传文件
    public ResponseResult uploadChunk(MultipartFile file, Integer chunk, String fileMd5)  {
        if(file==null) ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_ISNULL);
        //创建块文件目录
        boolean chunkFileFolder = createChunkFileFolder(fileMd5);
        //块文件
        File chunkFile = new File(getChunkfileFolderPath(fileMd5) + chunk);
        //上传的块文件
        InputStream inputStream=null;
        FileOutputStream fileOutputStream=null;
        try {
            inputStream = file.getInputStream();
            fileOutputStream=new FileOutputStream(chunkFile);
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("upload chunk file fail:{}",e.getMessage());
            ExceptionCast.cast(MediaCode.CHUNK_FILE_UPLOAD_FAIL);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       return new ResponseResult(CommonCode.SUCCESS);
    }
   /* 1）将块文件合并
      2）校验文件md5是否正确
      3）向Mongodb写入文件信息
      */
    //合并分块
    public ResponseResult mergeChunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //获取块文件的路径
        String chunkfileFolderPath = getChunkfileFolderPath(fileMd5);
        File chunkfileFold = new File(chunkfileFolderPath);
        if (!chunkfileFold.exists()){
            chunkfileFold.mkdirs();
        }
        //合并文件路径
        File mergeFile = new File(getFilePath(fileMd5, fileExt));
        //创建合并文件
        // 合并文件存在先删除再创建
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        boolean newFile=false;
        try {
            newFile = mergeFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("mergechunks..create mergeFile fail:{}",e.getMessage());
        }
        if(!mergeFile.exists()) ExceptionCast.cast(MediaCode.MERGE_FILE_CREATEFAIL);
        //获取块文件，此列表是已经排好序的列表
        List<File> chunkFiles = getChunkFiles(chunkfileFold);
        //合并文件
         mergeFile = mergeFile(mergeFile, chunkFiles);
         if(mergeFile==null) ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        //校验文件
        boolean checkResult=cheakFileMd5(mergeFile,fileMd5);
        if(!checkResult) ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        //将文件信息保存到数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        //文件路径保存相对路径
        mediaFile.setFilePath(getFileFolderRelativePath(fileMd5,fileExt));
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        //状态为上传成功
        mediaFile.setFileStatus("301002");
        MediaFile save = mediaFileRepository.save(mediaFile);
        return new ResponseResult(CommonCode.SUCCESS);
    }



}
