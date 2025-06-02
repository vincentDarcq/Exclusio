CREATE TABLE IF NOT EXISTS movies (
    id SERIAL PRIMARY KEY,
    titre TEXT,
    synopsis VARCHAR(3000),
    cov_portrait TEXT,
    cov_paysage TEXT,
    grade FLOAT,
    allo_grade FLOAT,
    imdb_grade FLOAT,
    year INT,
    pegi TEXT,
    avertissement TEXT,
    time TEXT,
    code_html_allocine TEXT,
    code_html_imdb TEXT
);

-- Tables pour les collections
CREATE TABLE IF NOT EXISTS movie_entity_casting (
    movie_entity_id BIGINT REFERENCES movies(id) ON DELETE CASCADE,
    casting TEXT
);

CREATE TABLE IF NOT EXISTS movie_entity_realisateur (
    movie_entity_id BIGINT REFERENCES movies(id) ON DELETE CASCADE,
    realisateur TEXT
);

CREATE TABLE IF NOT EXISTS movie_entity_genre (
    movie_entity_id BIGINT REFERENCES movies(id) ON DELETE CASCADE,
    genre TEXT
);