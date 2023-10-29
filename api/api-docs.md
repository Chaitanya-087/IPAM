# API Documentation

This is the API documentation for Liscence Mangement System, based on the OpenAPI 3.0.1 specification.

## Table of Contents

1. [Introduction](#introduction)
2. [API Description](#description)
3. [Base URL](#base-url)
4. [Authentication & Authorization](#authentication)
5. [Authentication Endpoints](#authentication-endpoints)
6. [IPAM Endpoints (ADMIN)](#IPAM-ADMIN-endpoints)
7. [IPAM Endpoints (USER)](#IPAM-USER-endpoints)

## Introduction <a name="introduction"></a>

This documentation provides detailed information about the API, which allows you to interact with the endpoints. It is based on the OpenAPI 3.0.1 specification and is intended to help understand how to use the available endpoints.

## API Description <a name="description"></a>

The IPAM (IP Address Management) API is designed to manage IP address-related operations within a network infrastructure. It provides functionalities to create, allocate, reserve, and manage IP addresses, subnets, and IP ranges.

## Base URL <a name="base-url"></a>

- Base URL: [http://localhost:8080](http://localhost:8080)
- Description: This is the base URL for accessing the API endpoints.

## Authentication & Authorization <a name="authentication"></a>

To access the API, you must use Bearer Token Authentication. Include a valid JWT (JSON Web Token) in the `Authorization` header of your requests.
the API endpoints are authorized by a `ROLE` which are ADMIN and USER roles. 

## Authentication Endpoints <a name="authentication-endpoints"></a>

### /api/auth/token

#### POST `/api/auth/token`

- **Description**: Generate Token for user
- **Request Body**:
  - Content-Type: `application/json`
  - Schema:
    ```json
    {
     "username": "John Doe",
     "password": "*********"
    }
    ```
- **Responses**:
  - 200: Token generated successfully
    - Content-Type: `application/json`
    - Schema: 
      ```json
      {
      "userId": 1,
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
      "username": "John Doe"
      }
      ```

### /api/auth/signup

#### POST `/api/auth/signup`

- **Description**: Register a user
- **Request Body**:
  - Content-Type: `application/json`
  - Schema:
    ```json
    {
    "username": "John Doe",
    "email": "john234@gmail.com",
    "password": "*******"
    }
    ```
- **Responses**:
  - 200: OK
    - Content-Type: `application/json`
    - Schema:
    ```json
    {
    "message" : "User registered successfully!"
    }
    ```
