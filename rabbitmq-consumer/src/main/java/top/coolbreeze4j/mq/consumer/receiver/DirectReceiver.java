package top.coolbreeze4j.mq.consumer.receiver;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author CoolBreeze
 * @date 2023/5/7 20:33.
 * 直接型交换机队列 消息接收
 *
 * @RabbitListener 注解是指定某方法作为消息消费的方法，例如监听某 Queue 里面的消息。
 * @RabbitListener 标注在方法上，直接监听指定的队列，此时接收的参数需要与发送市类型一致
 *
 * @RabbitListener 可以标注在类上面，需配合 @RabbitHandler 注解一起使用
 * @RabbitListener 标注在类上面表示当有收到消息的时候，就交给 @RabbitHandler 的方法处理，根据接受的参数类型进入具体的方法中。
 *
 */
@Component
//监听队列 directQueue
@RabbitListener(queues = "directQueue")
public class DirectReceiver {

    @RabbitHandler
    public void process(Map message) {
        System.out.println("directQueue 收到消息  : " + message.toString());
    }
}
