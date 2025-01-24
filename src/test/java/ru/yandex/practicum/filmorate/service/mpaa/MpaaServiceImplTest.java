package ru.yandex.practicum.filmorate.service.mpaa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.mapper.MpaaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpaa;
import ru.yandex.practicum.filmorate.storage.mpaa.MpaaDbStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaaServiceImpl.class})
@ContextConfiguration(classes = {MpaaDbStorage.class, MpaaRowMapper.class})
public class MpaaServiceImplTest {
    private final MpaaService mpaaService;

    static Mpaa getTestMpaa() {
        return new Mpaa(1L, "G");
    }

    @Test
    @DisplayName("Получить возрастной рейтинг по id")
    public void shouldGetMpaa() {
        Mpaa testMpaa = getTestMpaa();
        Optional<Mpaa> mpaaOptional = Optional.ofNullable(mpaaService.getMpaa(testMpaa.getId()));

        assertThat(mpaaOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testMpaa);
    }

    @Test
    @DisplayName("Список возрастных рейтингов")
    public void shouldGetAllMpaa() {
        List<Mpaa> testMpaa = new ArrayList<>();
        testMpaa.add(new Mpaa(1L, "G"));
        testMpaa.add(new Mpaa(2L, "PG"));
        testMpaa.add(new Mpaa(3L, "PG-13"));
        testMpaa.add(new Mpaa(4L, "R"));
        testMpaa.add(new Mpaa(5L, "NC-17"));
        Optional<List<Mpaa>> mpaaOptional = Optional.ofNullable(mpaaService.getAllMpaa());

        assertThat(mpaaOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testMpaa);
    }
}
