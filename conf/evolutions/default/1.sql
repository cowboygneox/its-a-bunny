# Expression History schema

# --- !Ups

CREATE TABLE ExpressionHistory (
    id         serial PRIMARY KEY,
    expression text   NOT NULL,
    result     real   NOT NULL,
    ts         bigint NOT NULL
);

# --- !Downs

DROP TABLE ExpressionHistory;