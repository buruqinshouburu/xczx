package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class demo {
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Test
    public void test01(){
        List<CmsPage> all = cmsPageRepository.findAll();
        for (CmsPage cmsPage : all) {
            System.out.println(cmsPage);
        }

    }
    @Test
    public void test02(){
        Pageable pageable= PageRequest.of(0,10);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        for (CmsPage cmsPage : all) {
            System.out.println(cmsPage);
        }

    }
}
