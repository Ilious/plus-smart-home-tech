package ru.yandex.practicum.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
