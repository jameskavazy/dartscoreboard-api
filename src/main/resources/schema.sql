CREATE TABLE IF NOT EXISTS matches (
    id VARCHAR(100) NOT NULL,
    created_at timestamp NOT NULL,
    type VARCHAR(50),
    race_to_leg INT,
    race_to_set INT,
    winner_id INT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE
);

