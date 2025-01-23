package ru.yandex.practicum.filmorate.service.mpaa;

import ru.yandex.practicum.filmorate.model.Mpaa;

import java.util.List;

public interface MpaaService {
    Mpaa getMpaa(Long mpaaId);

    List<Mpaa> getAllMpaa();
}
