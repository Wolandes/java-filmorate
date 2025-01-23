package ru.yandex.practicum.filmorate.storage.mpaa;

import ru.yandex.practicum.filmorate.model.Mpaa;

import java.util.List;

public interface MpaaStorage {
    Mpaa getMpaa(Long mpaaId);

    List<Mpaa> getAllMpaa();
}
