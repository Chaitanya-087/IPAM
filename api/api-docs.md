# API Documentation

This is the API documentation for IP Address Management, based on the OpenAPI 3.0.1 specification.

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

_________________

## API Description <a name="description"></a>

The IPAM (IP Address Management) API is designed to manage IP address-related operations within a network infrastructure. It provides functionalities to create, allocate, reserve, and manage IP addresses, subnets, and IP ranges.

_________________

## Base URL <a name="base-url"></a>

- Base URL: [http://localhost:8080](http://localhost:8080)
- Description: This is the base URL for accessing the API endpoints.

_________________

## Authentication & Authorization <a name="authentication"></a>

To access the API, you must use Bearer Token Authentication. Include a valid JWT (JSON Web Token) in the `Authorization` header of your requests.
the API endpoints are authorized by a `ROLE` which are ADMIN and USER roles.
_________________

## Authentication Endpoints <a name="authentication-endpoints"></a>

### /api/auth/token

**HTTP Method:** POST

**Endpoint:** `/api/auth/token`

**Description**: Generate Token for user

**Request Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "username": "string",
  "password": "string"
}
```

**Response Body**:

- Content-Type: `application/json`
- Schema

```json
{
  "id": 0,
  "token": "string",
  "username": "string"
}
```

### /api/auth/signup

**HTTP Method:** POST

**Endpoint:** `/api/auth/signup`

**Description:** Register a user

**Request Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}  
```

**Response Body**:

- Content-Type: `application/json`
- Schema

```json
{
  "message" : "string"
}
```

_________________

## IPAM Endpoints (ADMIN) <a name="IPAM-ADMIN-endpoints"></a>

### /api/ipam/users

**HTTP Method:** GET

**Endpoint:** `/api/ipam/users`

**Description:**
This endpoint allows fetching a list of users. To access this endpoint, the user must have the 'SCOPE_ROLE_ADMIN' authority.

**Request Parameters:**

- `page` (Optional, Default: 0) - An integer indicating the page number.
- `size` (Optional, Default: 10) - An integer indicating the page size.

**Response Body:**

- Content-Type: `application/json`
- Schema

```json
[
  {
    "id": 0,
    "name": "string",
    "email": "string",
    "ipAddressesCount": 0,
    "ipRangesCount": 0,
    "subnetsCount": 0
  }
]
```

**Example Usage:**

```http
GET /api/ipam/users?page=1&size=20
```

### /api/ipam/ipaddresses

**HTTP Method:** POST

**Endpoint:** `/api/ipam/ipaddresses`

**Description:**
This endpoint allows adding a new IP address. To access this endpoint, the user must have the 'SCOPE_ROLE_ADMIN' authority.

**Request Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "address": "string"
}
```

**Response Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "id": 0,
  "user": {
    "id": 0,
    "name": "string",
    "email": "string",
    "password": "string",
    "role": "string"
  },
  "status": "RESERVED",
  "createdAt": "2023-10-29T11:42:17.738Z",
  "updatedAt": "2023-10-29T11:42:17.738Z",
  "expiration": "2023-10-29T11:42:17.738Z",
  "dns": "string",
  "address": "string"
}
```

### /api/ipam/ipaddresses

**HTTP Method:** GET

**Endpoint:** `/api/ipam/ipaddresses`

**Description:**
This endpoint allows fetching a list of IP addresses. To access this endpoint, the user must have the 'SCOPE_ROLE_ADMIN' authority.

**Request Parameters:**

- `page` (Optional, Default: 0) - An integer indicating the page number.
- `size` (Optional, Default: 10) - An integer indicating the page size.

**Response Body:**

- Content-Type: `application/json`.
- Schema

```json
{
  "totalPages": 0,
  "currentPageSize": 0,
  "maxPageSize": 0,
  "totalElements": 0,
  "currentPage": 0,
  "hasNext": true,
  "hasPrevious": true,
  "data": [
    {
        "id": 0,
        "user": {
        "id": 0,
        "name": "string",
        "email": "string",
        "password": "string",
        "role": "string"
      },
    "status": "RESERVED",
    "createdAt": "2023-10-29T11:45:17.228Z",
    "updatedAt": "2023-10-29T11:45:17.228Z",
    "expiration": "2023-10-29T11:45:17.228Z",
    "dns": "string",
    "address": "string"
    }
  ]
}    
```

**Example Usage:**

```http
GET /api/ipam/ipaddresses?page=1&size=20
```

### /api/ipam/admin/ip-scan

**HTTP Method:** GET

**Endpoint:** `/api/ipam/admin/ip-scan`

**Description:**
This endpoint allows administrators to retrieve statistics related to IP addresses. Access to this endpoint requires the 'SCOPE_ROLE_ADMIN' authority.

**Response Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "reservedCount": 0,
  "inuseCount": 0,
  "availableCount": 0
}
```

**Example Usage:**

```http
GET /api/ipam/admin/ip-scan
```

## /api/ipam/ipranges

**HTTP Method:** POST

**Endpoint:** `/api/ipam/ipranges`

**Description:**
This endpoint allows administrators to add an IP range. Access to this endpoint requires the 'SCOPE_ROLE_ADMIN' authority.

**Request Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "startAddress": "string",
  "endAddress": "string"
}
```

**Response Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "id": 0,
  "startAddress": "string",
  "endAddress": "string",
  "status": "RESERVED",
  "size": 0,
  "createdAt": "2023-10-29T11:49:12.467Z",
  "updatedAt": "2023-10-29T11:49:12.467Z",
  "expirationDate": "2023-10-29T11:49:12.467Z",
  "user": {
    "id": 0,
    "name": "string",
    "email": "string",
    "password": "string",
    "role": "string"
  }
}
```

## /api/ipam/ipranges/{ipRangeId}/ipaddresses

**HTTP Method**: GET

**Endpoint:** `api/ipam/ipranges/{ipRangeId}/ipaddresses`

**Description:**
This endpoint retrieves a list of IP addresses associated with a specific IP range. It requires administrator (ROLE_ADMIN) authorization.

**Request Parameters:**

- `ipRangeId` (Path Parameter, Required) - The unique identifier of the IP range for which you want to retrieve IP addresses.
- `page` (Query Parameter, Optional, Default: 0) - The page number for pagination.
- `size` (Query Parameter, Optional, Default: 10) - The number of IP addresses to be displayed per page.

**Response Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "totalPages": 0,
  "currentPageSize": 0,
  "maxPageSize": 0,
  "totalElements": 0,
  "currentPage": 0,
  "hasNext": true,
  "hasPrevious": true,
  "data": [
    {
    "id": 0,
    "startAddress": "string",
    "endAddress": "string",
    "status": "RESERVED",
    "size": 0,
    "createdAt": "2023-10-29T11:49:12.467Z",
    "updatedAt": "2023-10-29T11:49:12.467Z",
    "expirationDate": "2023-10-29T11:49:12.467Z",
    "user": {
      "id": 0,
      "name": "string",
      "email": "string",
      "password": "string",
      "role": "string"
      }
    }
  ]
}
```

### Example Usage

```http
GET /api/ipam/ipranges/12345/ipaddresses?page=1&size=20
```
