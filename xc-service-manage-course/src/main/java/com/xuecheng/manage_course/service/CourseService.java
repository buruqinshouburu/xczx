package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sun.org.apache.regexp.internal.RE;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CoursePreviewResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.CourseView;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CourseResult;
import com.xuecheng.framework.domain.portalview.PreViewCourse;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.Client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private CmsPageClient cmsPageClient;
    @Value("${course-publish.siteId}")
    private String siteId;
    @Value("${course-publish.templateId}")
    private String templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;
    @Value("${course-publish.pageWebPath}")
    private String pageWebPath;
    @Value("${course-publish.pagePhysicalPath}")
    private String pagePhysicalPath;
    @Value("${course-publish.dataUrlPre}")
    private String dataUrlPre;
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

    /**
     * 获得课程详情数据
     * @param courseid
     * @return
     */
    @Transactional
    public PreViewCourse getCourseView(String courseid) {
       PreViewCourse preViewCourse=new PreViewCourse();
       //查询 courseBase;
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseid);
        if(!courseBaseOptional.isPresent()) ExceptionCast.cast(CourseCode.COURSE_CORSEBASE_ISNULL);
        preViewCourse.setCourseBase(courseBaseOptional.get());
        //查询 courseMarket;
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(courseid);
        if(!courseMarketOptional.isPresent()) ExceptionCast.cast(CourseCode.COURSE_CORSEMARKET_ISNULL);
        preViewCourse.setCourseMarket(courseMarketOptional.get());
        //查询 coursePic;
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(courseid);
        if(!coursePicOptional.isPresent()) ExceptionCast.cast(CourseCode.COURSE_CORSEPIC_ISNULL);
        preViewCourse.setCoursePic(coursePicOptional.get());
        //查询 teachplanNode;
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseid);
        preViewCourse.setTeachplan(teachplanNode);
        return preViewCourse;
    }

    /**
     * 添加课程详情页面
     * @param courseid
     * @return
     */
    @Transactional
    public CoursePreviewResult addCmsPage(String courseid) {
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseid);
        if(!courseBaseOptional.isPresent()) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        CourseBase courseBase = courseBaseOptional.get();
        //添加CmsPage信息
        CmsPage cmsPage = createCmsPage(courseid,courseBase.getName());
        CmsPageResult cmsPageResult = cmsPageClient.addCmsPage(cmsPage);
        if(!cmsPageResult.isSuccess()){
           return new CoursePreviewResult(CommonCode.FAIL,null);
        }
        CmsPage cmsPageSave = cmsPageResult.getCmsPage();
        String pageId = cmsPageSave.getPageId();
        if(StringUtils.isEmpty(pageId)) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        String Url=previewUrl+pageId;

        return new CoursePreviewResult(CommonCode.SUCCESS,Url);
    }

    /**
     * 发布页面
     * @param courseid
     * @return
     */
    @Transactional
    public CourseResult publish(String courseid) {
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseid);
        if(!courseBaseOptional.isPresent()) ExceptionCast.cast(CommonCode.INVALID_PARAM);
        CourseBase courseBase = courseBaseOptional.get();
        //添加CmsPage信息
        CmsPage cmsPage = createCmsPage(courseid,courseBase.getName());
        //一键发布
        CmsPageResult cmsPageResult = cmsPageClient.postPageQuick(cmsPage);
        if(!cmsPageResult.isSuccess()){
            return new CourseResult(CommonCode.FAIL,null);
        }
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        return new CourseResult(CommonCode.SUCCESS,save);
    }

    private CmsPage createCmsPage(String courseid,String name) {

        CmsPage cmsPage=new CmsPage();
        cmsPage.setDataUrl(dataUrlPre+courseid);
        cmsPage.setTemplateId(templateId);
        cmsPage.setSiteId(siteId);
        cmsPage.setPagePhysicalPath(pagePhysicalPath);
        cmsPage.setPageWebPath(pageWebPath);
        cmsPage.setPageName(courseid+".html");
        cmsPage.setPageAliase(name);
        return cmsPage;
    }
}
