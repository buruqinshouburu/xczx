package com.xuecheng.framework.domain.course;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CourseView {
    public CourseBase courseBase;
    public CourseMarket courseMarket;
    public CoursePic coursePic;
    public TeachplanNode teachplanNode;
}
