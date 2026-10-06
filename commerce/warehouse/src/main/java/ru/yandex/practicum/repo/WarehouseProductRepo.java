package ru.yandex.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.WarehouseProduct;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WarehouseProductRepo extends JpaRepository<WarehouseProduct, UUID> {

    List<WarehouseProduct> findAllByProductIdIn(Set<UUID> productIds);
}
