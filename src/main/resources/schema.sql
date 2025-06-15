CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(25) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS matches (
    match_id VARCHAR(36),
    created_at TIMESTAMP NOT NULL,
    match_type VARCHAR(50),
    race_to_leg INT,
    race_to_set INT,
    winner_id VARCHAR(36),
    match_status VARCHAR(15) NOT NULL,
    PRIMARY KEY (match_id),
    CONSTRAINT fk_winner
        FOREIGN KEY (winner_id)
            REFERENCES users(user_id)
            ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS sets (
    set_id VARCHAR(36),
    match_id VARCHAR(36) NOT NULL,
    set_winner_id VARCHAR(36),
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (set_id),
    CONSTRAINT fk_match_id
            FOREIGN KEY (match_id)
            REFERENCES matches(match_id)
            ON DELETE CASCADE,
    CONSTRAINT fk_set_winner
        FOREIGN KEY (set_winner_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS legs (
    leg_id VARCHAR(36),
    set_id VARCHAR(36) NOT NULL,
    match_id VARCHAR(36) NOT NULL,
    turn_index INT,
    winner_id VARCHAR(36),
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (leg_id),

    CONSTRAINT fk_match_id
        FOREIGN KEY (match_id)
        REFERENCES matches(match_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_set_id
        FOREIGN KEY (set_id)
        REFERENCES sets(set_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_winner_id
        FOREIGN KEY (winner_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS visits (
    visit_id VARCHAR(36),
    leg_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    score INT,
    checkout BOOLEAN NOT NULL,
    PRIMARY KEY (visit_id),

    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_leg_id
        FOREIGN KEY (leg_id)
        REFERENCES legs(leg_id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS matches_users (
    match_id VARCHAR(36),
    user_id VARCHAR(36),
    position INT,

    PRIMARY KEY (match_id, user_id),

    CONSTRAINT fk_match_id
        FOREIGN KEY (match_id)
        REFERENCES matches(match_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);
