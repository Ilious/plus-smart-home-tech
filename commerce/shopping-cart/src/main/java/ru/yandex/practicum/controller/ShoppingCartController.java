package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.feign.ShoppingCartOperations;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${app.api-version}" + "/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartOperations {

    private final ShoppingCartService shoppingCardService;

    @Override
    public ShoppingCartDto getCart(String username) {
        return shoppingCardService.getCartByUsername(username);
    }

    @Override
    public ShoppingCartDto updateCart(String username, Map<UUID, Long> request) {
        return shoppingCardService.updateCart(username, request);
    }

    @Override
    public void deactivateCart(String username) {
        shoppingCardService.deactivateCart(username);
    }

    @Override
    public ShoppingCartDto updateProductQuantity(String username, ChangeProductQuantityRequest request) {
        return shoppingCardService.updateProductQuantity(username, request);
    }

    @Override
    public ShoppingCartDto removeProducts(String username, List<UUID> productIds) {
        return shoppingCardService.removeProducts(username, productIds);
    }

}
