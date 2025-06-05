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

To clone the Pickup Scheduling Module repository and set it up on your local machine, follow the steps below:

### **1. Open a Terminal**
   Open a terminal or command prompt on your computer where Git is installed.

### **2. Run the Git Clone Command**
   Use the following command to clone the repository to your local machine:
   ```bash
   git clone https://github.com/Atharva-K03/pickup-scheduling.git
   ```

### **3. Navigate to the Cloned Directory**
   After cloning, move into the project directory by running:
   ```bash
   cd pickup-scheduling
   ```

### **4. Verify the Repository**
   Ensure the repository is cloned correctly by listing the files in the directory:
   - **Mac/Linux**:
     ```bash
     ls
     ```
   - **Windows**:
     ```bash
     dir
     ```

   You should see the project files, including:
   ```
   src/
   pom.xml
   README.md
   ```

---

Once you’ve done this, you can proceed to follow the [Installation](#installation) and [Database Configuration](#database-configuration) sections to set up and run the project locally.

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

You can find the database schema in the `db/schema.sql` file.

---

### **2. Set Up the Database**

1. **Create a Database**:
   Open your database client and create a new database:
   ```sql
   CREATE DATABASE pickupdb;
   ```

2. **Run the Schema Script**:
   Execute the script located at `db/schema.sql` to create all necessary tables:
   ```sql
   SOURCE /path/to/db/schema.sql;
   ```

3. **Update Database Credentials**:
   Update the database configurations in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/pickupdb
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

## **API Endpoints**

The following REST API endpoints are available in the Pickup Scheduling Module. These endpoints allow you to create, delete, retrieve, and list pickups.

---

### **Base URL**
All endpoints are prefixed with:
http://localhost:8081/wastewise/scheduler/pickups
---

### **Endpoints**

#### **1. Create a New Pickup**
**Description**: Creates a new pickup resource.  
**Method**: `POST`  
**URL**:
/wastewise/scheduler/pickups
**Request Body**:
```json
{
  "field1": "value1",
  "field2": "value2"
}
```
*(Replace `field1` and `field2` with the actual fields in the `CreatePickUpDto`.)*

**Response**:
- **201 Created**: Returns the ID of the newly created pickup.  
  Example Response:
  ```json
  "new_pickup_id"
  ```

---

#### **2. Delete a Pickup**
**Description**: Deletes an existing pickup by its ID.  
**Method**: `DELETE`  
**URL**:
/wastewise/scheduler/pickups/{id}

**Path Parameter**:
- `id` (String): The ID of the pickup to delete.

**Response**:
- **204 No Content**: Indicates successful deletion.

---

#### **3. List All Pickups**
**Description**: Retrieves a list of all existing pickups.  
**Method**: `GET`  
**URL**:
/wastewise/scheduler/pickups
**Response**:
- **200 OK**: Returns a list of pickups.  
  Example Response:
  ```json
  [
    {
      "id": "pickup_id_1",
      "field": "value"
    },
    {
      "id": "pickup_id_2",
      "field": "value"
    }
  ]
  ```

---

#### **4. Get Pickup by ID**
**Description**: Retrieves a specific pickup by its ID.  
**Method**: `GET`  
**URL**:
/wastewise/scheduler/pickups/{id}
**Path Parameter**:
- `id` (String): The ID of the pickup to retrieve.

**Response**:
- **200 OK**: Returns the pickup details.  
  Example Response:
  ```json
  {
    "id": "pickup_id",
    "field": "value"
  }
  ```

---

### **HTTP Status Codes**
These endpoints use the following HTTP status codes:
- **200 OK**: The request was successful.
- **201 Created**: A new resource was successfully created.
- **204 No Content**: The resource was successfully deleted.
- **400 Bad Request**: The request was invalid (e.g., invalid data or missing fields).
- **404 Not Found**: The requested resource could not be found.
- **500 Internal Server Error**: An unexpected error occurred on the server.

---

### **Examples**

1. **Create a New Pickup**
   ```bash
   curl -X POST http://localhost:8080/wastewise/scheduler/pickups \
   -H "Content-Type: application/json" \
   -d '{
         "field1": "value1",
         "field2": "value2"
       }'
   ```
   Response:
   ```json
   "new_pickup_id"
   ```

2. **Delete a Pickup**
   ```bash
   curl -X DELETE http://localhost:8081/wastewise/scheduler/pickups/{id}
   ```

3. **List All Pickups**
   ```bash
   curl -X GET http://localhost:8081/wastewise/scheduler/pickups
   ```
   Response:
   ```json
   [
     {
       "id": "pickup_id_1",
       "field": "value"
     },
     {
       "id": "pickup_id_2",
       "field": "value"
     }
   ]
   ```

4. **Get Pickup by ID**
   ```bash
   curl -X GET http://localhost:8081/wastewise/scheduler/pickups/{id}
   ```
   Response:
   ```json
   {
     "id": "pickup_id",
     "field": "value"
   }
   ```

---

### **Notes**
- Replace `{id}` in the URL with the actual ID of the pickup you want to access.
- Ensure the application is running locally or on the specified host/port before accessing these endpoints.
- For detailed API testing, tools like **Postman**, **cURL**, or **Swagger** can be used.