package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
public interface CourseControllerApi {
    @ApiOperation("教学计划查询列表")
    TeachplanNode selectList(String id);
    @ApiOperation("课程列表")
    QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);
    @ApiOperation("教学计划添加")
    ResponseResult add(Teachplan teachplan);
}
