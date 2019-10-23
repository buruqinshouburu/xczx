package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import com.xuecheng.manage_course.service.TeachplanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {
    @Autowired
    private TeachplanService teachplanService;
    @Autowired
    private CourseService courseService;
    @Override
    @GetMapping("/teachplan/list/{courseid}")
    public TeachplanNode selectList(@PathVariable("courseid") String courseid) {
        return teachplanService.selectList(courseid);
    }

    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page, @PathVariable("size") int size,CourseListRequest courseListRequest) {
        return courseService.findCourseList(page,size,courseListRequest);
    }

    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult add(@RequestBody Teachplan teachplan) {
        System.out.println(teachplan);
        return teachplanService.add(teachplan);
    }

    @Override
    @PostMapping("/coursebase/add")
    public ResponseResult addCourse(@RequestBody CourseBase courseBase) {
        return courseService.addCourse(courseBase);
    }

    @Override
    @GetMapping("/courseview/{courseId}")
    public CourseBase findCourseView(@PathVariable("courseId") String courseId) {
        return courseService.findCourseView(courseId);
    }

    @Override
    @PostMapping("/update/{id}")
    public ResponseResult updateCoursebase(@PathVariable("id") String id, @RequestBody CourseBase courseBase) {
        return courseService.update(id,courseBase);
    }

    @Override
    @GetMapping("/market/{courseid}")
    public CourseMarket getCourseMarketById(@PathVariable("courseid") String id) {
        return courseService.getCourseMarketById(id);
    }

    @Override
    @PostMapping("/update/market/{courseid}")
    public ResponseResult updateCourseMarket(@PathVariable("courseid") String id, @RequestBody CourseMarket courseMarket) {
        return courseService.updateCourseMarket(id,courseMarket);
    }

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseid, @RequestParam("pic") String pic) {
        return courseService.addCoursePic(courseid,pic);
    }

    @Override
    @GetMapping("/coursepic/list/{courseid}")
    public CoursePic findCoursePicList(@PathVariable("courseid") String courseid) {
        return courseService.findCoursePicList(courseid);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseid) {
        return courseService.deleteCoursePic(courseid);
    }
}
