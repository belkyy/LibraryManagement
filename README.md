# LibraryManagement

Library Management System (Java + MySQL)

A console-based Library Management System developed using Java, JDBC, and MySQL.
The system supports user authentication, book management, borrowing/returning books, late fee calculation, and JUnit testing.

Features;

👤 User System
- Sign up & login (passwords hashed with SHA-256)
Roles:
-ADMIN
-USER
--Student / Non-student distinction (affects late fees)

📖 Book Management
-Add / remove books (ADMIN)
-Search books by title or author
-View available books
-Books are uniquely identified by 5-digit IDs

🔁 Borrow & Return
-Borrow books by book ID
-Return books
-One book can be borrowed by only one user at a time
-Borrow duration limit: 10 days (240 hours)

💰 Late Fee System
-Late fee applies after 10 days
-Fee calculation:
-Students: 3 TL per late hour
-Non-students: 5 TL per late hour
-Users must pay outstanding fees before returning books

🛠 Admin Panel
-View all users
-View borrowed books with time & late fees
-Remove users (except admins)
-Remove books (only if not borrowed)

🧪 Testing
JUnit 5 is used for unit testing
DAO-based tests:
-BookDAOTest
-LoanDAOTest
-UserDAOTest

Database-dependent tests use fixed test data (e.g. book ID = 10000)

🗄 Database
MySQL database: Library
Main tables;
-users
-books
-loans
-fees


👨‍💻 Author
Developed by Mert Muhammed Çapkın
-ISTANBUL AREL UNIVERSITY (Computer Engineering)
Course project – Library Management System
