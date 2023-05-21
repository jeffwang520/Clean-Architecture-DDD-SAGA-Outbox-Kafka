package com.food.ordering.system.restaurant.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.RestaurantRequestAvroModel;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantApplicationServiceException;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.food.ordering.system.restaurant.service.domain.ports.input.message.listener.RestaurantRequestMessageListener;
import com.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class RestaurantRequestKafkaListener implements KafkaConsumer<RestaurantRequestAvroModel> {

  private final RestaurantRequestMessageListener restaurantRequestMessageListener;
  private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;

  public RestaurantRequestKafkaListener(
    RestaurantRequestMessageListener restaurantRequestMessageListener,
    RestaurantMessagingDataMapper restaurantMessagingDataMapper) {
    this.restaurantRequestMessageListener = restaurantRequestMessageListener;
    this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
  }

  @Override
  @KafkaListener(id = "${kafka-consumer-config.restaurant-status-consumer-group-id}",
    topics = "${restaurant-service.restaurant-status-request-topic-name}")
  public void receive(
    @Payload List<RestaurantRequestAvroModel> messages,
    @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
    @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
    @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    log.info("{} number of orders status requests received with keys {}, partitions {} and offsets {}" +
        ", sending for restaurant status",
      messages.size(), keys.toString(), partitions.toString(), offsets.toString());

    messages.forEach(restaurantStatusRequestAvroModel -> {
      try {
        log.info("Processing order status for order id: {}", restaurantStatusRequestAvroModel.getOrderId());
        restaurantRequestMessageListener.approveOrder(restaurantMessagingDataMapper.
          restaurantStatusRequestAvroModelToRestaurantStatus(restaurantStatusRequestAvroModel));
      } catch (DataAccessException e) {
        SQLException sqlException = (SQLException) e.getRootCause();
        if (sqlException != null && sqlException.getSQLState() != null &&
          PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
          //NO-OP for unique constraint exception
          log.error("Caught unique constraint exception with sql state: {} " +
              "in RestaurantStatusRequestKafkaListener for order id: {}",
            sqlException.getSQLState(), restaurantStatusRequestAvroModel.getOrderId());
        } else {
          throw new RestaurantApplicationServiceException("Throwing DataAccessException in" +
            " RestaurantStatusRequestKafkaListener: " + e.getMessage(), e);
        }
      } catch (RestaurantNotFoundException e) {
        //NO-OP for RestaurantNotFoundException
        log.error("No restaurant found for restaurant id: {}, and order id: {}",
          restaurantStatusRequestAvroModel.getRestaurantId(),
          restaurantStatusRequestAvroModel.getOrderId());
      }
    });
  }

}
