package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaRepository {
    public Collection<Mpa> getAllMpa();

    public Mpa getMpa(long id);
}
