# Pickup Scheduling Module

The **Pickup Scheduling Module** is designed to manage and schedule pickups efficiently. This document provides an overview of the project, including how to clone the repository, set up the database, and get started with the project.

---

## **Features**
- Schedule pickups for customers based on preferences.
- Manage pickup locations, timings, and customer information.
- Integration with a database for storing and retrieving scheduling data.
- APIs to interact with the module for seamless integration with other systems.

---

## **Table of Contents**
1. [Getting Started](#getting-started)
2. [Prerequisites](#prerequisites)
3. [Installation](#installation)
4. [Database Configuration](#database-configuration)
5. [Usage](#usage)
6. [Project Structure](#project-structure)
7. [Contributing](#contributing)
8. [License](#license)

---

## **Getting Started**
To get a working copy of the project for development or testing, follow these instructions.

### **1. Clone the Repository**
```bash
git clone <REMOTE_URL>
```
Replace `<REMOTE_URL>` with the URL of the GitHub (or other) repository.

### **2. Navigate to the Directory**
```bash
cd pickup-scheduling-module
```

---

## **Prerequisites**

Ensure you have the following installed on your system:
- **Java 21** (as the project uses Java SDK 21)
- **Maven** (to manage project dependencies)
- **MySQL Database** (or another DBMS if configured differently)
- A modern IDE like IntelliJ IDEA for development.

---

## **Installation**

### **Run the Application**
1. Build the project using Maven:
   ```bash
   mvn clean install
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

---

## **Database Configuration**

### **1. Database Overview**
The Pickup Scheduling Module uses the following database tables:

| **Table Name**        | **Description**                                   |
|-----------------------|---------------------------------------------------|
| `customers`           | Stores customer details, including name, contact, and preferences. |
| `schedules`           | Stores details of scheduled pickups (date, time, location, customer ID). |
| `locations`           | Stores pickup locations and their metadata.       |
| `users`               | Stores authenticated users for the module.        |

You can find the database schema in the `db/schema.sql` file.

---

### **2. Set Up the Database**

1. **Create a Database**:
   Open your database client and create a new database:
   ```sql
   CREATE DATABASE pickup_scheduling;
   ```

2. **Run the Schema Script**:
   Execute the script located at `db/schema.sql` to create all necessary tables:
   ```sql
   SOURCE /path/to/db/schema.sql;
   ```

3. **Update Database Credentials**:
   Update the database configurations in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/pickup_scheduling
   spring.datasource.username=<YOUR_USERNAME>
   spring.datasource.password=<YOUR_PASSWORD>
   spring.jpa.hibernate.ddl-auto=update
   ```

---

### **3. Seed Test Data**
Optionally, you can populate the database with initial data using the `db/data.sql` file:
```sql
SOURCE /path/to/db/data.sql;
```

---

## **Usage**

### **Run the Application**
Once the application is running, you can access the APIs via the following base URL:
