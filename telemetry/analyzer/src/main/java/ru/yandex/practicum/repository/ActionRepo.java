package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.dao.Action;

public interface ActionRepo extends JpaRepository<Action, Long> {
}
