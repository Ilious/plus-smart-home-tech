package ru.yandex.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.ShoppingCart;
import ru.yandex.practicum.exception.EntityNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepo extends JpaRepository<ShoppingCart, UUID> {

    Optional<ShoppingCart> findByUsername(String username);

    default ShoppingCart getByUsername(String username) {
        if (findByUsername(username).isPresent()) {
            return findByUsername(username).get();
        }
        throw new EntityNotFoundException("username", username, ShoppingCart.class);
    }

    default ShoppingCart findOrCreateByUsername(String username) {
        return findByUsername(username)
                .orElse(this.save(ShoppingCart.builder()
                        .username(username)
                        .build()
                ));
    }
}
