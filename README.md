# 🚗 Rental Ride – Fleet Management System

A role-based Spring Boot web application to manage vehicle rentals, employee assignments, and customer records. Designed to simplify and automate fleet operations for rental companies.

---

## 🌟 Features

- 👥 Role-Based Access Control (Admin, Employee, Customer)
- 🚘 CRUD Operations on Vehicles
- 👨‍🔧 Manage Employee Records
- 📋 View & Manage Rental Bookings
- 📆 Track Rental Duration & Cost
- 🧾 View Booking History by User
- 🌐 REST API Architecture
- 🗂️ Modular MVC Code Structure

---

## 🛠️ Tech Stack

| Backend         | Frontend       | Database     | Tools              |
|-----------------|----------------|--------------|--------------------|
| Spring Boot     | Thymeleaf      | MySQL / H2   | Spring Data JPA    |
| Spring MVC      | Bootstrap      |              | Maven              |
| REST Controllers| HTML/CSS       |              | Git & GitHub       |

---

## 📂 Project Structure

```

rental-ride/
├── src/
│   ├── main/
│   │   ├── java/com/rentalride/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── RentalRideApplication.java
│   │   └── resources/
│   │       ├── templates/
│   │       ├── static/
│   │       ├── application.properties
├── pom.xml
└── README.md

````

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- MySQL (or H2 for testing)
- Git

### Installation

```bash
git clone https://github.com/UnifoK/RentalRide.git
cd rental-ride
mvn spring-boot:run
````

> ✅ Access the app at: `http://localhost:8080/`

---

## 🧪 Sample Endpoints (if using REST)

| Method | Endpoint            | Description         |
| ------ | ------------------- | ------------------- |
| GET    | `/vehicles`         | List all vehicles   |
| POST   | `/vehicles/add`     | Add a new vehicle   |
| GET    | `/rentals/{userId}` | View rental history |
| POST   | `/rentals/book`     | Book a rental       |

---

## 🧭 Future Enhancements

* ✅ Integrate with Android App via REST API
* ✅ Implement JWT-Based Authentication
* ✅ Add invoice generation (PDF)
* ✅ Admin dashboard with analytics

---

## 📱 Android Integration

This system powers the **Green Gaushala** Android app using Retrofit to interact with Spring Boot REST endpoints hosted on **AWS RDS**.

---

## 🙋‍♂️ Author

Sazid
📧 Sazid413saifi@gmail.com
🔗https://www.linkedin.com/in/sazid-a30601149/


---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
