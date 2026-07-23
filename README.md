# Barbershop Management System

A JavaFX desktop application for managing barbershop appointments, services, and staff. Built with Java 11, Maven, and MySQL.

## Features

- **Role-based Access:** Admin, Barber, and Customer roles with different dashboards
- **Appointment Booking:** Customers can book appointments with barbers for specific services
- **Service Management:** Admins can create, update, and delete barbershop services with pricing and duration
- **Barber Management:** Admins can manage barber profiles and availability
- **Password Security:** Passwords hashed using BCrypt
- **Walk-in Support:** Barbers can record walk-in appointments without customer accounts
- **Real-time Updates:** Interactive UI built with JavaFX

## Tech Stack

- **Language:** Java 11
- **UI Framework:** JavaFX 13
- **Database:** MySQL 8.0
- **Build Tool:** Maven
- **Additional Libraries:**
  - BCrypt (password hashing)
  - MySQL Connector/J (database driver)
  - java-dotenv (environment configuration)

## Prerequisites

Before running the project, ensure the following are installed:

- Java JDK 11
- Apache Maven
- MySQL Server
- NetBeans IDE (optional)

## Database Setup

The application automatically initializes the database schema on first run. Default credentials:

- **Admin Email:** `admin@barbershop.com`
- **Admin Password:** `admin123` (change in `src/main/java/com/barbershop/app/util/DBInitializer.java`)
- **Database:** `barbershop_db`
- **MySQL User:** `root`
- **MySQL Password:** `barbershop_password`

Configure these credentials in `src/main/java/com/barbershop/app/util/DBConnection.java` if needed.

## Running the Application

### From Terminal

1. Clone the repository:
   ```bash
   git clone https://github.com/Beriamir/barbershop-java.git
   cd barbershop-java
   ```

2. Ensure MySQL server is running

3. Run the application:
   ```bash
   mvn javafx:run
   ```

4. For a fresh build:
   ```bash
   mvn clean javafx:run
   ```

### From NetBeans IDE

1. Open the project in NetBeans
2. Wait for Maven to download dependencies
3. Ensure MySQL server is running and configured
4. Press **F6** or click **Run Project**
5. If you encounter build issues, run **Clean and Build Project** first

## Project Structure

```
src/main/java/com/barbershop/app/
├── Main.java                 Application entry point
├── controller/              Controllers for each scene (Login, Dashboard, etc.)
├── model/                   Entity models (User, Appointment, Barber, Service)
├── dao/                     Data Access Objects for database operations
├── service/                 Business logic layer
├── util/                    Utilities (DBConnection, SceneManager, etc.)
└── fxml/                    JavaFX FXML UI layouts
resources/
└── schema.sql              Database schema initialization
```

## License

MIT License - see LICENSE file for details

## Note

This project was built as part of a college course with a team. Thanks for checking it out!
