package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.ShoppingCart;
import ru.yandex.practicum.dao.ShoppingCartState;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.NotAuthorizedException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.repo.ShoppingCartRepo;
import ru.yandex.practicum.dto.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepo shoppingCartRepo;

    private final ShoppingCartMapper shoppingCartMapper;

    @Transactional(readOnly = true)
    public ShoppingCartDto getCartByUsername(String username) {
        log.debug("Get cart by username {}", username);
        validateUserOrThrow(username);

        ShoppingCart cartByUsername = shoppingCartRepo.findOrCreateByUsername(username);

        return shoppingCartMapper.toDto(cartByUsername);
    }

    public ShoppingCartDto updateCart(String username, Map<UUID, Long> request) {
        validateUserOrThrow(username);

        ShoppingCart cartByUsername = shoppingCartRepo.findOrCreateByUsername(username);
        verifyCartActiveOrThrow(cartByUsername, username);

        request.forEach((id, quantity) -> cartByUsername.getProducts().merge(id, quantity, Long::sum));

        return shoppingCartMapper.toDto(cartByUsername);
    }

    public void deactivateCart(String username) {
        validateUserOrThrow(username);

        ShoppingCart cart = shoppingCartRepo.getByUsername(username);
        if (cart.getShoppingCartState().equals(ShoppingCartState.DEACTIVATE))
            log.info("Card is deactivated already by username {}", username);

        cart.setShoppingCartState(ShoppingCartState.DEACTIVATE);
    }

    public void removeProducts(String username, List<UUID> productIds) {
        validateUserOrThrow(username);

        Optional<ShoppingCart> byUsername = shoppingCartRepo.findByUsername(username);
        if (byUsername.isPresent()) {
            ShoppingCart shoppingCart = byUsername.get();
            if (shoppingCart.getProducts().isEmpty()) {
                throw new EntityNotFoundException("ids", ShoppingCart.class);
            }

            productIds.forEach(shoppingCart.getProducts()::remove);
        }
    }

    public ShoppingCartDto updateProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUserOrThrow(username);
        ShoppingCart cart = shoppingCartRepo.getByUsername(username);
        verifyCartActiveOrThrow(cart, username);

        cart.getProducts().put(request.getProductId(), request.getNewQuantity());

        return shoppingCartMapper.toDto(cart);
    }

    private void validateUserOrThrow(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedException(username);
        }
    }

    private void verifyCartActiveOrThrow(ShoppingCart cart, String username) {
        if (cart.getShoppingCartState() == ShoppingCartState.DEACTIVATE)
            throw new IllegalArgumentException(String
                    .format("Cannot change product quantity for username %s: card is deactivated ", username));
    }
}
