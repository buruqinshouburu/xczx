package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;

import java.util.List;

@Api("查询目录结构")
public interface CategoryControllerApi {
    public CategoryNode category_findlist();
}
