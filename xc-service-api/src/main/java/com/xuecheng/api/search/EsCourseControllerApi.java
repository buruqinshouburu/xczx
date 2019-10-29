package com.xuecheng.api.search;

import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="课程搜索",description="课程搜索",tags={"课程搜索"})
public interface EsCourseControllerApi {
    @ApiOperation(value = "课程搜索")
    public QueryResponseResult List(int page, int size, CourseSearchParam courseSearchParam);
}
