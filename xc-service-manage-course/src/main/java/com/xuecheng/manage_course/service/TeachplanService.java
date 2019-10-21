package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CourseMapper;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeachplanService {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    @Autowired
    private TeachplanRepository teachplanRepository;


    public TeachplanNode selectList(String id){
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        return teachplanNode;
    }

    @Transactional
    public ResponseResult add(Teachplan teachplan) {
        if(teachplan==null ||StringUtils.isEmpty(teachplan.getCourseid())||StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String parentid = teachplan.getParentid();
        if(StringUtils.isEmpty(parentid)){
            // 父节点为空，则该节点为二级结点
           //获取courseid
            String courseid = teachplan.getCourseid();
            String id = getTeachplan(teachplan, courseid);
            teachplan.setParentid(id);
            teachplan.setGrade("2");
            teachplanRepository.save(teachplan);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        //父节点不为空，则该结点为三级结点
        teachplan.setGrade("3");
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    private String getTeachplan(Teachplan teachplan, String courseid) {
        List<Teachplan> byCourseidAndGrade = teachplanRepository.findByCourseidAndParentid(courseid, "0");
        if(byCourseidAndGrade==null||byCourseidAndGrade.size()==0){
          //该节点为根节点,自动创建根节点
            Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseid);
            if(!baseOptional.isPresent()){
                ExceptionCast.cast(CommonCode.INVALID_PARAM);
            }
            Teachplan teachplanroot=new Teachplan();
           teachplanroot.setCourseid(courseid);
           teachplanroot.setGrade("1");
           teachplanroot.setParentid("0");
           teachplanroot.setPname(baseOptional.get().getName());
           teachplanroot.setOrderby(teachplan.getOrderby());
           teachplanroot.setStatus("0");
           teachplanroot.setPtype(teachplan.getPtype());
           teachplanRepository.save(teachplanroot);
           return teachplanroot.getId();
       }
        return byCourseidAndGrade.get(0).getId();
    }
}
