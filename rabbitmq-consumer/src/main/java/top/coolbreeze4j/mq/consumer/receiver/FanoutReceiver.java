package top.coolbreeze4j.mq.consumer.receiver;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author CoolBreeze
 * @date 2023/5/13 21:55.
 * 扇型交换机队列 消息接收
 */
@Component
public class FanoutReceiver {

    //监听队列 fanoutQueueA
    @RabbitListener(queues = "fanoutQueueA")
    public void fanoutQueueAListener(Map message) {
        System.out.println("fanoutQueueA 收到消息  : " + message.toString());
    }


    //监听队列 fanoutQueueB
    @RabbitListener(queues = "fanoutQueueB")
    public void fanoutQueueBListener(Map message) {
        System.out.println("fanoutQueueB 收到消息  : " + message.toString());
    }

    //监听队列 fanoutQueueC
    @RabbitListener(queues = "fanoutQueueC")
    public void fanoutQueueCListener(Map message) {
        System.out.println("fanoutQueueC 收到消息  : " + message.toString());
    }
}
