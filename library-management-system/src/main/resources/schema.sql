-- Database schema for PostgreSQL

CREATE TABLE IF NOT EXISTS books (
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    author         VARCHAR(255) NOT NULL,
    isbn           VARCHAR(100) UNIQUE,
    published_year INT,
    available      BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS members (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    email          VARCHAR(255) UNIQUE NOT NULL,
    phone          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS borrow_records (
    id             BIGSERIAL PRIMARY KEY,
    book_id        BIGINT NOT NULL,
    member_id      BIGINT NOT NULL,
    borrow_date    VARCHAR(50) NOT NULL,
    return_date    VARCHAR(50),
    CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);
