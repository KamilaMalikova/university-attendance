CREATE KEYSPACE IF NOT EXISTS university WITH replication = {'class': 'SimpleStrategy', 'replication_factor':1};
USE university ;
CREATE TABLE IF NOT EXISTS attendance (
    univ_id int,
    user_id int,
    date timestamp,
    is_in boolean,
    PRIMARY KEY (univ_id, user_id, date, is_in)
);

CREATE TABLE IF NOT EXISTS publications(
    univ_id int,
    user_id int,
    publication_id int,
    date timestamp,
    PRIMARY KEY ( univ_id, user_id, publication_id )
);

CREATE TABLE IF NOT EXISTS result (
    year int,
    univ_id int,
    user_id int,
    publications int,
    hour_in_univ int,
    PRIMARY KEY ( year, univ_id, user_id )
);
