package ru.yandex.practicum.dao;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "shopping_carts")
@Getter @Setter @ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID shoppingCartId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShoppingCartState shoppingCartState = ShoppingCartState.ACTIVE;

    @Column(nullable = false)
    private String username;

    @ElementCollection
    @CollectionTable(name = "shopping_cart_products", joinColumns = @JoinColumn(name = "shopping_cart_id"))
    @MapKeyColumn(name="product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products = new HashMap<>();
}
