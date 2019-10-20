package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CourseControllerApi {
    TeachplanNode selectList(String id);
    QueryResponseResult findCourseList(int page,int size);
    ResponseResult add(Teachplan teachplan);
}
