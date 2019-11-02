package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MediaProcessTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaProcessTask.class);
    @Autowired
    private MediaFileRepository mediaFileRepository;
    @Value("${xc-service-manage-media.video-location}")
    String video_location;
    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;
    //找到文件地址
    private String findPath(MediaFile mediaFile) {
        return video_location+mediaFile.getFilePath()+mediaFile.getFileName();
    }
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg){
        Map msgMap = JSON.parseObject(msg, Map.class);
        LOGGER.info("receive media process task msg :{} ",msgMap);
        //解析消息         
        // 媒资文件id
        String  mediaId = (String) msgMap.get("mediaId");
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if(!optional.isPresent()) return;
        MediaFile mediaFile = optional.get();
        //找到mp4文件地址
        String path=findPath(mediaFile);
        File file = new File(path);
        if(!file.exists()) return;
        if(mediaFile.getFileType()==null||!mediaFile.getFileType().equals("mp4")&&!mediaFile.getFileType().equals("avi")){
            mediaFile.setProcessStatus("303004");
            mediaFileRepository.save(mediaFile);
        }else {
            mediaFile.setProcessStatus("303001");
            mediaFileRepository.save(mediaFile);
        }
        //文件转换 avi->mp4->m3u8
        boolean status = false;
        if (mediaFile.getFileType().equalsIgnoreCase("mp4")) {
            status = transformMp4(mediaFile);
        } else if (mediaFile.getFileType().equalsIgnoreCase("avi")) {
            status = transformAvi(mediaFile);
        }
        if(status){
            mediaFile.setProcessStatus("303002");
        }else mediaFile.setProcessStatus("303003");
        mediaFileRepository.save(mediaFile);
    }

    private boolean transformAvi(MediaFile mediaFile) {
        //ffmpeg的安装位置
        //文件所在位置
        String video_path = findPath(mediaFile);
        //生成的mp4文件名字
        String mp4_name = mediaFile.getFileId()+".mp4";
        //m3u8文件存储地址
        String mp4folder_path = findPath(mediaFile);
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_path);
        String s = mp4VideoUtil.generateMp4();
        if(s.equals("success")) {
            return transformMp4(mediaFile);
        }
        MediaFileProcess_m3u8 mediaFileProcess_m3u8=new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setErrormsg(s);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
       // mediaFileRepository.save(mediaFile);
        return false;
    }

    private boolean transformMp4(MediaFile mediaFile) {
       //ffmpeg的安装位置
        //文件所在位置

        String video_path =  findPath(mediaFile);
        //生成的m3u8文件名字
        String m3u8_name = mediaFile.getFileId()+".m3u8";
        //m3u8文件存储地址
        String m3u8folder_path = video_location+mediaFile.getFilePath()+"hls/";
        File folder = new File(m3u8folder_path);
        if(folder.exists()) folder.mkdirs();
        HlsVideoUtil hlsVideoUtil=new HlsVideoUtil(ffmpeg_path,video_path,m3u8_name,m3u8folder_path);
        String s = hlsVideoUtil.generateM3u8();
        MediaFileProcess_m3u8 mediaFileProcess_m3u8=new MediaFileProcess_m3u8();
        if(s.equals("success")) {
            List<String> ts_list = hlsVideoUtil.get_ts_list();
            mediaFileProcess_m3u8.setTslist(ts_list);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            //m3u8文件url
            mediaFile.setFileUrl(mediaFile.getFilePath()+"hls/"+m3u8_name);
            //mediaFileRepository.save(mediaFile);
            return true;
        }
        mediaFileProcess_m3u8.setErrormsg(s);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
       // mediaFileRepository.save(mediaFile);
        return false;
    }

}
