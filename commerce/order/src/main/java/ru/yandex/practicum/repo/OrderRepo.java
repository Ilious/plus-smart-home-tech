package ru.yandex.practicum.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.Order;
import ru.yandex.practicum.exception.EntityNotFoundException;

import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID> {

    Page<Order> findByUsername(String username, Pageable pageable);

    default Order getOrderById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id), Order.class));
    }
}
