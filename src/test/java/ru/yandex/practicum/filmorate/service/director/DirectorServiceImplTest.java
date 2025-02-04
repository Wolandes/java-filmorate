package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DirectorServiceImpl.class})
@ContextConfiguration(classes = {DirectorDbStorage.class, DirectorRowMapper.class})
public class DirectorServiceImplTest {
    private final DirectorService directorService;

    static Director getTestDirector() {
        return new Director(1L, "test director");
    }

    @Test
    @DisplayName("Получить режиссера по id")
    public void shouldGetDirector() {
        Director testDirector = getTestDirector();
        Optional<Director> directorOptional = Optional.ofNullable(directorService.getDirector(testDirector.getId()));

        assertThat(directorOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testDirector);
    }

    @Test
    @DisplayName("Список режиссеров")
    public void shouldGetAllDirectors() {
        List<Director> testDirector = new ArrayList<>();
        testDirector.add(directorService.getDirector(1L));
        testDirector.add(directorService.getDirector(2L));
        testDirector.add(directorService.getDirector(3L));
        testDirector.add(directorService.getDirector(4L));
        testDirector.add(directorService.getDirector(5L));
        Optional<List<Director>> directorOptional = Optional.ofNullable(directorService.getAllDirectors());

        assertThat(directorOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testDirector);
    }

    @Test
    @DisplayName("Добавить режиссера")
    public void shouldCreateDirector() {
        Director testDirector = new Director(null, "New test director");
        Optional<Director> directorOptional = Optional.ofNullable(directorService.createDirector(testDirector));
        testDirector.setId(directorOptional.isPresent() ? directorOptional.get().getId() : 0L);

        assertThat(directorOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testDirector);
    }

    @Test
    @DisplayName("Изменит режиссера")
    public void shouldUpdateDirector() {
        Director testDirector = getTestDirector().toBuilder()
                .name("Update test director")
                .build();
        Optional<Director> directorOptional = Optional.ofNullable(directorService.updateDirector(testDirector));

        assertThat(directorOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testDirector);
    }

    @Test
    @DisplayName("Удалить режиссера")
    public void shouldRemoveDirector() {
        List<Director> testDirector = new ArrayList<>();
        testDirector.add(directorService.getDirector(1L));
        testDirector.add(directorService.getDirector(2L));
        testDirector.add(directorService.getDirector(3L));
        testDirector.add(directorService.getDirector(4L));
        testDirector.add(directorService.getDirector(5L));
        Optional<List<Director>> directorOptional = Optional.ofNullable(directorService.getAllDirectors());

        assertThat(directorOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testDirector);

        testDirector.remove(4);
        directorService.removeDirector(5L);
        directorOptional = Optional.ofNullable(directorService.getAllDirectors());

        assertThat(directorOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testDirector);
    }
}
