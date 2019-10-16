package com.xuecheng.manage_cms.service;


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
        import com.xuecheng.manage_cms.dao.CmsPageRepository;
        import com.xuecheng.manage_cms.dao.CmsSiteRepository;
        import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
        import org.apache.commons.lang3.StringUtils;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.data.domain.*;
        import org.springframework.stereotype.Service;

        import java.util.List;
        import java.util.Optional;

@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

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
}
