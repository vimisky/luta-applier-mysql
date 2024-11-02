package io.github.vimisky.luta.applier.mysql.processor;

import org.springframework.amqp.core.Message;

public interface LutaMessageDrivenTask {
    public boolean isRunning();
    public boolean onMessage(Message message);
}
