USE example_plugin;

DROP TABLE votes;

CREATE TABLE IF NOT EXISTS votes
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    uuid        VARCHAR(36) UNIQUE,
    player_name VARCHAR(16),
    voted_on    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO votes (uuid, player_name) VALUE (?, ?);



SELECT * FROM votes;