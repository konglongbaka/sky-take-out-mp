package com.sky.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    //队列 起名：TestDirectQueue
    @Bean
    public Queue SimpleQueue1() {
        return new Queue("SimpleQueue1",true);
    }
    @Bean
    public Queue SimpleQueue2() {
        return new Queue("SimpleQueue2",true);
    }

    //Direct交换机 起名：TestDirectExchange
    @Bean
    DirectExchange TestDirectExchange() {
        return new DirectExchange("SimpleExchange");
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
    @Bean
    Binding bindingDirect1() {
        return BindingBuilder.bind(SimpleQueue1()).to(TestDirectExchange()).with("TestDirectRouting");
    }

    @Bean
    Binding bindingDirect2() {
        return BindingBuilder.bind(SimpleQueue2()).to(TestDirectExchange()).with("TestDirectRouting");
    }


    @Bean
    public Queue objectQueue() {
        return new Queue("ObjectQueue",true);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue("OrderQueue",true);
    }

    @Bean
    public Queue logQueue() {
        return new Queue("LogQueue",true);
    }


}
