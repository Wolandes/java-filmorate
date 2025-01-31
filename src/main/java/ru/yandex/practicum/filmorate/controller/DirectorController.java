package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.validation.ValidatorGroups;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirector(@PathVariable("id") Long directorId) {
        log.info("Вызван метод GET /directors/{}", directorId);
        Director director = directorService.getDirector(directorId);
        log.info("Метод GET /genres/{} вернул ответ {}", directorId, director);
        return director;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getAllDirectors() {
        log.info("Вызван метод GET /directors");
        List<Director> directors = directorService.getAllDirectors();
        log.info("Метод GET /directors вернул ответ {}", directors);
        return directors;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({ValidatorGroups.Create.class})
    public Director createDirector(@RequestBody @Valid Director director) {
        log.info("Вызван метод POST /directors {}", director);
        Director newDirector = directorService.createDirector(director);
        log.info("Метод POST /directors вернул ответ {}", newDirector);
        return newDirector;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated({ValidatorGroups.Update.class})
    public Director updateDirector(@RequestBody @Valid Director director) {
        log.info("Вызван метод PUT /directors {}", director);
        Director newDirector = directorService.updateDirector(director);
        log.info("Метод PUT /directors вернул ответ {}", newDirector);
        return newDirector;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirector(@PathVariable("id") Long directorId) {
        log.info("Вызван метод DELETE /directors/{}", directorId);
        directorService.removeDirector(directorId);
        log.info("Метод DELETE /directors/{} выполнен успешно", directorId);
    }
}
