package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.repository.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final JdbcMpaRepository mpaDbStorage;

    public Collection<Mpa> getAllMpa() {
        log.info("Получение информации об всех возрастных рейтингах");
        return mpaDbStorage.getAllMpa();
    }

    public Mpa getMpa(long id) {
        log.info("Получение информации об возрастном рейтинге с id: " + id);
        return mpaDbStorage.getMpa(id);
    }
}
