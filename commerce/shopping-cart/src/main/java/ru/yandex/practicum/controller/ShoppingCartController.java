package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feign.ShoppingCartOperations;
import ru.yandex.practicum.service.ShoppingCartService;
import ru.yandex.practicum.dto.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("${app.api-version}" + "/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartOperations {

    private final ShoppingCartService shoppingCardService;

    @Override
    public ShoppingCartDto getCart(@RequestParam final String username) {
        return shoppingCardService.getCartByUsername(username);
    }

    @Override
    public ShoppingCartDto updateCart(@RequestParam final String username,
                                      @RequestBody Map<UUID, Long> request) {
        return shoppingCardService.updateCart(username, request);
    }

    @Override
    public void deactivateCart(@RequestParam String username) {
        shoppingCardService.deactivateCart(username);
    }

    @Override
    public ShoppingCartDto updateProductQuantity(@RequestParam final String username,
                                                              @Valid
                                                              @RequestBody ChangeProductQuantityRequest request) {
        return shoppingCardService.updateProductQuantity(username, request);
    }

    @Override
    public void removeProducts(@RequestParam final String username, List<UUID> productIds) {
        shoppingCardService.removeProducts(username, productIds);
    }

}
