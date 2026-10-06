package ru.yandex.practicum.dto.payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.enums.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @ToString
public class PaymentDto {

    private UUID paymentId;

    private BigDecimal totalSum;

    private BigDecimal totalDelivery;

    private BigDecimal totalFee;

    private PaymentState state;
}
