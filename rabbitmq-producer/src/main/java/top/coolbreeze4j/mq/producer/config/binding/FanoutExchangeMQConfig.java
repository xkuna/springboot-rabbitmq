package top.coolbreeze4j.mq.producer.config.binding;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CoolBreeze
 * @date 2023/5/7 20:48.
 * 扇形交换机 MQ配置
 * 将三个队列都绑定在交换机 fanoutExchange 上
 * 因为是扇型交换机, 路由键无需配置,配置也不起作用
 */
@Configuration
public class FanoutExchangeMQConfig {
    /**
     * 队列名称
     */
    public static final String FANOUT_QUEUE_A = "fanoutQueueA";
    public static final String FANOUT_QUEUE_B = "fanoutQueueB";
    public static final String FANOUT_QUEUE_C = "fanoutQueueC";

    /**
     * 交换机名称
     */
    public static final String FANOUT_EXCHANGE = "myFanoutExchange";


    @Bean
    public Queue fanoutQueueA(){
        return new Queue(FANOUT_QUEUE_A);
    }

    @Bean
    public Queue fanoutQueueB(){
        return new Queue(FANOUT_QUEUE_B);
    }

    @Bean
    public Queue fanoutQueueC(){
        return new Queue(FANOUT_QUEUE_C);
    }

    @Bean
    public FanoutExchange myFanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding bindingA(Queue fanoutQueueA, FanoutExchange myFanoutExchange){
        return BindingBuilder.bind(fanoutQueueA).to(myFanoutExchange);
    }

    @Bean
    public Binding bindingB(Queue fanoutQueueB, FanoutExchange myFanoutExchange){
        return BindingBuilder.bind(fanoutQueueB).to(myFanoutExchange);
    }

    @Bean
    public Binding bindingC(Queue fanoutQueueC, FanoutExchange myFanoutExchange){
        return BindingBuilder.bind(fanoutQueueC).to(myFanoutExchange);
    }

}
