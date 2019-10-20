package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq消费方
 */
@Configuration
public class RabbitMqConfig {
    //交换机的名称
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";
    //配置交换机
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_TOPICS_INFORM(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

}
