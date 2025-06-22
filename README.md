# Loan Engine V1
## Tech Stack

| Layer           | Technology                 |
|----------------|----------------------------|
| Backend        | Java 17, Spring Boot 3.5.3 |
| Database       | HSQLDB                     |
| ORM            | Spring Data JPA            |
| Build Tool     | Maven                      |
| Dependency Injection | Spring Framework           |
| Testing        | JUnit 5, Mockito           |

---

## Initial Data

### Loan Data
| Loan ID                                 | Description                                                    |
|----------------------------------------|----------------------------------------------------------------|
| 13e96370-f415-4697-b5c5-7765d8a780b9   | Tidak ada repayment dengan status pending hingga 22 Juni 2025  |
| 4c8ba930-2edb-4c62-a3a2-e83f46969d47   | Memiliki 3 repayment dengan status pending hingga 22 Juni 2025 |

### Customer Data
| Customer ID                             | Nama              |
|----------------------------------------|------------------|
| 6518c67c-e842-49f8-b430-bc6252ade609   | Rizky Perdana    |
| a9d3a481-f27c-450c-94d4-f9bfb3e3bd4c   | Michael Jackson  |

### System Config
| Key                     | Keterangan                                     |
|------------------------|------------------------------------------------|
| ANNUAL_INTEREST_RATE    | Annual interest rate in percentage (e.g: 12.5) |
| PRINCIPAL_AMOUNT        | Default principal loan amount for new users    |

## How to Run
1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
4. Access the application at `http://localhost:8083`
5. Access the Swagger UI at `http://localhost:8083/swagger-ui/index.html`
6. Use the API endpoints as described below
7. To run tests, use:
   ```bash
   mvn test
   ```
   
### Testing
Delete `data` folder if you want to reset the data

### API Contract
```json
Path: /loan/v1/make-payment/{loanId}
Method: PUT
Request Body: {
"paidAmount": 3300000
}
Response Body: 
- No Content (200)
- Loan Not Found (404)
- No Repayment Schedule (400)
- Repayment Amount Not Match (400)
```

```json
Path: /loan/v1
Method: POST
Request Body: {
"customer": {
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "fullName": "string",
  "email": "string"
},
"amount": 0
}
Response Body: 
- No Content (200), create new customer when id is null with customizable amount if amount is not null or else will use the system config amount
- Customer Not Found (404) when id is fill
```

```json
Path: /loan/v1/is-delinquent/{loanId}
Method: GET
Response Body: 
- Boolean Result (200)
- Loan Not Found (404)
```

```json
Path: /loan/v1/get-outstanding/{loanId}
Response Body: 
- Numeric Result (200)
- Loan Not Found (400)
```
### ✅ Key Assumptions
| Assumption                     | Description                                                             |
|------------------------|-------------------------------------------------------------------------|
| 📅 Loan starts at creation time | Assumes loan begins the day it's created                                |
| 💰 No partial or early payments	| All payments must be exact and due (not future weeks)                   |
| 🧾 Flat interest	| No amortization or compound interest, just flat 10% per annum           |
| 👥 Single-customer loan	| Each loan is assigned to one Customer, no co-signers or shared loans    |
| 📄 Payment status is binary	| Each repayment is either `PENDING` or `PAID`, with no in-between        |
| 🔄 System-configurable rates	| Principal amount and interest rate can be loaded from DB (SystemConfig) | 