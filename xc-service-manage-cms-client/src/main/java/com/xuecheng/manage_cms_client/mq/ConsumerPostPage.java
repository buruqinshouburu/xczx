package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * 监听消息队列，准备加载页面
 */
@Component
public class ConsumerPostPage {
    private static final Logger LOGGER= LoggerFactory.getLogger(ConsumerPostPage.class);
    @Autowired
    PageService pageService;
    @Autowired
    CmsPageRepository cmsPageRepository;
    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String message){
        //将队列中的消息转换成map格式
        Map map = JSON.parseObject(message, Map.class);
        //打印日志信息
        System.out.println("-------------监听到消息----------");
       LOGGER.info("receive cms post page: {}",message.toString());
       String  pageId = (String) map.get("pageId");
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()){
            LOGGER.error("receive cms post page,cmsPage is null: {}",message.toString());
        }
      pageService.publicHtml(pageId);
    }
}
