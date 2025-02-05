package ru.yandex.practicum.filmorate.exception;

public class ExceptionMessages {
    public static final String FILM_NOT_FOUND_ERROR = "Фильм с id = %d не найден";
    public static final String USER_NOT_FOUND_ERROR = "Пользователь с id = %d не найден";
    public static final String GENRE_NOT_FOUND_ERROR = "Жанр с id = %d не найден";
    public static final String GENRE_NOT_FOUND_FROM_LIST_ERROR = "Из списка id %s не все элементы найдены";
    public static final String MPAA_NOT_FOUND_ERROR = "Возрастной рейтинг с id = %d не найден";
    public static final String SELECT_ERROR = "Ошибка получения данных";
    public static final String INSERT_USERS_ERROR = "Не удалось добавить пользователя %s";
    public static final String UPDATE_USERS_ERROR = "Не удалось изменить пользователя id = %d";
    public static final String DELETE_USERS_ERROR = "Не удалось удалить пользователя id = %d";
    public static final String INSERT_FRIEND_ERROR = "Пользователю с id = %d не удалось добавить друга с id = %d";
    public static final String DELETE_FRIEND_ERROR = "У пользователя с id = %d не удалось удалить друга с id = %d";
    public static final String INSERT_FILM_ERROR = "Не удалось добавить фильм %s";
    public static final String UPDATE_FILM_ERROR = "Не удалось изменить фильм id = %d";
    public static final String DELETE_FILM_ERROR = "Не удалось удалить фильм id = %d";
    public static final String INSERT_LIKE_ERROR = "Пользователю с id = %d не удалось добавить лайк на фильм с id = %d";
    public static final String DELETE_LIKE_ERROR = "Пользователю с id = %d не удалось удалить лайк на фильме с id = %d";
    public static final String DIRECTOR_NOT_FOUND_ERROR = "Режиссер с id = %d не найден";
    public static final String INSERT_DIRECTOR_ERROR = "Не удалось добавить режиссера %s";
    public static final String UPDATE_DIRECTOR_ERROR = "Не удалось изменить режиссера id = %d";
    public static final String DELETE_DIRECTOR_ERROR = "Не удалось удалить режиссера с id = %d";
    public static final String DIRECTOR_NOT_FOUND_FROM_LIST_ERROR = "Из списка режиссеров с id %s не все элементы найдены";
    public static final String PARAM_VALUE_FILMS_BY_DIRECTOR_ERROR = "Неверно указан параметр сортировки, вы указали %s, допустимые значения: year, likes";
    public static final String PARAM_VALUE_SEARCH_FILMS_ERROR = "Неверно указан параметр поиска, вы указали %s, допустимые значения: director, title";
    public static final String REVIEW_NOT_FOUND_ERROR = "Отзыв с id = %d не найден";
    public static final String INSERT_REVIEW_ERROR = "Не удалось добавить отзыв %s";
    public static final String UPDATE_REVIEW_ERROR = "Не удалось изменить отзыв id = %d";
    public static final String DELETE_REVIEW_ERROR = "Не удалось удалить отзыв с id = %d";
    public static final String GET_FILM_REVIEWS_ERROR = "Не удалось получить все отзывы по фильму с id = %d";
    public static final String INSERT_REVIEWLIKE_ERROR = "Пользователю с id = %d не удалось добавить лайк на отзыв с id = %d";
    public static final String INSERT_REVIEWDISLIKE_ERROR = "Пользователю с id = %d не удалось добавить дизлайк на отзыв с id = %d";
    public static final String DELETE_REVIEWLIKE_ERROR = "Пользователю с id = %d не удалось удалить лайк на отзыв с id = %d";
    public static final String DELETE_REVIEWDISLIKE_ERROR = "Пользователю с id = %d не удалось удалить дизлайк на отзыв с id = %d";
    public static final String INSERT_EVENT_ERROR = "Не удалось добавить событие с id = %d";
}
