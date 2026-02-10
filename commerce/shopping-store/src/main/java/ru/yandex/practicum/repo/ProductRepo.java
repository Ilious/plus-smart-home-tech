package ru.yandex.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.Product;
import ru.yandex.practicum.enums.ProductCategory;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {

    List<Product> findAllByCategory(ProductCategory category, Pageable pageable);
}
