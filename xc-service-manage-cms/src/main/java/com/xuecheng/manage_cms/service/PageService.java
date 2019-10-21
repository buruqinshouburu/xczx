package com.xuecheng.manage_cms.service;


import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitMqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 分页查找所有cmsPage
     * @param size
     * @param page
     * @param queryPageRequest
     * @return
     */
    public QueryResponseResult findList(int size, int page , QueryPageRequest queryPageRequest) {
        if(queryPageRequest==null){
            QueryPageRequest queryPageRequest1=new QueryPageRequest();
        }
        if(page<=0){
            page=1;
        }
        page--;
        if(size<=0){
            size=10;
        }
        //将条件封装到类中
        CmsPage cmsPage = new CmsPage();
        if(StringUtils.isNoneEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        if(StringUtils.isNoneEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //条件匹配器         
        //页面名称模糊查询，需要自定义字符串的匹配器实现模糊查询
        ExampleMatcher exampleMatcher=ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //创建条件实例
        Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);
        //分页对象
        Pageable pageable= PageRequest.of(page,size);
        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage> queryResult=new QueryResult<CmsPage>();
        queryResult.setTotal(all.getTotalElements());
        queryResult.setList(all.getContent());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);

    }

    /**
     * 查找站点名称
     * @return
     */
    public QueryResponseResult findSiteName() {
        List<CmsSite> all = cmsSiteRepository.findAll();
        QueryResult<CmsSite> queryResult=new QueryResult<>();
        queryResult.setList(all);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    /**
     * 查找模版名称
     * @return
     */
    public QueryResponseResult findtemplateName() {
        List<CmsTemplate> all = cmsTemplateRepository.findAll();
        QueryResult<CmsTemplate> queryResult=new QueryResult<>();
        queryResult.setList(all);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    /**
     * 添加CmsPage
     * @param cmsPage
     * @return
     */
    public QueryResponseResult add(CmsPage cmsPage) {
        CmsPage repository = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(repository!=null){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        if(repository==null){
            CmsPage cmsPageSave=new CmsPage();
            getCmsPage(cmsPage,cmsPageSave);
            cmsPageRepository.save(cmsPageSave);
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }
        return new QueryResponseResult(CommonCode.FAIL,null);
    }

    private void getCmsPage(CmsPage cmsPage,CmsPage cmsPageSave) {
        cmsPageSave.setSiteId(cmsPage.getSiteId());
        cmsPageSave.setTemplateId(cmsPage.getTemplateId());
        cmsPageSave.setPageName(cmsPage.getPageName());
        cmsPageSave.setPageAliase(cmsPage.getPageAliase());
        cmsPageSave.setPageWebPath(cmsPage.getPageWebPath());
        cmsPageSave.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
        cmsPageSave.setPageType(cmsPage.getPageType());
        cmsPageSave.setPageCreateTime(cmsPage.getPageCreateTime());
        cmsPageSave.setDataUrl(cmsPage.getDataUrl());
    }

    /**
     * 根据id查找CmsPage
     * @param id
     * @return
     */
    public CmsPageResult findCmsPageById(String id) {
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            CmsPage cmsPage = optional.get();
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    /**
     * 编辑Cmspage
     * @param cmsPage
     * @return
     */
    public QueryResponseResult edit(CmsPage cmsPage) {
        CmsPageResult cmsPageResult = findCmsPageById(cmsPage.getPageId());
        CmsPage cmsPagesave = cmsPageResult.getCmsPage();
        if(cmsPagesave!=null){
             getCmsPage(cmsPage,cmsPagesave);
            CmsPage save = cmsPageRepository.save(cmsPagesave);
            if(save!=null){
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }
        }
        return new QueryResponseResult(CommonCode.FAIL,null);
    }

    /**
     * 删除CmsPage
     * @param id
     * @return
     */
    public QueryResponseResult del(String id) {
        cmsPageRepository.deleteById(id);
        return new QueryResponseResult(CommonCode.SUCCESS,null);
    }
    /**
     * 1、填写页面DataUrl 在编辑cms页面信息界面填写DataUrl，将此字段保存到cms_page集合中。
     * 2、静态化程序获取页面的DataUrl
     * 3、静态化程序远程请求DataUrl获取数据模型。
     * 4、静态化程序获取页面的模板信息
     * 5、执行页面静态化
     */
    public String getPageHtml(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(optional.isPresent()){
            //静态化程序远程请求DataUrl获取数据模型。
            Map model = getModelByPageId(optional.get().getDataUrl());
            if(model==null) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
            //静态化程序获取页面的模板信息
            String template = getTemplateByPageId(optional.get().getTemplateId());
            if(template==null) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
            //执行页面静态化
            String generatehtml = generatehtml(model, template);
            if(StringUtils.isEmpty(generatehtml)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
            return generatehtml;
        }
        return null;
    }

    /**
     * 页面静态化
     * @param model
     * @param template
     * @return
     */
    private String generatehtml(Map model, String template)  {
       //生成配置类
        Configuration configuration=new Configuration(Configuration.getVersion());
        //模板加载器
        StringTemplateLoader stringTemplateLoader=new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",template);
        //配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        try {
            Template template1 = configuration.getTemplate("template");
            String s = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取页面模版
     * @param templateId
     * @return
     */
    private String getTemplateByPageId(String templateId) {
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if(optional.isPresent()){
           String fileId= optional.get().getTemplateFileId();
           if(StringUtils.isEmpty(fileId)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            GridFsResource gridFsResource=new GridFsResource(gridFSFile,gridFSDownloadStream);
            try {
                String s = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
                return s;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取数据模型
     * @param dataUrl
     * @return
     */
    private Map getModelByPageId(String dataUrl) {
        if(StringUtils.isEmpty(dataUrl)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }

    /**
     * 保存静态化页面，并发布消息
     * @param pageId
     * @return
     */
    public ResponseResult saveHtmlPage(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(!optional.isPresent()) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        CmsPage cmsPage = optional.get();
        //页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将页面保存到GridFS中
        saveHtml(cmsPage, pageHtml);
        //发布消息
        sendMessage(cmsPage);
        //返回结果
        return new ResponseResult(CommonCode.SUCCESS);
    }

    private void sendMessage(CmsPage cmsPage) {
        Map<String,String> msg=new HashMap<>();
        msg.put("pageId",cmsPage.getPageId());
        String s = JSON.toJSONString(msg);
        rabbitTemplate.convertAndSend(RabbitMqConfig.EX_ROUTING_CMS_POSTPAGE,cmsPage.getSiteId(),s);
    }

    private void saveHtml(CmsPage cmsPage, String pageHtml) {
        String htmlFileId = cmsPage.getHtmlFileId();
        if(StringUtils.isNotEmpty(htmlFileId)){
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        try {
            InputStream inputStream = IOUtils.toInputStream(pageHtml, "utf-8");
            ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
            String filedId = objectId.toString();
            cmsPage.setHtmlFileId(filedId);
            cmsPageRepository.save(cmsPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
