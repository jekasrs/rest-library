
CREATE TABLE book_types (
    id        INT UNSIGNED NOT NULL AUTO_INCREMENT ,
    name      VARCHAR(50) NOT NULL,
    cnt       INT UNSIGNED,
    fine      DECIMAL(18, 2),
    day_count INT UNSIGNED,
    PRIMARY KEY (id)
);

CREATE TABLE books (
    id       INT UNSIGNED NOT NULL AUTO_INCREMENT ,
    name     VARCHAR(50) NOT NULL,
    cnt      INT UNSIGNED,
    type_id  INT UNSIGNED,
    FOREIGN KEY (type_id) REFERENCES BOOK_TYPES (id),
    PRIMARY KEY (ID)
);

CREATE TABLE clients (
    id              INT UNSIGNED NOT NULL AUTO_INCREMENT,
    first_name      VARCHAR(20) NOT NULL,
    last_name       VARCHAR(20),
    father_name     VARCHAR(20),
    passport_series VARCHAR(20),
    passport_num    VARCHAR(20),
    PRIMARY KEY (id)
);

CREATE TABLE journal (
    id        INT UNSIGNED NOT NULL AUTO_INCREMENT,
    book_id   INT UNSIGNED NOT NULL,
    client_id INT UNSIGNED NOT NULL,
    date_beg  TIMESTAMP(6),
    date_end  TIMESTAMP(6),
    date_ret  TIMESTAMP(6),
    FOREIGN KEY (book_id) REFERENCES books (id),
    FOREIGN KEY (client_id) REFERENCES clients (id),
    PRIMARY KEY (id)
);

INSERT INTO library.book_types (name, cnt, fine, day_count) VALUES ('Novel', 50, 10.00, 14);
INSERT INTO library.book_types (name, cnt, fine, day_count) VALUES ('Poem', 10, 1.00, 21);
INSERT INTO library.book_types (name, cnt, fine, day_count) VALUES ('Fairy tale', 20, 5.00,  31);
INSERT INTO library.book_types (name, cnt, fine, day_count) VALUES ('Old', 30, 2.00, 14);
INSERT INTO library.book_types (name, cnt, fine, day_count) VALUES ('Russian', 100, 10.00, 28);
INSERT INTO library.book_types (name, cnt, fine, day_count) VALUES ('Classic', 100, 10.00, 21);

INSERT INTO library.books (name, cnt, type_id) VALUES ('War and Peace' , 100, 1);
INSERT INTO library.books (name, cnt, type_id) VALUES ('Anna Karenina', 50, 1);
INSERT INTO library.books (name, cnt, type_id) VALUES ('Borodin', 10, 2);
INSERT INTO library.books (name, cnt, type_id) VALUES ('Cinderella', 100, 3);
INSERT INTO library.books (name, cnt, type_id) VALUES ('Vinni Puh', 200, 5);

INSERT INTO library.clients (username, password, first_name, last_name, father_name, passport_series, passport_num) VALUES ('client1', '1231q', 'Ivan', 'Ivanov', 'Ivanov', '1234', '56');
INSERT INTO library.clients (username, password, first_name, last_name, father_name, passport_series, passport_num) VALUES ('client2', '1231q', 'Evgen', 'Smirnov', 'Alex', '1234', '56');
INSERT INTO library.clients (username, password, first_name, last_name, father_name, passport_series, passport_num) VALUES ('client3', '1231q', 'Ivan', 'Ivanov', 'Ivanov', '5678', '56');
INSERT INTO library.clients (username, password, first_name, last_name, father_name, passport_series, passport_num) VALUES ('admin'  , 'admin', 'Admin', 'Admin', 'Admin', '0000', '00');
INSERT INTO library.clients (username, password, first_name, last_name, father_name, passport_series, passport_num) VALUES ('client5', '1231q', 'Ivan', 'Petrov', 'Ivanov', '1237', '00');

INSERT INTO library.journal (book_id, client_id, date_beg, date_end) VALUES (1, 1, '2022-03-05 22:43:53.000000','2022-04-05 22:43:53.000000' );
INSERT INTO library.journal (book_id, client_id, date_beg, date_end) VALUES (2, 1, '2022-03-05 22:43:53.000000','2022-04-05 22:43:53.000000' );
INSERT INTO library.journal (book_id, client_id, date_beg, date_end) VALUES (1, 2, '2022-02-05 22:43:53.000000','2022-05-05 22:43:53.000000' );
INSERT INTO library.journal (book_id, client_id, date_beg, date_end) VALUES (1, 3, '2022-03-01 22:43:53.000000','2022-04-03 22:43:53.000000' );
INSERT INTO library.journal (book_id, client_id, date_beg, date_end, date_ret) VALUES (3, 5, '2022-03-05 22:43:53.000000','2022-04-05 22:43:53.000000', '2022-04-01 22:43:53.000000');
