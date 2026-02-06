package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.Action;

public interface ActionRepo extends JpaRepository<Action, Long> {
}
