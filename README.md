# Running the Application

## Prerequisites

Before running the project, ensure the following are installed:

- Java JDK 11
- Apache Maven
- MySQL Server
- Netbeans IDE (optional)

---

## Running from terminal

1. Clone the repository.
    ```bash
    git clone <repository-url>

2. Navigate to the project directory.
    ```bash
    cd <project-folder>

3. Make sure the MySQL server is running

4. Configure the database connection if neccessary.

5. Run the application
    ```bash
    mvn javafx:run

> If you encounter buid issues or want to perform a fresh build, use:
    ```bash
    mvn clean javafx:run

---

## Running with NetBeans

1. Open the project in NetBeans.
2. Wait for Maven tp download the required dependencies.
3. Ensure the MySQL server is running and the database is configured.
4. Press **F6** or click **Run Project**.

> if you encounter build issues, run **Clean and Build Project** before running again.

## Note

This project was built as part of a college course with my team. Thanks for checking it out!
