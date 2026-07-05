# Beginner's Guide to Library Management System

## What is this project?

This is a **REST API** built with **Spring Boot** that manages a library.
It lets you add books, register members, and track who borrowed which book.

Data is stored in a **SQLite** database (a simple file: `library.db`).

---

## How does a Spring Boot REST API work?

Think of it like a restaurant:

```
Client (Postman / Browser)
        |
        |  sends HTTP request  (e.g. GET /api/books)
        v
   CONTROLLER         <-- waiter (takes your order)
        |
        v
    SERVICE           <-- chef (applies business rules)
        |
        v
   REPOSITORY         <-- kitchen (talks to the database)
        |
        v
   SQLite DATABASE    <-- fridge (stores the data)
```

Every request flows **down** this chain, and the response flows **back up**.

---

## Project Structure Explained

```
src/main/java/com/niranjan/
│
├── controller/         ← REST endpoints (URLs the client calls)
│   ├── BookController.java
│   ├── MemberController.java
│   └── BorrowController.java
│
├── service/            ← Business logic (rules of the app)
│   ├── BookService.java
│   ├── MemberService.java
│   └── BorrowService.java
│
├── repository/         ← Database operations (SQL queries)
│   ├── BookRepository.java
│   ├── MemberRepository.java
│   └── BorrowRepository.java
│
├── entity/             ← Data models (what a Book/Member looks like)
│   ├── Book.java
│   ├── Member.java
│   └── BorrowRecord.java
│
├── exception/          ← Error handling
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── GlobalExceptionHandler.java
│
└── library_management_system/
    └── LibraryManagementSystemApplication.java  ← App entry point
```

---

## Layer 1: Entity (Data Models)

Entities are simple Java classes that represent your data.
Think of them as a **form** or **template**.

### Book.java
```java
public class Book {
    private Long id;           // auto-generated unique number (1, 2, 3...)
    private String title;      // e.g. "Clean Code"
    private String author;     // e.g. "Robert Martin"
    private String isbn;       // unique book code e.g. "978-0132350884"
    private int publishedYear; // e.g. 2008
    private boolean available; // true = on shelf, false = borrowed
}
```

### Member.java
```java
public class Member {
    private Long id;
    private String name;   // e.g. "Alice"
    private String email;  // e.g. "alice@example.com"
    private String phone;  // e.g. "9999999999"
}
```

### BorrowRecord.java
```java
public class BorrowRecord {
    private Long id;
    private Long bookId;       // which book
    private Long memberId;     // who borrowed it
    private String borrowDate; // e.g. "2024-06-13"
    private String returnDate; // null = still borrowed, filled = returned
}
```

---

## Layer 2: Repository (Database Layer)

Repositories talk directly to the SQLite database using **JDBC** (plain SQL).

### How JDBC works:
```java
// 1. Connect to the database
Connection conn = DriverManager.getConnection("jdbc:sqlite:library.db");

// 2. Write your SQL query
PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE id = ?");
ps.setLong(1, id);  // replace ? with the actual id

// 3. Execute and read results
ResultSet rs = ps.executeQuery();
while (rs.next()) {
    String title = rs.getString("title"); // read column by name
}
```

### The 5 basic database operations (CRUD):

| Operation | SQL             | What it does            |
|-----------|-----------------|-------------------------|
| Create    | INSERT INTO ... | Add a new row           |
| Read      | SELECT * FROM   | Get rows                |
| Update    | UPDATE ... SET  | Change existing row     |
| Delete    | DELETE FROM ... | Remove a row            |
| Exists    | SELECT 1 FROM   | Check if row exists     |

### Table creation (runs once on startup):
```java
// This SQL runs when the app starts to create the table if it doesn't exist yet
stmt.executeUpdate("""
    CREATE TABLE IF NOT EXISTS books (
        id             INTEGER PRIMARY KEY AUTOINCREMENT,
        title          TEXT NOT NULL,
        author         TEXT NOT NULL,
        isbn           TEXT UNIQUE,
        published_year INTEGER,
        available      INTEGER DEFAULT 1
    )
""");
```

> SQLite uses `INTEGER` for booleans: `1 = true`, `0 = false`

---

## Layer 3: Service (Business Logic)

Services contain the **rules** of your application.

Example from `BorrowService.java` — borrowing a book:
```java
public BorrowRecord borrowBook(Long bookId, Long memberId) {

    // Rule 1: Book must exist
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    // Rule 2: Book must be available (not already borrowed)
    if (!book.isAvailable()) {
        throw new BadRequestException("Book is already borrowed.");
    }

    // Rule 3: Member must exist
    if (!memberRepository.existsById(memberId)) {
        throw new ResourceNotFoundException("Member not found");
    }

    // All rules passed — create the borrow record
    BorrowRecord record = new BorrowRecord();
    record.setBookId(bookId);
    record.setMemberId(memberId);
    record.setBorrowDate(LocalDate.now().toString());

    borrowRepository.insert(record);            // save to DB
    bookRepository.updateAvailability(bookId, false); // mark as unavailable

    return record;
}
```

---

## Layer 4: Controller (API Endpoints)

Controllers are the **entry points** — they define the URLs your API exposes.

### Key Annotations:

| Annotation          | Meaning                                              |
|---------------------|------------------------------------------------------|
| `@RestController`   | This class handles HTTP requests and returns JSON    |
| `@RequestMapping`   | Base URL for all methods in this class               |
| `@GetMapping`       | Handles GET requests (fetch data)                    |
| `@PostMapping`      | Handles POST requests (create data)                  |
| `@PutMapping`       | Handles PUT requests (update data)                   |
| `@DeleteMapping`    | Handles DELETE requests (delete data)                |
| `@PathVariable`     | Reads a value from the URL e.g. `/books/{id}`        |
| `@RequestBody`      | Reads JSON from the request body and maps to object  |

### Example from BookController.java:
```java
@RestController
@RequestMapping("/api/books")   // all URLs start with /api/books
public class BookController {

    @GetMapping                 // GET /api/books
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")        // GET /api/books/1
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping                // POST /api/books
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book created = bookService.addBook(book);
        return ResponseEntity.status(201).body(created);
    }

    @DeleteMapping("/{id}")     // DELETE /api/books/1
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }
}
```

---

## Layer 5: Exception Handling

When something goes wrong, we throw an exception and the
`GlobalExceptionHandler` catches it and returns a clean JSON error.

```java
// In service — throw this if resource doesn't exist
throw new ResourceNotFoundException("Book not found with id: 99");

// GlobalExceptionHandler catches it and returns:
{
  "timestamp": "2024-06-13T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found with id: 99"
}
```

Without this, Spring would return a messy HTML error page.

---

## All API Endpoints

### Books

| Method | URL               | What it does          | Request Body                          |
|--------|-------------------|-----------------------|---------------------------------------|
| GET    | /api/books        | Get all books         | None                                  |
| GET    | /api/books/1      | Get book with id=1    | None                                  |
| POST   | /api/books        | Add a new book        | `{"title":"...","author":"...",...}`  |
| PUT    | /api/books/1      | Update book with id=1 | `{"title":"...","author":"...",...}`  |
| DELETE | /api/books/1      | Delete book with id=1 | None                                  |

### Members

| Method | URL                | What it does           | Request Body                        |
|--------|--------------------|------------------------|-------------------------------------|
| GET    | /api/members       | Get all members        | None                                |
| GET    | /api/members/1     | Get member with id=1   | None                                |
| POST   | /api/members       | Register new member    | `{"name":"...","email":"...",...}`  |
| PUT    | /api/members/1     | Update member          | `{"name":"...","email":"...",...}`  |
| DELETE | /api/members/1     | Delete member          | None                                |

### Borrow / Return

| Method | URL                                  | What it does                     |
|--------|--------------------------------------|----------------------------------|
| GET    | /api/borrow                          | Get full borrow history          |
| POST   | /api/borrow/book/1/member/2          | Member 2 borrows book 1          |
| PUT    | /api/borrow/return/book/1            | Return book 1                    |

---

## How to Run

Open a terminal inside the `library-management-system` folder and run:

```bash
./mvnw spring-boot:run
```

App starts at: `http://localhost:8090`

---

## Test with Postman (Step by Step)

### Step 1 — Add a book
```
POST http://localhost:8090/api/books
Content-Type: application/json

{
  "title": "Clean Code",
  "author": "Robert Martin",
  "isbn": "978-0132350884",
  "publishedYear": 2008
}
```

### Step 2 — Register a member
```
POST http://localhost:8090/api/members
Content-Type: application/json

{
  "name": "Alice",
  "email": "alice@example.com",
  "phone": "9999999999"
}
```

### Step 3 — Borrow the book (book id=1, member id=1)
```
POST http://localhost:8090/api/borrow/book/1/member/1
```

### Step 4 — Check the book is now unavailable
```
GET http://localhost:8090/api/books/1
```
You'll see `"available": false`

### Step 5 — Return the book
```
PUT http://localhost:8090/api/borrow/return/book/1
```

---

## Key Concepts Glossary

| Term              | Meaning                                                               |
|-------------------|-----------------------------------------------------------------------|
| REST API          | A way to communicate over HTTP using JSON                            |
| Endpoint          | A URL that does something (e.g. `/api/books`)                        |
| JSON              | Data format used to send/receive data `{"key": "value"}`             |
| HTTP Methods      | GET=read, POST=create, PUT=update, DELETE=remove                     |
| JDBC              | Java API to run SQL queries against a database                       |
| SQLite            | A file-based database — no separate server needed                    |
| Dependency Injection | Spring automatically creates and connects objects for you         |
| `@Autowired` / Constructor Injection | Spring injects the dependency automatically    |
| `Optional<T>`     | A wrapper that may or may not contain a value (avoids null errors)   |
| `ResponseEntity`  | Wraps your response with an HTTP status code (200, 201, 404, etc.)   |

---

## Common HTTP Status Codes

| Code | Meaning                              |
|------|--------------------------------------|
| 200  | OK — request succeeded               |
| 201  | Created — new resource was created   |
| 204  | No Content — deleted successfully    |
| 400  | Bad Request — invalid input          |
| 404  | Not Found — resource doesn't exist   |
| 500  | Internal Server Error — app crashed  |
