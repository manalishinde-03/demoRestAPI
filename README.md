# Petstore REST API

A Spring Boot implementation of the Swagger Petstore v3 API. Data is held in memory and resets when the application restarts; no database or authentication setup is required.

## Run

From this directory, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The API base URL is `http://localhost:8080/api/v3`.

## Routes

| Group | Routes |
| --- | --- |
| Pet | `POST /pet`, `PUT /pet`, `GET /pet/findByStatus`, `GET /pet/findByTags`, `GET /pet/{petId}`, `POST /pet/{petId}`, `DELETE /pet/{petId}`, `POST /pet/{petId}/uploadImage` |
| Store | `GET /store/inventory`, `POST /store/order`, `GET /store/order/{orderId}`, `DELETE /store/order/{orderId}` |
| User | `POST /user`, `POST /user/createWithList`, `GET /user/login`, `GET /user/logout`, `GET /user/{username}`, `PUT /user/{username}`, `DELETE /user/{username}` |

Requests and responses use the Petstore JSON schemas. Pet image uploads accept multipart form data or an `application/octet-stream` request body.

Run the test suite with `.\mvnw.cmd test`.
