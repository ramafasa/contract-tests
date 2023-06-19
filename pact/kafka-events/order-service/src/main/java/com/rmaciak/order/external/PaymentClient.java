package com.rmaciak.order.external;


import com.rmaciak.order.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentClient implements PaymentService {

    @Override
    public boolean executeOnlinePayment(UUID accountId, UUID orderId, BigDecimal amount) {
        return true;
    }
}
