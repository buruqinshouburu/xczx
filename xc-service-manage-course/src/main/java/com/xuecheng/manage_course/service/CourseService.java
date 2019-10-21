package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseBaseRepository courseBaseRepository;

    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {
        if(courseListRequest==null){
            CourseListRequest courseListRequest1=new CourseListRequest();
        }
        if(page<=0){
            page=1;
        }
        if(size<=0){
            size=5;
        }
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseList = courseMapper.findCourseList();
        long total = courseList.getTotal();
        QueryResult<CourseInfo> queryResult=new QueryResult<>();
        queryResult.setTotal(total);
        queryResult.setList(courseList);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
    @Transactional
    public ResponseResult addCourse(CourseBase courseBase) {
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CourseBase findCourseView(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        return optional.get();
    }
    @Transactional
    public ResponseResult update(String id, CourseBase courseBase) {
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if(!optional.isPresent()) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        CourseBase save = optional.get();
        save.setName(courseBase.getName());
        save.setUsers(courseBase.getUsers());
        save.setMt(courseBase.getMt());
        save.setSt(courseBase.getSt());
        save.setGrade(courseBase.getGrade());
        save.setStudymodel(courseBase.getStudymodel());
        save.setDescription(courseBase.getDescription());
        courseBaseRepository.save(save);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
