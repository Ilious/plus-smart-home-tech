package ru.yandex.practicum.dal.dao;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "scenarios")
@Getter @Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hubId;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapKeyColumn(
            table = "scenarios_conditions",
            name = "sensor_id"
    )
    @JoinTable(
         name = "scenarios_conditions",
         joinColumns = @JoinColumn(name = "scenario_id"),
         inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    private Map<String, Condition> conditions = new HashMap<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapKeyColumn(
            table = "scenarios_actions",
            name = "sensor_id"
    )
    @JoinTable(
         name = "scenarios_conditions",
         joinColumns = @JoinColumn(name = "scenario_id"),
         inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Map<String, Action> actions = new HashMap<>();
}
