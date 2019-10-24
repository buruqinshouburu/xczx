package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CourseMapper;
import com.xuecheng.manage_course.dao.CourseMarketRepository;
import com.xuecheng.manage_course.dao.CoursePicRepository;
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
    @Autowired
    private CourseMarketRepository courseMarketRepository;
    @Autowired
    private CoursePicRepository coursePicRepository;

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

    public CourseMarket getCourseMarketById(String id) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(id);
        if(optional.isPresent()){
          return optional.get();
        }
        return null;
    }
    @Transactional
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        //通过id查找course_market表
        Optional<CourseMarket> optional = courseMarketRepository.findById(id);
        if(optional.isPresent()){
            //原本存在信息，进行修改操作
            CourseMarket courseMarketSave = optional.get();
            courseMarketSave.setPrice_old(courseMarketSave.getPrice());
            courseMarketSave.setPrice(courseMarket.getPrice());
            courseMarketSave.setStartTime(courseMarket.getStartTime());
            courseMarketSave.setEndTime(courseMarketSave.getEndTime());
            courseMarketSave.setCharge(courseMarket.getCharge());
            courseMarketSave.setValid(courseMarket.getValid());
            courseMarketSave.setQq(courseMarket.getQq());
            courseMarketRepository.save(courseMarketSave);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        //不存在信息，直接添加
        courseMarketRepository.save(courseMarket);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    @Transactional
    public ResponseResult addCoursePic(String courseid, String pic) {
        Optional<CoursePic> optional = coursePicRepository.findById(courseid);
        if(!optional.isPresent()){
            CoursePic coursePic = new CoursePic();
            coursePic.setPic(pic);
            coursePic.setCourseid(courseid);
            coursePicRepository.save(coursePic);
        }else {
            CoursePic coursePic = optional.get();
            //删除dfs图片
            //.............
            coursePic.setPic(pic);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CoursePic findCoursePicList(String courseid) {
        return coursePicRepository.findById(courseid).get();
    }
    @Transactional
    public ResponseResult deleteCoursePic(String courseid) {
        Long result = coursePicRepository.deleteByCourseid(courseid);
        if(result>0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
}
