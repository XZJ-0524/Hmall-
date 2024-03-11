package com.hmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.order.domain.dto.OrderFormDTO;
import com.hmall.order.domain.po.Order;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjzjzj
 * @since 2023-12-05
 */
public interface IOrderService extends IService<Order> {

    Long createOrder(OrderFormDTO orderFormDTO);

    void markOrderPaySuccess(Long orderId);
    void cancelOrderAndRecoverStock(Long orderId);
}
