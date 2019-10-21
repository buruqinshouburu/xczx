package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.service.PageService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class demo {
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    PageService pageService;
    static String fileId="5da87a7462aa4d6264ac7762";
    @Test
    public void test01(){
        List<CmsPage> all = cmsPageRepository.findAll();
        for (CmsPage cmsPage : all) {
            System.out.println(cmsPage);
        }

    }
    @Test
    public void test02(){
        Pageable pageable= PageRequest.of(0,10);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        for (CmsPage cmsPage : all) {
            System.out.println(cmsPage);
        }
    }

    /**
     * 使用GridFsTemplate存储文件测试代码
     */
    @Test
    public void test04() throws FileNotFoundException {
        //要存储的文件
        File file=new File("F:\\java\\xczx\\testfreemarker\\src\\main\\resources\\templates\\index_banner.ftl");
        //定义输入流
        FileInputStream fileInputStream=new FileInputStream(file);
        //向GridFS存储文件
        ObjectId objectId = gridFsTemplate.store(fileInputStream, "轮播图测试文件01", "");
        //得到文件ID
        String s = objectId.toString();
        System.out.println(s);
    }

    /**
     *读取文件
     * 1)在conﬁg包中定义Mongodb的配置类
     */
    @Test
    public void test05() throws IOException {

        //根据id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource，用于获取流对象
        GridFsResource gridFsResource=new GridFsResource(gridFSFile,gridFSDownloadStream);
        //获取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
        System.out.println(s);
    }

    /**
     * 删除文件
     */
    @Test
    public void test06(){
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(fileId)));
    }
    
    @Test
    public void test07(){
        String pageHtml = pageService.getPageHtml("5a795ac7dd573c04508f3a56");
        System.out.println(pageHtml);
    }
}
