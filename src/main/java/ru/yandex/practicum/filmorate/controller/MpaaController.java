package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpaa;
import ru.yandex.practicum.filmorate.service.mpaa.MpaaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaaController {
    private final MpaaService mpaaService;

    @GetMapping
    @RequestMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mpaa getMpaa(@PathVariable("id") Long mpaaId) {
        log.info("Вызван метод GET /mpa/{}", mpaaId);
        Mpaa mpaa = mpaaService.getMpaa(mpaaId);
        log.info("Метод GET /mpa/{} вернул ответ {}", mpaaId, mpaa);
        return mpaa;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Mpaa> getAllMpaa() {
        log.info("Вызван метод GET /mpaa");
        List<Mpaa> mpaa = mpaaService.getAllMpaa();
        log.info("Метод GET /mpaa вернул ответ {}", mpaa);
        return mpaa;
    }
}
