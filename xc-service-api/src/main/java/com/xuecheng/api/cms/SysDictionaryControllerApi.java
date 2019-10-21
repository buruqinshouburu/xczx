package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "字典查询接口")
public interface SysDictionaryControllerApi {
    @ApiOperation("查询字典信息")
    public SysDictionary getSysDictionary(String dType);
}
