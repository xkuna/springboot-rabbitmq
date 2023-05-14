package top.coolbreeze4j.mq.producer.config;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

/**
 * @author CoolBreeze
 * @date 2023/5/14 02:32.
 * 保留默认rabbitTemplate的配置下 修改rabbitTemplate的方式
 * 可以让配置文件中的spring.rabbitmq.template相关的配置生效
 * RabbitMQConfig的方式配置会导致spring.rabbitmq.template的配置不生效
 */
//@Configuration
public class RabbitInitializingBean  implements InitializingBean {

    private final RabbitTemplate rabbitTemplate;

    public RabbitInitializingBean(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
            System.out.println("ConfirmCallback:     "+"msgId："+correlationData.getId());
            System.out.println("ConfirmCallback:     "+"确认情况："+ack);
            System.out.println("ConfirmCallback:     "+"原因："+cause);
        });

        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("ReturnCallback:     "+"消息："+message);

            String body = new String(message.getBody());
            Map data = JSONObject.parseObject(body, Map.class);
            System.out.println("data--" + data);
            System.out.println("msgId--" + data.get("msgId"));

            System.out.println("ReturnCallback:     "+"回应码："+replyCode);
            System.out.println("ReturnCallback:     "+"回应信息："+replyText);
            System.out.println("ReturnCallback:     "+"交换机："+exchange);
            System.out.println("ReturnCallback:     "+"路由键："+routingKey);
        });
    }
}
