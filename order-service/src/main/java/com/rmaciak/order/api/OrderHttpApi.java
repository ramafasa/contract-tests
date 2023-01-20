package com.rmaciak.order.api;

import com.rmaciak.order.domain.OrderCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

import static com.rmaciak.order.domain.PaymentType.TRANSFER_ONLINE;

@RestController
@RequiredArgsConstructor
public class OrderHttpApi {

    private final OrderCreator orderCreator;

    @PutMapping("/order/{orderID}")
    public void createOrder(
            @PathVariable UUID orderID
    ) {
        orderCreator.createOrder(UUID.randomUUID(), orderID, BigDecimal.TEN, TRANSFER_ONLINE);
    }
}
