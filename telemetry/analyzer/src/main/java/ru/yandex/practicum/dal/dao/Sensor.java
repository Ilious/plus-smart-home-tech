package ru.yandex.practicum.dal.dao;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensors")
@Getter @Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Sensor {

    @Id
    private String id;

    private String hubId;
}
