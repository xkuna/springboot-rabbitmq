package top.coolbreeze4j.mq.producer.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.coolbreeze4j.mq.producer.config.binding.FanoutExchangeMQConfig;
import top.coolbreeze4j.mq.producer.config.binding.TopicExchangeMQConfig;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author CoolBreeze
 * @date 2023/5/7 20:22.
 */
@RestController
public class MessageController {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @GetMapping("/direct")
    public String produceDirectMessage(){
        Map<String, Object> data = new HashMap<>();

        data.put("msgId", UUID.randomUUID().toString());
        data.put("msg","我是消息" + System.currentTimeMillis());
        data.put("crateTime", LocalDateTime.now().toString());

        rabbitTemplate.convertAndSend("directExchange", "directRouting", data);
        return "发送消息成功!";
    }



    @GetMapping("/topic")
    public String produceTopicMessage(@RequestParam String type){
        Map<String, Object> data = new HashMap<>();

        if("mail".equals(type)){
            data.put("msgId", UUID.randomUUID().toString());
            data.put("msg","发送邮件" + System.currentTimeMillis());
            data.put("crateTime", LocalDateTime.now().toString());
            rabbitTemplate.convertAndSend(TopicExchangeMQConfig.TOPIC_EXCHANGE, "system.mail", data);
        }else if("sms".equals(type)){
            data.put("msgId", UUID.randomUUID().toString());
            data.put("msg","发送短信" + System.currentTimeMillis());
            data.put("crateTime", LocalDateTime.now().toString());
            rabbitTemplate.convertAndSend(TopicExchangeMQConfig.TOPIC_EXCHANGE, "system.sms", data);
        }

        return "发送消息成功!";
    }


    @GetMapping("/fanout")
    public String produceFanoutMessage(){
        Map<String, Object> data = new HashMap<>();
        data.put("msgId", UUID.randomUUID().toString());
        data.put("msg","扇形交换机发送消息" + System.currentTimeMillis());
        data.put("crateTime", LocalDateTime.now().toString());
        rabbitTemplate.convertAndSend(FanoutExchangeMQConfig.FANOUT_EXCHANGE, null, data);

        return "发送消息成功!";
    }


    //---------------------------接下来是消息发送失败，回执测试---------------------------
    //---------------------------具体配置查看 RabbitMQConfig---------------------------
    //---------------------------建议将对象转为JsonString---------------------------
    @GetMapping("/confirm-callback")
    public String confirmCallback(){
        Map<String, Object> data = new HashMap<>();
        String msgId = UUID.randomUUID().toString();
        data.put("msgId", msgId);
        data.put("msg","发送消息" + System.currentTimeMillis());
        data.put("crateTime", LocalDateTime.now().toString());

        String msg = JSONObject.toJSONString(data);
        //设置错误的交换机，触发ConfirmCallback
        rabbitTemplate.convertAndSend("callBackDirectExchange111", "callBackDirectRouting", msg, new CorrelationData(msgId));

        return "发送消息成功!";
    }

    @GetMapping("/return-callback")
    public String returnCallback(){
        Map<String, Object> data = new HashMap<>();
        String msgId = UUID.randomUUID().toString();
        data.put("msgId", msgId);
        data.put("msg","发送消息" + System.currentTimeMillis());
        data.put("crateTime", LocalDateTime.now().toString());

        String msg = JSONObject.toJSONString(data);
        //设置错误的队列，触发ReturnCallback
        rabbitTemplate.convertAndSend("callBackDirectExchange", "callBackDirectRouting111", msg, new CorrelationData(msgId));

        return "发送消息成功!";
    }


    //---------------------------接下来正确发送消息 便于消费者测试手动确认---------------------------

    @GetMapping("/consumer-ack")
    public String produceConsumeAckDirectMessage(){
        Map<String, Object> data = new HashMap<>();

        data.put("msgId", UUID.randomUUID().toString());
        data.put("msg","我是消息" + System.currentTimeMillis());
        data.put("crateTime", LocalDateTime.now().toString());

        rabbitTemplate.convertAndSend("consumerAckDirectExchange", "consumerAckDirectRouting", data);
        return "发送消息成功!";
    }
}
