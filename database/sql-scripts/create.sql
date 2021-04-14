CREATE TABLE register
(
    name  VARCHAR(255) NOT NULL UNIQUE,
    amount INT NOT NULL,
    PRIMARY KEY (name)
);