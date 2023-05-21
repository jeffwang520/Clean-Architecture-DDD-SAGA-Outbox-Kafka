package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurant.RestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.*;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.food.ordering.system")
public class OrderTestConfiguration {

  @Bean
  public PaymentRequestMessagePublisher paymentRequestMessagePublisher() {
    return Mockito.mock(PaymentRequestMessagePublisher.class);
  }

  @Bean
  public RestaurantRequestMessagePublisher restaurantApprovedRequestMessagePublisher() {
    return Mockito.mock(RestaurantRequestMessagePublisher.class);
  }

  @Bean
  public OrderRepository orderRepository() {
    return Mockito.mock(OrderRepository.class);
  }

  @Bean
  public CustomerRepository customerRepository() {
    return Mockito.mock(CustomerRepository.class);
  }

  @Bean
  public RestaurantRepository restaurantRepository() {
    return Mockito.mock(RestaurantRepository.class);
  }

  @Bean
  public PaymentOutboxRepository paymentOutboxRepository() {
    return Mockito.mock(PaymentOutboxRepository.class);
  }

  @Bean
  public RestaurantOutboxRepository approvedOutboxRepository() {
    return Mockito.mock(RestaurantOutboxRepository.class);
  }

  @Bean
  public OrderDomainService orderDomainService() {
    return new OrderDomainServiceImpl();
  }

}
