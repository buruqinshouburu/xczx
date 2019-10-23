package com.xuecheng.api.file;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface filesystemControllerApi {
    UploadFileResult updateFile(MultipartFile multipartFile, String businesskey, String filetag, String metadata);
}
