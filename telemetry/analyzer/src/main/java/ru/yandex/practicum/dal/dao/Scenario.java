package ru.yandex.practicum.dal.dao;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "scenarios")
@Getter @Setter
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hubId;

    private String name;

    @OneToMany
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

    @OneToMany
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
