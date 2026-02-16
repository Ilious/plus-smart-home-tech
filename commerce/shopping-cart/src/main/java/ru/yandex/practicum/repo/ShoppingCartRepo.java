package ru.yandex.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.ShoppingCart;
import ru.yandex.practicum.dao.ShoppingCartState;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface ShoppingCartRepo extends JpaRepository<ShoppingCart, UUID> {

    List<ShoppingCart> findAllByUsernameAndShoppingCartState(String username, ShoppingCartState state);

    List<ShoppingCart> findAllByUsername(String username);

    default ShoppingCart findOrCreateByUsername(String username) {
        List<ShoppingCart> activeCarts = findAllByUsernameAndShoppingCartState(username, ShoppingCartState.ACTIVE);

        if (!activeCarts.isEmpty()) {
            return activeCarts.getFirst();
        }

        return this.save(ShoppingCart.builder()
                .username(username)
                .products(new HashMap<>())
                .shoppingCartState(ShoppingCartState.ACTIVE)
                .build());
    }
}
