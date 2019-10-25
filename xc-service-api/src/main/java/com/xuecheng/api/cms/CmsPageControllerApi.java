package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "CMS页面管理接口",description = "页面管理的增删改查")
public interface CmsPageControllerApi {
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true,dataType = "int",paramType = "path"),
            @ApiImplicitParam(name = "size",value = "每页显示条数",required = true,dataType = "int",paramType = "path"),
            @ApiImplicitParam(name = "queryPageRequest",value = "查询条件",required = false,dataType = "String",paramType = "path")
    })
    public QueryResponseResult findList(int size,int page,QueryPageRequest queryPageRequest);
    public QueryResponseResult findSiteName();
    public QueryResponseResult findtemplateName();
    public CmsPageResult add(CmsPage cmsPage);
    public CmsPageResult findCmsPageById(String id);
    public CmsPageResult edit(CmsPage cmsPage);
    public QueryResponseResult del(String id);
    @ApiOperation("发布页面")
    public ResponseResult post(String pageId);
    @ApiOperation("意见发布课程页面")
    public CmsPageResult postPageQuick(CmsPage cmsPage);
    public CmsPageResult addCmsPage( CmsPage cmsPage);
}
