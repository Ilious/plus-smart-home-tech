package ru.yandex.practicum.dao;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensors")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

    @Id
    private String id;

    private String hubId;
}
