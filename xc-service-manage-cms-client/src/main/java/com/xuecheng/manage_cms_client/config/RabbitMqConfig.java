package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq消费方
 */
@Configuration
public class RabbitMqConfig {
    //监听队列bean的名称
    public static final String QUEUE_CMS_POSTPAGE="queue_cms_postPage";
    //交换机的名称
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";
    @Value("${xuecheng.mq.queue}")
    public String  queue_cms_postpage_name;
    @Value("${xuecheng.mq.routingkey}")
    public String routingKey;
    //配置交换机
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_TOPICS_INFORM(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }
    //声明队列
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue QUEUE_CMS_POSTPAGE(){
        Queue queue=new Queue(queue_cms_postpage_name);
        return queue;
    }
    //绑定交换机
    @Bean
    public Binding binding(@Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange,
                            @Qualifier(QUEUE_CMS_POSTPAGE) Queue queue) {
       return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }

}
