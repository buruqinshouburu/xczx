package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.TeachplanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {
    @Autowired
    private TeachplanService teachplanService;
    @Override
    @GetMapping("/teachplan/list/{courseid}")
    public TeachplanNode selectList(@PathVariable("courseid") String courseid) {
        return teachplanService.selectList(courseid);
    }

    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page, @PathVariable("size") int size) {
        return teachplanService.findCourseList(page,size);
    }

    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult add(@RequestBody Teachplan teachplan) {
        System.out.println(teachplan);
        return teachplanService.add(teachplan);
    }
}
