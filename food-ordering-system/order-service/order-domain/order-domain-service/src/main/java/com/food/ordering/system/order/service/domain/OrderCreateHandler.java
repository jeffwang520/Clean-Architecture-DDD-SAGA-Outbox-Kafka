package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderRequest;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHandler {

  private final OrderCreateHelper orderCreateHelper;
  private final OrderDataMapper orderDataMapper;
  private final PaymentOutboxHelper paymentOutboxHelper;
  private final OrderSagaHelper orderSagaHelper;

  public OrderCreateHandler(
    OrderCreateHelper orderCreateHelper,
    OrderDataMapper orderDataMapper,
    PaymentOutboxHelper paymentOutboxHelper,
    OrderSagaHelper orderSagaHelper) {
    this.orderCreateHelper = orderCreateHelper;
    this.orderDataMapper = orderDataMapper;
    this.paymentOutboxHelper = paymentOutboxHelper;
    this.orderSagaHelper = orderSagaHelper;
  }

  @Transactional
  public CreateOrderResponse createOrderResponse(CreateOrderRequest createOrderRequest) {
    OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrderEvent(createOrderRequest);
    log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
    CreateOrderResponse createOrderResponse = orderDataMapper.createOrderResponse(
      orderCreatedEvent.getOrder(),
      "Order created successfully");

    paymentOutboxHelper.savePaymentOutboxMessage(
      orderDataMapper.orderPaymentEventCreatedPayload(orderCreatedEvent),
      orderCreatedEvent.getOrder().getOrderStatus(),
      orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.getOrder().getOrderStatus()),
      OutboxStatus.STARTED,
      UUID.randomUUID());

    log.info("Returning CreateOrderResponse with order id: {}", orderCreatedEvent.getOrder().getId());

    return createOrderResponse;
  }
}