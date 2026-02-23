package ru.yandex.practicum.dao;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_bookings")
@Getter @Setter @ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "booking_id")
    private UUID bookingId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @ElementCollection
    @Column(name = "quantity")
    @MapKeyColumn(name = "product_id")
    @CollectionTable(name = "bookings_products",
            joinColumns = @JoinColumn(name = "booking_id"))
    private Map<UUID, Long> products;
}
