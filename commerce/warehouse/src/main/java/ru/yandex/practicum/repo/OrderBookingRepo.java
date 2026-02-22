package ru.yandex.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.OrderBooking;

import java.util.Optional;
import java.util.UUID;

public interface OrderBookingRepo extends JpaRepository<OrderBooking, UUID> {

    Optional<OrderBooking> findByOrderId(UUID orderId);
}
