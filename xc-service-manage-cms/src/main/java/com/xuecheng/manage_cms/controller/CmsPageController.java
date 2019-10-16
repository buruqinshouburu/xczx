package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms/pages")
public class CmsPageController implements CmsPageControllerApi {
    @Autowired
   private PageService pageService;
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("size") int size,@PathVariable("page") int page, QueryPageRequest queryPageRequest) {
      return pageService.findList(size,page,queryPageRequest);
    }

    @Override
    @GetMapping("/site")
    public QueryResponseResult findSiteName() {
       return pageService.findSiteName();
    }

    @Override
    @GetMapping("/template")
    public QueryResponseResult findtemplateName() {
        return pageService.findtemplateName();
    }

    @Override
    @PostMapping("/add")
    public QueryResponseResult add(@RequestBody CmsPage cmsPage) {
        System.out.println(cmsPage);
        return pageService.add(cmsPage);
    }

    @Override
    @GetMapping("/list/{id}")
    public CmsPageResult findCmsPageById(@PathVariable("id") String id) {
        return pageService.findCmsPageById(id);
    }

    @Override
    @PostMapping("/edit")
    public QueryResponseResult edit(@RequestBody CmsPage cmsPage) {
        return pageService.edit(cmsPage);
    }

    @Override
    @GetMapping("/del/{id}")
    public QueryResponseResult del(@PathVariable("id") String id) {
        return pageService.del(id);
    }
}
