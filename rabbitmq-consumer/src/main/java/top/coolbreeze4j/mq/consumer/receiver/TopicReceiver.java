package top.coolbreeze4j.mq.consumer.receiver;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author CoolBreeze
 * @date 2023/5/13 21:55.
 * 主题交换机队列 消息接收
 */
@Component
public class TopicReceiver {

    //监听队列 mailQueue
    @RabbitListener(queues = "mailQueue")
    public void mailListener(Map message) {
        System.out.println("mailQueue 收到邮件  : " + message.toString());
    }


    //监听队列 smsQueue
    @RabbitListener(queues = "smsQueue")
    public void smsListener(Map message) {
        System.out.println("smsQueue 收到短信  : " + message.toString());
    }
}
