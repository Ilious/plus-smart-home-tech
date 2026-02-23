package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class AssemblyProductRequest {

    @NotNull(message = "assembly must contain products")
    private Map<UUID, Long> products;

    @NotNull(message = "assembly can't be without orderId")
    private UUID orderId;
}
