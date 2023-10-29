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
- **Schema:**
```json
{
  "username": "John Doe",
  "password": "*********"
}
```
- **Responses**:
    - Content-Type: `application/json`
- **Schema:** 
```json
{
  "userId": 1,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
  "username": "John Doe"
}
```

### /api/auth/signup

#### POST `/api/auth/signup`

- **Description:** Register a user
- **Request Body:**
  - Content-Type: `application/json`
- **Schema:**
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
  - **Schema**:
```json
{
  "message" : "User registered successfully!"
}
```
### IPAM Endpoints (ADMIN) <a name="IPAM-ADMIN-endpoints"></a>

**HTTP Method:** GET

**Endpoint:** `/api/ipam/users`

**Description:**
This endpoint allows fetching a list of users. To access this endpoint, the user must have the 'SCOPE_ROLE_ADMIN' authority.

**Request Parameters:**
- `page` (Optional, Default: 0) - An integer indicating the page number.
- `size` (Optional, Default: 10) - An integer indicating the page size.

**Response:**
- Content-Type: `application/json`
- Schema:
```json
{
    "totalPages": 0,
    "currentPageSize": 0,
    "maxPageSize": 10,
    "currentPage": 0,
    "hasNext": false,
    "hasPrevoius": false,
    "data": [
              {
                  "id": 1,
                  "username": "name",
                  "email": "john234@gmail.com",
                  "password": "{bycrpt}********",  
              }
            ]
}
```

**Example Usage:**
```http
GET /api/ipam/users?page=1&size=20
```

## Method: addIpAddress

**HTTP Method:** POST

**Endpoint:** `/api/ipam/ipaddresses`

**Description:**
This endpoint allows adding a new IP address. To access this endpoint, the user must have the 'SCOPE_ROLE_ADMIN' authority.

**Request Body:**
- **Content-Type:** `application/json`
- **Schema:**
```json
{
  "address": "192.168.0.1"
}
```
**Response Body:**
- **Content-Type:** `application/json`
- **Schema:**
```json
{
  "id": 1,
  "user": null,
  "status": "AVAILABLE",
  "createdAt": "2023-10-28T14:30:00Z",
  "updatedAt": "2023-10-28T14:30:00Z",
  "expiration": null,
  "dns": null,
  "address": "192.168.0.1"
}
```
## Method: getIpAddresses

**HTTP Method:** GET

**Endpoint:** `/api/ipam/ipaddresses`

**Description:**
This endpoint allows fetching a list of IP addresses. To access this endpoint, the user must have the 'SCOPE_ROLE_ADMIN' authority.

**Request Parameters:**
- `page` (Optional, Default: 0) - An integer indicating the page number.
- `size` (Optional, Default: 10) - An integer indicating the page size.

**Response Body:**
- **Content-Type:** `application/json`.
- **Schema:**
```json
  {
    "totalPages": 0,
    "currentPageSize": 0,
    "maxPageSize": 10,
    "currentPage": 0,
    "hasNext": false,
    "hasPrevious": false,
    "data": [
      {
        "id": 1,
        "user": {
          "id": 1001,
          "username": "John Doe",
          "email": "john234@gmail.com",
          "password": "*********"
        },
        "status": "AVAILABLE",
        "createdAt": "2023-10-28T14:30:00Z",
        "updatedAt": "2023-10-28T14:30:00Z",
        "expiration": null,
        "dns": null,
        "address": "192.168.0.1"
      }
    ]
}    
```
**Example Usage:**
```http
GET /api/ipam/ipaddresses?page=1&size=20
```

**HTTP Method:** GET

**Endpoint:** `/api/ipam/admin/ip-scan`

**Description:**
This endpoint allows administrators to retrieve statistics related to IP addresses. Access to this endpoint requires the 'SCOPE_ROLE_ADMIN' authority.

**Response Body:**
- **Content-Type:** `application/json`
- **Schema:**
```json
{
  "reservedCount": 4,
  "inuseCount": 23,
  "availableCount": 245
}
```
**Example Usage:**
```http
GET /api/ipam/admin/ip-scan
```

## Method: addIPRange

**HTTP Method:** POST

**Endpoint:** `/ipranges`

**Description:**
This endpoint allows administrators to add an IP range. Access to this endpoint requires the 'SCOPE_ROLE_ADMIN' authority.

**Request Body:**
- Content-Type: `application/json`
- Schema:
```json
{
  
}
```
**Response:**
- Content-Type: `application/json`
- Status Code: 201 (Created)
- Schema: [IPRangeDTO](#iprangedto)
