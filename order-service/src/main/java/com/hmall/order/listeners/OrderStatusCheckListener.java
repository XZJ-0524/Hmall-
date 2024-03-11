package com.hmall.order.listeners;

import com.hmall.common.constant.MqConstants;
import com.hmall.common.domain.MultiDelayMessage;
import com.hmall.common.mq.DelayMessageProcessor;
import com.hmall.order.domain.po.Order;
import com.hmall.order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderStatusCheckListener {
    private final IOrderService orderService;
    private final RabbitTemplate rabbitTemplate;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConstants.DELAY_ORDER_QUEUE),
            exchange = @Exchange(value = MqConstants.DELAY_EXCHANGE, delayed = "true", type = ExchangeTypes.TOPIC),
            key = MqConstants.DELAY_ORDER_ROUTING_KEY
    ))
    public void listenOrderDelayMessage(MultiDelayMessage<Long> msg){
        // 1.查询订单状态
        Order order = orderService.getById(msg.getData());
        // 2.判断是否支付
        if (order.getStatus() == 2 || order == null){
            //已支付,结束
            return;
        }
        // 3. 未支付,判断是否存在延迟时间
        if(msg.hasNextDelay()){
            // 3.1 存在,重发
            Long nextDelay = msg.removeNextDelay();
            rabbitTemplate.convertAndSend(MqConstants.DELAY_EXCHANGE, MqConstants.DELAY_ORDER_ROUTING_KEY,
                    msg, new DelayMessageProcessor(nextDelay.intValue()));
            return;
        }
            // 3.2 不存在,取消订单并恢复库存
        orderService.cancelOrderAndRecoverStock(order.getId());
    }
}
