package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoursePicRepository extends JpaRepository<CoursePic,String> {
    CoursePic findByPic(String filedId);



    Long deleteByCourseid(String courseid);
}
