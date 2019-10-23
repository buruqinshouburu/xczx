package com.xuecheng.filesystem.controller;

import com.xuecheng.api.file.filesystemControllerApi;
import com.xuecheng.filesystem.service.fileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.soap.Addressing;

@RestController
@RequestMapping("/filesystem")
public class fileSystemController implements filesystemControllerApi {
    @Autowired
    private fileSystemService fileSystemService;
    @Override
    @PostMapping("/upload")
    public UploadFileResult updateFile(@RequestParam("file") MultipartFile multipartFile,
                                       @RequestParam(value = "businesskey",required = false) String businesskey,
                                       @RequestParam(value = "filetag",required = true) String filetag,
                                       @RequestParam(value = "metadate",required = false) String metadata) {

        return fileSystemService.updateFile(multipartFile,businesskey,filetag,metadata);
    }
}
