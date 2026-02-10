package ru.yandex.practicum.feign;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartOperations {
    @GetMapping
    ShoppingCartDto getCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto updateCart(@RequestParam String username,
                               @RequestBody Map<UUID, Long> request);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    void deactivateCart(@RequestParam String username);

    @PostMapping("/change-quantity")
    ShoppingCartDto updateProductQuantity(@RequestParam String username,
                                                       @Valid
                                                       @RequestBody ChangeProductQuantityRequest request);
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/remove")
    void removeProducts(@RequestParam String username, List<UUID> productIds);
}
