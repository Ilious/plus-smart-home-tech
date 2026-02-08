package ru.yandex.practicum.dao;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "scenarios")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hubId;

    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @MapKeyColumn(
            name = "sensor_id", table = "scenario_conditions"
    )
    @JoinTable(
         name = "scenario_conditions",
         joinColumns = @JoinColumn(name = "scenario_id"),
         inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    private Map<String, Condition> conditions = new HashMap<>();

    @OneToMany(cascade = CascadeType.ALL)
    @MapKeyColumn(
            name = "sensor_id", table = "scenario_conditions"
    )
    @JoinTable(
         name = "scenario_actions",
         joinColumns = @JoinColumn(name = "scenario_id"),
         inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Map<String, Action> actions = new HashMap<>();
}
