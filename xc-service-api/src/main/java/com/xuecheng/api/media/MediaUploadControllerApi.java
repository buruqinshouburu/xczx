package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value ="媒资管理接口",description ="媒资管理接口，提供文件上传，文件处理等接口")
public interface MediaUploadControllerApi {
//    fileMd5:this.fileMd5,
//    fileName: file.name,
//    fileSize:file.size,
//    mimetype:file.type,
//    fileExt:file.ext
    @ApiOperation("文件注册上传")
    public ResponseResult Register(String fileMd5,String fileName,Long fileSize,String mimetype,String fileExt);

    // 文件唯一表示
    //fileMd5:this.fileMd5,
    // 当前分块下标
    //chunk:block.chunk,
    // 当前分块大小
    //chunkSize:block.end-block.start
    @ApiOperation("文件注册上分块检查")
    public CheckChunkResult checkChunk(String fileMd5,Integer chunk,Long chunkSize);
    @ApiOperation("上传文件")
    public ResponseResult uploadChunk(MultipartFile file,Integer chunk,String fileMd5);
    @ApiOperation("合并分块")
    public  ResponseResult mergeChunks(String fileMd5,String fileName,Long fileSize,String mimetype,String fileExt);
}
