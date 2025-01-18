# java-filmorate
Template repository for Filmorate project.

Основные таблицы:![QuickDBD-Free Diagram (7)](https://github.com/user-attachments/assets/af1dcd05-f794-4059-9675-48c3795bcfc4)

Film - Хранит информацию о фильмах: название, описание, дата выхода, длительность, рейтинг MPA и пользовательский рейтинг. Связана с таблицей Rating для хранения пользовательских рейтингов. Использует внешний ключ mpa_rating_id для ссылки на таблицу MPA_Rating.

MPA_Rating - Содержит предопределённые значения рейтингов фильмов (например, G, PG, R).

User - Хранит данные пользователей, включая email, логин, имя и дату рождения. Связана с таблицей Friendship для управления друзьями.

Genre - Содержит список жанров фильмов.

Rating - Хранит пользовательские оценки фильмов.

Film_Genres - Таблица связи "многие ко многим" между фильмами и жанрами.

Film_likes - Таблица для отслеживания лайков фильмов пользователями.
