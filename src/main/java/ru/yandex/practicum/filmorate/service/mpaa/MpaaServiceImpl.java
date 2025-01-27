package ru.yandex.practicum.filmorate.service.mpaa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpaa;
import ru.yandex.practicum.filmorate.storage.mpaa.MpaaStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaaServiceImpl implements MpaaService {
    private final MpaaStorage mpaaStorage;

    @Override
    public Mpaa getMpaa(Long mpaaId) {
        return Optional.ofNullable(mpaaStorage.getMpaa(mpaaId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.MPAA_NOT_FOUND_ERROR, mpaaId)));
    }

    @Override
    public List<Mpaa> getAllMpaa() {
        return Optional.ofNullable(mpaaStorage.getAllMpaa())
                .orElse(new ArrayList<>());
    }
}
