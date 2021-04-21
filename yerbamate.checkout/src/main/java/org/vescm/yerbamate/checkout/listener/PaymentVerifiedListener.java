package org.vescm.yerbamate.checkout.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.vescm.yerbamate.checkout.entity.CheckoutEntity;
import org.vescm.yerbamate.checkout.service.CheckoutService;
import org.vescm.yerbamate.checkout.streaming.PaymentPaidSink;
import org.vescm.yerbamate.payment.event.PaymentCreatedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentVerifiedListener {
    private final CheckoutService checkoutService;

    @StreamListener(PaymentPaidSink.INPUT)
    public void handler(PaymentCreatedEvent paymentCreatedEvent) {
        log.info("CheckoutCreatedListener={}", paymentCreatedEvent);

        CheckoutEntity checkoutEntity = checkoutService
                .updateStatus(
                        String.valueOf(paymentCreatedEvent.getCheckoutCode()),
                        String.valueOf(paymentCreatedEvent.getPaymentCode())).orElseThrow();
    }
}
