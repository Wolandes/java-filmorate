# java-filmorate
Template repository for Filmorate project.
![QuickDBD-Free Diagram (5).png](..%2FDownloads%2FQuickDBD-Free%20Diagram%20%285%29.png)

Основные таблицы:

Film - Хранит информацию о фильмах: название, описание, дата выхода, длительность, рейтинг MPA и пользовательский рейтинг. Связана с таблицей Rating для хранения пользовательских рейтингов. Использует внешний ключ mpa_rating_id для ссылки на таблицу MPA_Rating.

MPA_Rating - Содержит предопределённые значения рейтингов фильмов (например, G, PG, R).

User - Хранит данные пользователей, включая email, логин, имя и дату рождения. Связана с таблицей Friendship для управления друзьями.

Friendship - Отражает социальные связи между пользователями.Поля requester_id и responder_id представляют инициатора и получателя заявки.Поле status указывает статус дружбы (например, pending, accepted, rejected).

Genre - Содержит список жанров фильмов.

Rating - Хранит пользовательские оценки фильмов.

Film_Genres - Таблица связи "многие ко многим" между фильмами и жанрами.

Film_likes - Таблица для отслеживания лайков фильмов пользователями.