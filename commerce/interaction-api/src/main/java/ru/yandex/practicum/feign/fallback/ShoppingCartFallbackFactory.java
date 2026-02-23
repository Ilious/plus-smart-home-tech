package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.ServiceNotUpException;
import ru.yandex.practicum.feign.client.ShoppingCartClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class ShoppingCartFallbackFactory implements FallbackFactory<ShoppingCartClient> {

    @Override
    public ShoppingCartClient create(Throwable cause) {
        log.error("Shopping-cart link failed. Circuit opened due to: {}", cause.getMessage());

        return new ShoppingCartClient() {
            @Override
            public ShoppingCartDto getCart(String username) {
                log.error("Cannot get cart by username {}. Shopping-cart is down", username);
                throw new ServiceNotUpException("Get cart failed", cause);
            }

            @Override
            public ShoppingCartDto updateCart(String username, Map<UUID, Long> request) {
                log.error("Cannot update cart by username {}. Shopping-cart is down", username);
                throw new ServiceNotUpException("Update cart failed", cause);
            }

            @Override
            public void deactivateCart(String username) {
                log.error("Cannot deactivate cart by username {}. Shopping-cart is down", username);
                throw new ServiceNotUpException("Deactivate cart failed", cause);
            }

            @Override
            public ShoppingCartDto updateProductQuantity(String username, ChangeProductQuantityRequest request) {
                log.error("Cannot update product quantity by username {}. Shopping-cart is down", username);
                throw new ServiceNotUpException("Update product quantity failed", cause);
            }

            @Override
            public ShoppingCartDto removeProducts(String username, List<UUID> productIds) {
                log.error("Cannot remove products by username {}. Shopping-cart is down", username);
                throw new ServiceNotUpException("Remove products failed", cause);
            }
        };
    }
}
