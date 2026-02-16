package ru.yandex.practicum.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.Product;
import ru.yandex.practicum.enums.ProductCategory;

import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {

    Page<Product> findAllByCategory(ProductCategory category, Pageable pageable);
}
