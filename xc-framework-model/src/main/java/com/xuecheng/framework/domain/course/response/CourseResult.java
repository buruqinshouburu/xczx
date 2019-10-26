package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CourseResult extends ResponseResult {
    public CourseBase courseBase;
    public CourseResult(ResultCode resultCode, CourseBase courseBase) {
        super(resultCode);
        this.courseBase = courseBase;
    }
}
