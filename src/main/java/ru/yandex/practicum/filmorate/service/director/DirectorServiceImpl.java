package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    @Override
    public Director getDirector(Long directorId) {
        return Optional.ofNullable(directorStorage.getDirector(directorId))
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.DIRECTOR_NOT_FOUND_ERROR, directorId)));
    }

    @Override
    public List<Director> getAllDirectors() {
        return Optional.ofNullable(directorStorage.getAllDirectors())
                .orElse(new ArrayList<>());
    }

    @Override
    public Director createDirector(Director director) {
        return Optional.ofNullable(directorStorage.createDirector(director))
                .orElseThrow(() -> new DbException(String.format(ExceptionMessages.INSERT_DIRECTOR_ERROR, director)));
    }

    @Override
    public Director updateDirector(Director director) {
        Optional.ofNullable(directorStorage.getDirector(director.getId()))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.DIRECTOR_NOT_FOUND_ERROR, director.getId())));
        return Optional.ofNullable(directorStorage.updateDirector(director))
                .orElseThrow(() -> new DbException(String.format(ExceptionMessages.UPDATE_DIRECTOR_ERROR, director.getId())));
    }

    @Override
    public void removeDirector(Long directorId) {
        Director director = Optional.ofNullable(directorStorage.getDirector(directorId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.DIRECTOR_NOT_FOUND_ERROR, directorId)));
        directorStorage.removeDirector(director);
    }
}
