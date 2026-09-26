# PostgreSQL Migration Guide

Your project has been upgraded to use **PostgreSQL** with Spring Boot and HikariCP connection pooling!

---

## 1. Setup PostgreSQL Database

Before running the application, make sure PostgreSQL is running and the database is created.

### Using `psql` or pgAdmin:
```sql
CREATE DATABASE library_db;
```

---

## 2. Configure Database Credentials

Open `src/main/resources/application.properties` and verify your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# Automatically applies schema.sql on startup
spring.sql.init.mode=always
```

---

## 3. Database Tables (Auto-Initialized)

When you start the application, Spring Boot will automatically execute `src/main/resources/schema.sql` to create the required tables:
- **`books`** (`id`, `title`, `author`, `isbn`, `published_year`, `available`)
- **`members`** (`id`, `name`, `email`, `phone`)
- **`borrow_records`** (`id`, `book_id`, `member_id`, `borrow_date`, `return_date`)

---

## 4. (Optional) Migrate Existing SQLite Data to PostgreSQL

If you had records in `library.db` that you'd like to import into PostgreSQL:

```sql
-- Sample Seed / Migration SQL
INSERT INTO books (title, author, isbn, published_year, available) VALUES
('Clean Code', 'Robert C. Martin', '978-0132350884', 2008, TRUE),
('The Pragmatic Programmer', 'Andrew Hunt, David Thomas', '978-0201616224', 1999, TRUE),
('Designing Data-Intensive Applications', 'Martin Kleppmann', '978-1449373320', 2017, TRUE);

INSERT INTO members (name, email, phone) VALUES
('Alice Smith', 'alice@example.com', '123-456-7890'),
('Bob Jones', 'bob@example.com', '987-654-3210');
```

---

## 5. Run the Application

Run the Spring Boot application using Maven:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

The application will be accessible at `http://localhost:8090`.
