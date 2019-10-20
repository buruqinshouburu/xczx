package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 从队列中获取信息，将页面保存到服务器的具体路径
     * @param pageId
     * @return
     */
    public void publicHtml(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(optional.isPresent()){
            //获取服务器文件真实地址
            CmsPage cmsPage = optional.get();
            String Path = getRealPath(cmsPage.getSiteId())+cmsPage.getPagePhysicalPath()+cmsPage.getPageName();
            //从GridFs中下载静态页面
            downloadHtml(cmsPage.getHtmlFileId(),Path);

        }
    }

    /**
     * 下载静态化页面
     * @param htmlFileId
     * @param path
     */
    private void downloadHtml(String htmlFileId, String path) {
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        FileOutputStream fileOutputStream=null;
        InputStream inputStream = null;
        if(file==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        try {
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getObjectId());
            GridFsResource gridFsResource=new GridFsResource(file,gridFSDownloadStream);
            inputStream = gridFsResource.getInputStream();
            // String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
             fileOutputStream=new FileOutputStream(new File(path));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
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
    }

    /**
     * 查找页面要保存的路径
     * @param siteId
     * @return
     */
    private String getRealPath(String siteId) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if (optional.isPresent()){
            CmsSite cmsSite = optional.get();
            String sitePhysicalPath = cmsSite.getSitePhysicalPath();
            return sitePhysicalPath;
        }
        return null;
    }
}
