package org.vescm.yerbamate.checkout.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.vescm.yerbamate.checkout.entity.CheckoutEntity;
import org.vescm.yerbamate.checkout.entity.CheckoutItemEntity;
import org.vescm.yerbamate.checkout.entity.ShippingEntity;
import org.vescm.yerbamate.checkout.event.CheckoutCreatedEvent;
import org.vescm.yerbamate.checkout.repository.CheckoutRepository;
import org.vescm.yerbamate.checkout.resource.checkout.CheckoutRequest;
import org.vescm.yerbamate.checkout.streaming.CheckoutCreatedSource;
import org.vescm.yerbamate.checkout.util.UUIDUtil;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final CheckoutCreatedSource checkoutCreatedSource;
    private final UUIDUtil uuidUtil;

    @Override
    public Optional<CheckoutEntity> create(CheckoutRequest checkoutRequest) {
        log.info("M=create, checkoutRequest={}", checkoutRequest);

        final CheckoutEntity checkoutEntity = CheckoutEntity.builder()
                .code(uuidUtil.createUUID().toString())
                .status(CheckoutEntity.Status.CREATED)
                .saveAddress(checkoutRequest.getSaveAddress())
                .saveInformation(checkoutRequest.getSaveInfo())
                .shipping(ShippingEntity.builder()
                        .address(checkoutRequest.getAddress())
                        .complement(checkoutRequest.getComplement())
                        .country(checkoutRequest.getCountry())
                        .state(checkoutRequest.getState())
                        .cep(checkoutRequest.getCep())
                        .build())
                .build();

        checkoutEntity.setItems(checkoutRequest.getProducts()
                .stream()
                .map(product -> CheckoutItemEntity.builder()
                        .checkout(checkoutEntity)
                        .product(product)
                        .build())
                .collect(Collectors.toList()));

        final CheckoutEntity entity = checkoutRepository.save(checkoutEntity);

        final CheckoutCreatedEvent checkoutCreatedEvent = CheckoutCreatedEvent.newBuilder()
                .setCheckoutCode(entity.getCode())
                .setStatus(entity.getStatus().name())
                .build();

        checkoutCreatedSource.output().send(MessageBuilder.withPayload(checkoutCreatedEvent).build());

        return Optional.of(entity);
    }

    @Override
    public Optional<CheckoutEntity> updateStatus(String checkoutCode, String paymentCode) {
        CheckoutEntity.Status paymentStatus = paymentCode == null ?
                CheckoutEntity.Status.REJECTED : CheckoutEntity.Status.APPROVED;

        final CheckoutEntity checkoutEntity = checkoutRepository
                .findByCode(checkoutCode)
                .orElse(CheckoutEntity.builder().build());

        checkoutEntity.setStatus(paymentStatus);

        return Optional.of(checkoutRepository.save(checkoutEntity));
    }
}
