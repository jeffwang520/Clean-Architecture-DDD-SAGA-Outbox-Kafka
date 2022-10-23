package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.EmptyEvent;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse
  , EmptyEvent, OrderCancelledEvent> {
  private final OrderDomainService orderDomainService;
  private final OrderSagaHelper orderSagaHelper;

  public OrderApprovalSaga(
    OrderDomainService orderDomainService,
    OrderSagaHelper orderSagaHelper) {
    this.orderDomainService = orderDomainService;
    this.orderSagaHelper = orderSagaHelper;
  }

  @Override
  @Transactional
  public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());
    Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
    orderDomainService.approveOrder(order);
    orderSagaHelper.saveOrder(order);
    log.info("Order with id: {} is approved", order.getId().getValue());
    return EmptyEvent.INSTANCE;
  }

  @Override
  @Transactional
  public OrderCancelledEvent rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("Cancelling order with id: {}", restaurantApprovalResponse.getOrderId());
    Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
    OrderCancelledEvent domainEvent = orderDomainService.cancelOrderPayment(order,
      restaurantApprovalResponse.getFailureMessages());
    orderSagaHelper.saveOrder(order);
    log.info("Order with id: {} is cancelling", order.getId().getValue());
    return domainEvent;
  }
}
