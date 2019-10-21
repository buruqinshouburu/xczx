package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeachplanRepository extends JpaRepository<Teachplan,String> {


    List<Teachplan> findByCourseidAndParentid(String courseid, String s);

    Optional<Teachplan> findByCourseid(String courseid);
}
