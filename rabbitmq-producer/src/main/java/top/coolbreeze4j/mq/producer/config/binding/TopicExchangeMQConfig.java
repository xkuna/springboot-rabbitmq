package top.coolbreeze4j.mq.producer.config.binding;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CoolBreeze
 * @date 2023/5/7 20:48.
 * 主题交换机 MQ配置
 */
@Configuration
public class TopicExchangeMQConfig {
    /**
     * 队列名称
     */
    public static final String TOPIC_QUEUE_MAIL = "mailQueue";
    public static final String TOPIC_QUEUE_SMS = "smsQueue";

    /**
     * 交换机名称
     */
    public static final String TOPIC_EXCHANGE = "myTopicExchange";

    /**
     * 路由键
     * *代表一个单词，#可以代替零个或多个单词
     * 每个单词之间要用点隔开
     * 单词最多 255 个字节
     * 通过相关的匹配规则后就会将满足条件的消息放到对应的队列中
     */
    public static final String ROUTING_KEY_MAIL = "#.mail";
//    public static final String ROUTING_KEY = "*.mail";


    public static final String ROUTING_KEY_SMS = "#.sms";
//    public static final String ROUTING_KEY_SMS = "*.sms";

    @Bean
    public Queue mailQueue(){
        return new Queue(TOPIC_QUEUE_MAIL);
    }

    @Bean
    public Queue smsQueue(){
        return new Queue(TOPIC_QUEUE_SMS);
    }

    @Bean
    public TopicExchange myTopicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding mailBinding(Queue mailQueue, TopicExchange myTopicExchange){
        return BindingBuilder.bind(mailQueue).to(myTopicExchange).with(ROUTING_KEY_MAIL);
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, TopicExchange myTopicExchange){
        return BindingBuilder.bind(smsQueue).to(myTopicExchange).with(ROUTING_KEY_SMS);
    }

}
