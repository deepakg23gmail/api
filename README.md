# Product Management API

A production-grade Spring Boot REST API for managing products with full CRUD operations.

## Tech Stack

- Java 25
- Spring Boot 3.4.7
- Spring Data JPA
- H2 Database (in-memory)
- Jakarta Bean Validation
- SpringDoc OpenAPI 2.8.6
- JUnit 5 + Mockito
- Maven

## Project Structure

```
src/main/java/com/example/productapi
├── ProductApiApplication.java
├── config/
│   ├── OpenApiConfig.java
│   ├── DataSeeder.java
│   └── WebConfig.java
├── controller/
│   └── ProductController.java
├── dto/
│   ├── ApiResponse.java
│   ├── ApiError.java
│   ├── ProductRequest.java
│   └── ProductResponse.java
├── entity/
│   └── Product.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/
│   └── ProductRepository.java
└── service/
    ├── ProductService.java
    └── impl/
        └── ProductServiceImpl.java
```

## Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Run tests only
mvn test

# Run integration tests
mvn verify
```

The application starts on port **8080** by default.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/products` | Create a new product |
| GET | `/api/v1/products` | Get all products |
| GET | `/api/v1/products/{id}` | Get product by ID |
| PUT | `/api/v1/products/{id}` | Update a product |
| DELETE | `/api/v1/products/{id}` | Delete a product |

### Query Parameters for GET All

- `?category=Electronics` - Filter by category
- `?search=laptop` - Search by name/description

## Example Requests

### Create Product

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Earbuds",
    "description": "Noise-cancelling Bluetooth earbuds with 30hr battery",
    "price": 199.99,
    "quantity": 200,
    "category": "Electronics"
  }'
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Product created successfully",
  "data": {
    "id": 6,
    "name": "Wireless Earbuds",
    "description": "Noise-cancelling Bluetooth earbuds with 30hr battery",
    "price": 199.99,
    "quantity": 200,
    "category": "Electronics",
    "active": true,
    "createdAt": "2026-08-17T10:30:00",
    "updatedAt": "2026-08-17T10:30:00"
  },
  "timestamp": "2026-08-17T10:30:00"
}
```

### Get Product by ID

```bash
curl http://localhost:8080/api/v1/products/1
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "Laptop Pro 16",
    "description": "High-performance laptop with 16-inch Retina display, 32GB RAM, and 1TB SSD",
    "price": 1999.99,
    "quantity": 25,
    "category": "Electronics",
    "active": true,
    "createdAt": "2026-08-17T10:00:00",
    "updatedAt": "2026-08-17T10:00:00"
  },
  "timestamp": "2026-08-17T10:00:00"
}
```

### Get All Products

```bash
curl http://localhost:8080/api/v1/products
```

### Filter by Category

```bash
curl "http://localhost:8080/api/v1/products?category=Electronics"
```

### Search Products

```bash
curl "http://localhost:8080/api/v1/products?search=Laptop"
```

### Update Product

```bash
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Pro 16 - Updated",
    "description": "Updated description",
    "price": 1799.99,
    "quantity": 20,
    "category": "Electronics"
  }'
```

### Delete Product

```bash
curl -X DELETE http://localhost:8080/api/v1/products/1
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Product deleted successfully",
  "data": null,
  "timestamp": "2026-08-17T10:30:00"
}
```

### Validation Error Example

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "price": -5,
    "quantity": -1,
    "category": ""
  }'
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed. See field errors for details.",
  "fieldErrors": [
    { "field": "name", "rejectedValue": "", "message": "Product name is required" },
    { "field": "price", "rejectedValue": -5, "message": "Price must be greater than 0" },
    { "field": "quantity", "rejectedValue": -1, "message": "Quantity must be non-negative" },
    { "field": "category", "rejectedValue": "", "message": "Category is required" }
  ],
  "timestamp": "2026-08-17T10:30:00"
}
```

## Documentation

Once the application is running, access the Swagger UI at:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI Docs:** http://localhost:8080/api-docs

## H2 Console

Access the H2 database console at http://localhost:8080/h2-console:

- JDBC URL: `jdbc:h2:mem:productdb`
- Username: `sa`
- Password: *(empty)*

## Sample Data

The application seeds 5 sample products on startup:

| ID | Name | Price | Category |
|----|------|-------|----------|
| 1 | Laptop Pro 16 | 1999.99 | Electronics |
| 2 | Wireless Mouse | 49.99 | Accessories |
| 3 | USB-C Hub | 79.99 | Accessories |
| 4 | Mechanical Keyboard | 149.99 | Accessories |
| 5 | 4K Monitor 27" | 599.99 | Electronics |
