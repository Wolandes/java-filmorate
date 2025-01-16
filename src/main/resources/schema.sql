CREATE TABLE IF NOT EXISTS Film (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    releaseDate DATE,
    duration INT,
    mpa_rating_id INT,
    FOREIGN KEY (mpa_rating_id) REFERENCES Mpa_Rating(id)
);

CREATE TABLE IF NOT EXISTS Genre (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Mpa_Rating (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255),
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS Film_likes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    film_id INT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (film_id) REFERENCES Film(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Friends_Map (
    id_User INT NOT NULL,
    id_Friend INT NOT NULL,
    PRIMARY KEY (id_User, id_Friend),
    FOREIGN KEY (id_User) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_Friend) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Film_Genre (
    film_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES Film(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES Genre(id) ON DELETE CASCADE
);