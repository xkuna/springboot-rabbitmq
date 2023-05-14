package top.coolbreeze4j.mq.consumer.receiver.ack;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.IOException;
import java.util.Map;

/**
 * @author CoolBreeze
 * @date 2023/5/7 20:33.
 * 直接型交换机队列 消息接收
 * 测试手动确认
 * 手动确认 要把配置文件中的spring.rabbitmq.listener.simple.acknowledge-mode 设置为manual
 *
 * 启用手动确认后，top.coolbreeze4j.mq.consumer.receiver 下的队列监听全部需要手动 确认，
 * 所以 只有测试本类时再配置 手动监听即可
 */
//@Component
//监听队列 directQueue
@RabbitListener(queues = "consumerAckDirectQueue")
public class AckDirectReceiver {

    /**
     * 具体根据实际情况来处理，比如:把处理失败的消息先 记录下来 从队列中删除，然后定时去重新处理这些消息，重试超过3次则人工干预
     *
     * 下面的处理流程是 处理失败重新放入队列，再次处理时还出现异常就丢掉消息，这样就会导致消息完全丢失
     * 下面的代码仅仅是演示下 方法的调用
     */
    @RabbitHandler
    public void process(Map map, Channel channel, Message message) throws IOException {
        //消息在队列中消息投递序号(不是自己设置的消息唯一id)
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.println("consumerAckDirectQueue 收到消息  : " + map.toString());

            //业务处理...
            int a = 1/0;
            //消息确认
            //方法: basicAck：表示成功确认，使用此回执方法后，消息会被rabbitmq broker 删除。
            //参数1:
            //     deliveryTag：表示消息投递序号，每次消费消息或者消息重新投递后，deliveryTag都会增加。
            //手动消息确认模式下，我们可以对指定deliveryTag的消息进行ack、nack、reject等操作。
            //参数2:
            //     multiple：是否批量确认，值为 true 则会一次性 ack所有小于当前消息 deliveryTag 的消息。
            //举个栗子: 假设我先发送三条消息deliveryTag分别是5、6、7，可它们都没有被确认，
            //当我发第四条消息此时deliveryTag为8，multiple设置为 true，会将5、6、7、8的消息全部进行确认。
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            //该消息是否是重试消息
            Boolean redelivered = message.getMessageProperties().getRedelivered();
            if(redelivered){
                System.out.println("消息重试后异常,拒绝再次重试入列！");

                /**
                 * 方法 basicNack() 表示失败确认，一般在消费消息业务异常时用到此方法，可以将消息重新投递入队列
                 * 参数1 deliveryTag：表示消息投递序号
                 * 参数2 multiple：是否批量确认
                 * 参数3 requeue：值为 true 消息将重新入队列。false 则会从队列中删除
                 */
                channel.basicNack(deliveryTag, false, false);
            }else{
                System.out.println("消息处理异常,再次重试入列！");

                /**
                 * 方法 basicReject 拒绝消息，与basicNack区别在于不能进行批量操作，其他用法很相似。
                 * 参数1 deliveryTag：表示消息投递序号。
                 * 参数2 requeue：值为 true 消息将重新入队列。false 则会从队列中删除
                 */
                channel.basicReject(deliveryTag, true);
            }

        }
    }
}
