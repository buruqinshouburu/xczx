package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PageService {
    @Autowired
   private CmsPageRepository cmsPageRepository;

    public QueryResponseResult findList(int size, int page , QueryPageRequest queryPageRequest) {
        if(queryPageRequest==null){
            QueryPageRequest queryPageRequest1=new QueryPageRequest();
        }
        if(page<=0){
            page=1;
        }
        page--;
        if(size<=0){
            size=10;
        }
        Pageable pageable= PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        QueryResult<CmsPage> queryResult=new QueryResult<CmsPage>();
        queryResult.setTotal(all.getTotalElements());
        queryResult.setList(all.getContent());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);

    }
}
