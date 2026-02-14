# 📚 Rubin's Library Management System

A full-stack Library Management application built with **Spring Boot** and **Vaadin**. This project allows librarians to manage books, students, and loans with a modern, responsive UI.

## ✨ Key Features

* **Magic Book Search:** Integrated with **Google Books API**. Enter an ISBN, and the app automatically fetches the Title, Author, and **Cover Image**.
* **Loan Management:** Check books in and out to students. Tracks "Assigned" and "Returned" dates automatically.
* **Secure Authentication:** Complete registration flow with **Email Verification** (SMTP) and **BCrypt** password hashing.
* **Student Management:** Add, edit, and delete student records.
* **Visual Dashboard:** (Coming soon) A dashboard to view library statistics.

## 🛠️ Tech Stack

* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 3.x
* **Frontend:** Vaadin 25 (Flow)
* **Database:** PostgreSQL
* **Tools:** Maven, Lombok, Google Books API

## 🚀 How to Run

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/Sinbad369/library-vaadin.git](https://github.com/Sinbad369/library-vaadin.git)
    ```

2.  **Configure Environment Variables**
    This project uses environment variables for security. You must set these in your IDE or OS before running:
    * `DB_PASSWORD`: Your PostgreSQL password.
    * `MAIL_PASSWORD`: Your Gmail App Password (for email features).

3.  **Database Configuration**
    Ensure your PostgreSQL service is running. The app expects a database named `library_db` running on port `5432`.
    *(URL: `jdbc:postgresql://localhost:5432/library_db`)*

4.  **Run the App**
    Run the `LibraryApplication.java` class. The app will be available at:
    `http://localhost:8080/`

## 📸 Screenshots
### Home Page 
![HomePage View](images/homepage.png)

### Students Management 
![Students View](images/students.png)

### Books Management (Magic Search)
![Books View](images/books.png)
![Books View](images/addBookGoogleAPI.png)


### Loan Management
![Loans View](images/assignBook.png)
![Loans View](images/studentBookAssign.png)


### Secure Registration & Login & Edit Profile
![Sign Up View](images/registration.png)
![Login View](images/login.png)
![Database Bcrypt password](images/bcryptpassword.png)
![Edit Profile](images/editProfile.png)