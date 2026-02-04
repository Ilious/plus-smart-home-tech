package ru.yandex.practicum.repository;

import org.springframework.data.repository.CrudRepository;
import ru.yandex.practicum.dal.dao.Condition;

public interface ConditionRepo extends CrudRepository<Condition, Long> {
}
