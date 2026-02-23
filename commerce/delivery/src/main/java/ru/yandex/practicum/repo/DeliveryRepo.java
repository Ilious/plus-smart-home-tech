package ru.yandex.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.Delivery;
import ru.yandex.practicum.exception.EntityNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepo extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByOrderId(UUID orderId);

    default Delivery getDeliveryByOrderId(UUID orderId) {
        return this.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(orderId), Delivery.class));
    }
}
