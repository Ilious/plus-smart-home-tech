package ru.yandex.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.Payment;

import java.util.UUID;

public interface PaymentRepo extends JpaRepository<Payment, UUID> {
}
