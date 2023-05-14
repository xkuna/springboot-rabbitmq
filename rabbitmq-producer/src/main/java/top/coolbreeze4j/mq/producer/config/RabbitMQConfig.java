package top.coolbreeze4j.mq.producer.config;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author CoolBreeze
 * @date 2023/5/13 22:57.
 * RabbitMQ 配置类
 * 在这里主要是统一配置 消息发送确认
 * 具体根据实际情况做出调整
 *
 * 如果消息不太重要，丢失也没有影响，那么自动ACK会比较方便。好处就是可以提高吞吐量，缺点就是会丢失消息
 * (注释@Configuration 使本配置类不生效，并且配置文件对应的两个Callback配置也要注释掉)
 *
 * 如果消息非常重要，不容丢失，则建议手动ACK(当前设置)，正常情况都是更建议使用手动ACK。虽然可以解决消息不会丢失的问题，但是可能会造成消费者过载
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        //ConfirmCallback 每次发送消息都会执行
        //ack=true代表消息进入broker 不代表消息进入队列
        //ack=false代表消息没有进入broker(找不到交换机)
        //broker = 交换机 + 队列，先找交换机 再根据情况找队列

        //消息补偿方式:
        // 发送消息时附带有一张历史表，以msgId（全局唯一id,correlationData.getId()）为主键的表，它记录了消息是否投递成功，消息重试次数以及消息过期时间等信息
        // 理论上ConfirmCallback执行回调ACK如果成功，那么历史表相应的状态可以更改为成功，失败的话只是打印日志不做其他处理；(也可以设置失败，根据情况来)
        // 之后会有一个单独的定时任务@schedule 去历史表去查询所有发送状态不为成功的消息（也可以用batch批处理解决）
        // 查询完毕时再看消息的过期时间或者是否重试次数不超过上限，最后我们发送一次此消息
        // 总结来说，ConfirmCallBack用历史表等三方工具去执行重发

        //唯一id需要在消息发送时设置correlationData的id
        //例如
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
                if(null != correlationData){
                    System.out.println("ConfirmCallback:     "+"msgId："+correlationData.getId());
                }
                System.out.println("ConfirmCallback:     "+"确认情况："+ack);
                System.out.println("ConfirmCallback:     "+"原因："+cause);
            }
        });

        //保证returnCallback完全生效(配合配置文件 缺一不可)
        rabbitTemplate.setMandatory(true);
        //ReturnCallback 消息进入broker 通过了交换机 但未进入队列时会执行
        //消息补偿方式:
        //   与ConfirmCallback 不同的是只有发送失败(通过了交换机 但未进入队列)才会执行,所以走ConfirmCallback的失败方案即可
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("ReturnCallback:     "+"消息："+message);

                String body = new String(message.getBody());
                Map data = JSONObject.parseObject(body, Map.class);
                System.out.println("data--" + data);
                System.out.println("msgId--" + data.get("msgId"));

                System.out.println("ReturnCallback:     "+"回应码："+replyCode);
                System.out.println("ReturnCallback:     "+"回应信息："+replyText);
                System.out.println("ReturnCallback:     "+"交换机："+exchange);
                System.out.println("ReturnCallback:     "+"路由键："+routingKey);
            }
        });

        return rabbitTemplate;
    }
}
