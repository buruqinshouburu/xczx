package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaUploadControllerApi;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.mediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/media/upload")
public class MediaUploadController implements MediaUploadControllerApi {
    @Autowired
    private mediaService mediaService;
    @Override
    @PostMapping("/register")
    public ResponseResult Register(@RequestParam("fileMd5") String fileMd5,
                                   @RequestParam("fileName") String fileName,
                                   @RequestParam("fileSize") Long fileSize,
                                   @RequestParam("mimetype" )String mimetype,
                                   @RequestParam("fileExt" )String fileExt) {
        return mediaService.register(fileMd5,fileName,fileSize,mimetype,fileExt);
    }

    @Override
    @PostMapping("/checkchunk")
    public CheckChunkResult checkChunk(@RequestParam("fileMd5")String fileMd5,
                                       @RequestParam("chunk") Integer chunk,
                                       @RequestParam("fileSize") Long chunkSize) {
        return mediaService.chunChunk(fileMd5,chunk,chunkSize);
    }

    @Override
    @PostMapping("/uploadchunk")
    public ResponseResult uploadChunk(@RequestParam("file") MultipartFile file,
                                      @RequestParam("chunk") Integer chunk,
                                      @RequestParam("fileMd5") String fileMd5) {
        return mediaService.uploadChunk(file,chunk,fileMd5);
    }

    @Override
    @PostMapping("/mergechunks")
    public ResponseResult mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                      @RequestParam("fileName")String fileName,
                                      @RequestParam("fileSize")Long fileSize,
                                      @RequestParam("mimetype") String mimetype,
                                      @RequestParam("fileExt" )String fileExt) {
        return mediaService.mergeChunks(fileMd5,fileName,fileSize,mimetype,fileExt);
    }
}
