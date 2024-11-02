package io.github.vimisky.luta.applier.mysql.processor;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

public class LutaMessageListener implements ChannelAwareMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(LutaMessageListener.class);

    private LutaMessageDrivenTask task;

    public LutaMessageListener() {
    }

    public LutaMessageListener(LutaMessageDrivenTask task) {
        this.task = task;
    }

    public LutaMessageDrivenTask getTask() {
        return task;
    }

    public void setTask(LutaMessageDrivenTask task) {
        this.task = task;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        if (this.task != null){
            while (!this.task.onMessage(message)){
                if (this.task.isRunning()){
                    Thread.sleep(5000);
                }else {
                    return;
                }
            }
            //第二个参数是，是否把小于tag的一起确认了，false，只确认自己。
            channel.basicAck(deliveryTag, false);
        }

    }
}
