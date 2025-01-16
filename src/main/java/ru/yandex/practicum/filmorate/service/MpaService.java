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

    public Collection<Mpa> getAllMpa(){
        return mpaDbStorage.getAllMpa();
    }

    public Mpa getMpa(long id){
        return mpaDbStorage.getMpa(id);
    }
}
