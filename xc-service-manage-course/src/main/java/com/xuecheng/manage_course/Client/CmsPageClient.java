package com.xuecheng.manage_course.Client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "XC-SERVICE-MANAGE-CMS")
public interface CmsPageClient {
    @GetMapping("/cms/pages/list/{id}")
    public CmsPageResult findCmsPageById(@PathVariable("id") String id);
    @PostMapping("/cms/pages/addCmsPage")
    public CmsPageResult addCmsPage(CmsPage cmsPage);
    @PostMapping("/cms/pages/postPageQuick")
    public CmsPageResult postPageQuick(@RequestBody CmsPage cmsPage);

}
