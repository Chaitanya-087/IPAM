# API Documentation

This is the API documentation for IP Address Management, based on the OpenAPI 3.0.1 specification.

## Table of Contents

1. [Introduction](#introduction)
2. [API Description](#description)
3. [Base URL](#base-url)
4. [Authentication & Authorization](#authentication)
5. [Authentication Endpoints](#authentication-endpoints)
6. [IPAM Endpoints (ADMIN)](#admin-endpoints)
7. [IPAM Endpoints (USER)](#user-endpoints)
8. [Common Endpoints](#common-endpoints)

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

### generate token

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

### register a new user

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

## IPAM Endpoints (ADMIN) <a name="admin-endpoints"></a>

### get all users

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

### add a new ip addresses into to ip address pool

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
  "user": null,
  "status": "AVAILABLE",
  "createdAt": "2023-10-29T11:42:17.738Z",
  "updatedAt": "2023-10-29T11:42:17.738Z",
  "expiration": null,
  "dns": "string",
  "address": "string"
}
```

### get all ip addresses (in_use , available or reserved)

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

### get stats of ip addresses

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

### all a new ip range to ip ranges pool

**HTTP Method:** POST

**Endpoint:** `/api/ipam/ipranges`

**Description:**
This endpoint allows administrators to add an IP range. Access to this endpoint requires the 'SCOPE_ROLE_ADMIN' authority.
Also this operation adds ip addresses that are in the new ip range.

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
  "status": "AVAILABLE",
  "size": 0,
  "createdAt": "2023-10-29T11:49:12.467Z",
  "updatedAt": "2023-10-29T11:49:12.467Z",
  "expirationDate": null,
  "user": null
}
```

### get all ip ranges (in_use, available or reversed)

**HTTP Method:** GET

**Endpoint:** `/api/ipam/ipranges`

**Description:**
This endpoint allows administrators with the 'SCOPE_ROLE_ADMIN' authority to retrieve a list of IP ranges.

**Request Parameters:**

- `page` (Optional, Default: 0) - An integer indicating the page number.
- `size` (Optional, Default: 10) - An integer indicating the page size.

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

**Example Usage:**

```http
GET /api/ipam/ipranges?page=1&size=20
```

### get all ip addresses corresponding to particular ip range (available, in_use or reserved)

**HTTP Method:** GET

**Endpoint:** `/api/ipam/ipranges/{ipRangeId}/ipaddresses`

**Description:**
This endpoint retrieves a list of IP addresses associated with a specific IP range. It requires administrator 'SCOPE_ROLE_ADMIN' authorization.

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
  "maxPageSize": 10,
  "totalElements": 0,
  "currentPage": 0,
  "hasNext": true,
  "hasPrevious": true,
  "data": [
    {
    "id": 0,
    "address": "string",
    "status": "RESERVED",
    "dns": "string",
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

**Example Usage:**

```http
GET /api/ipam/ipranges/12345/ipaddresses?page=1&size=20
```

### get ip ranges stat

**HTTP Method:** GET

**Endpoint:** `/api/ipam/admin/iprange-scan`

**Description:**
This endpoint allows administrators to retrieve statistics related to IP Ranges. Access to this endpoint requires the 'SCOPE_ROLE_ADMIN' authority.

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
GET /api/ipam/admin/iprange-scan
```

### add a new subnet to subnet pool

**HTTP Method:** POST

**Endpoint:** `/api/ipam/subnets`

**Description:**
This endpoint allows adding a subnet. To access it, the user must have the 'SCOPE_ROLE_ADMIN' authority. Also this adds ip addresses corresponding to that new subnet

**Request Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "name": "string",
  "cidr": "string",
  "gateway": "string",
  "mask": "string"
}
```

**Response Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "id": 0,
  "user": null,
  "status": "AVAILABLE",
  "createdAt": "2023-10-29T12:10:48.384Z",
  "updatedAt": "2023-10-29T12:10:48.384Z",
  "expiration": null,
  "name": "string",
  "cidr": "string",
  "mask": "string",
  "gateway": "string",
  "size": 0
}
```

### get all subnets

**HTTP Method:** GET

**Endpoint:** `/api/ipam/subnets`

**Description:**
This endpoint retrieves a list of subnets. To access it, the user must have the 'SCOPE_ROLE_ADMIN' authority.

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
  "maxPageSize": 10,
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
      "createdAt": "2023-10-29T12:10:48.384Z",
      "updatedAt": "2023-10-29T12:10:48.384Z",
      "expiration": "2023-10-29T12:10:48.384Z",
      "name": "string",
      "cidr": "string",
      "mask": "string",
      "gateway": "string",
      "size": 0
    }
  ]
}
```

**Example Usage:**

```http
GET /api/ipam/subnets
```

### get subnets stat

**HTTP Method:** GET

**Endpoint:** `/api/ipam/admin/subnet-scan`

**Description:**
This endpoint allows administrators to retrieve statistics related to subnets. Access to this endpoint requires the 'SCOPE_ROLE_ADMIN' authority.

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
GET /api/ipam/admin/subnet-scan
```

### /api/ipam/reserve/network-object/{id}

**HTTP Method:** POST

**Endpoint:** `/api/ipam/reserve/network-object/{id}`

**Description:**
This endpoint allows reserving a network object with a specific ID. To access this endpoint, the user must have the 'SCOPE_ROLE_ADMIN' authority.
the id can be of subnet or ip range or a single ip address, when a subnet or ip range is reserved the corresponding ip-addresses are also reserved

**Request Parameters:**

- `id` (Path Parameter (required)) - A long integer representing the ID of the network object to be reserved.

**Request Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "purpose": "string"
}
```

**Response Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "message": "string"
}
```

### get all current reservations

**HTTP Method:** GET

**Endpoint:** `/api/ipam/reservations`

**Description:**
This endpoint allows administrators to retrieve a list of reservations. To access this endpoint, the user must have the 'SCOPE_ROLE_ADMIN' authority.

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
  "maxPageSize": 10,
  "totalElements": 0,
  "currentPage": 0,
  "hasNext": true,
  "hasPrevious": true,
  "data": [
    {
      "id": 0,
      "purpose": "string",
      "releaseDate": "2023-10-29T11:45:17.228Z",
      "type": "string",
      "identifier": "string"
    }
  ]
}
```

_________________

## IPAM Endpoints (USER) <a name="user-endpoints"></a>

### get all allocated ipaddresses for specific user

**HTTP Method:** GET

**Endpoint:** `/api/ipam/users/{userId}/ipaddresses`

**Description:**
This endpoint allows users with the 'SCOPE_ROLE_USER' authority to retrieve a list of IP addresses associated with a specific user.

**Request Parameters:**

- `userId` (Path Parameter) - A long integer representing the ID of the user whose IP addresses are to be retrieved.
- `page` (Optional, Default: 0) - An integer indicating the page number.
- `size` (Optional, Default: 10) - An integer indicating the page size.

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
        "user": {
        "id": 0,
        "name": "string",
        "email": "string",
        "password": "string",
        "role": "string"
      },
    "status": "IN_USE",
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
GET /api/ipam/users/123/ipaddresses?page=1&size=20
```

### get all available ipaddresses

**HTTP Method:** GET

**Endpoint:** `/api/ipam/ipaddresses/available`

**Description:**
This endpoint allows users with the 'SCOPE_ROLE_USER' authority to retrieve a list of available (unreserved) or (not in_use) IP addresses.

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
      "user": null,
      "status": "AVAILABLE",
      "createdAt": "2023-10-29T11:45:17.228Z",
      "updatedAt": "2023-10-29T11:45:17.228Z",
      "expiration": null,
      "dns": "string",
      "address": "string"
    }
  ]
}    
```

**Example Usage:**

```http
GET /api/ipam/ipaddresses/available?page=1&size=20
```

### allocate ipaddress requested by specific user

**HTTP Method:** POST

**Endpoint:** `/api/ipam/allocate/ipaddresses/{ipAddressId}/users/{userId}`

**Description:**
This endpoint allows users with the 'SCOPE_ROLE_USER' authority to allocate a specific IP address to a user. Once allocated, the IP address will be marked for a certain duration and will be automatically de-allocated after the expiration time.

**Request Parameters:**

- `ipAddressId` (Path Parameter) - A long integer representing the ID of the IP address to be allocated.
- `userId` (Path Parameter) - A long integer representing the ID of the user to whom the IP address will be allocated.

**Response Body:**

- Content-Type: `application/json`.
- Schema

```json
{
  "message": "string"
}
```

### get all ip ranges allocated to specific user

**HTTP Method:** GET

**Endpoint:** `/api/ipam/users/{userId}/ipranges`

**Description:**
This endpoint allows administrators with the 'SCOPE_ROLE_USER' authority to retrieve a list of his IP ranges.

**Request Parameters:**

- `userId` (Path Parameter) - This endpoint allows users with the 'SCOPE_ROLE_USER' authority to get all ipranges that allocated to that user.
- `page` (Optional, Default: 0) - An integer indicating the page number.
- `size` (Optional, Default: 10) - An integer indicating the page size.

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
    "status": "IN_USE",
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

**Example Usage:**

```http
GET /api/ipam/users/123/ipranges?page=1&size=20
```

### get all available ip addresses in an iprange

**HTTP Method:** GET

**Endpoint:** `/api/ipam/ipranges/{ipRangeId}/ipaddresses/available`

**Description:**
This endpoint retrieves a list of available IP addresses associated with a specific IP range. It requires administrator (SCOPE_ROLE_USER) authorization.

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
  "maxPageSize": 10,
  "totalElements": 0,
  "currentPage": 0,
  "hasNext": true,
  "hasPrevious": true,
  "data": [
    {
    "id": 0,
    "address": "string",
    "status": "AVAILABLE",
    "dns": "string",
    "createdAt": "2023-10-29T11:49:12.467Z",
    "updatedAt": "2023-10-29T11:49:12.467Z",
    "expirationDate": null,
    "user": null
    }
  ]
}
```

**Example Usage:**

```http
GET /api/ipam/ipranges/12345/ipaddresses/available?page=1&size=20
```

### get all available ipranges

**HTTP Method:** GET

**Endpoint:** `/api/ipam/ipranges/available`

**Description:**
This endpoint allows users with the 'SCOPE_ROLE_USER' authority to retrieve a list of available (unallocated) IP ranges.

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
    "startAddress": "string",
    "endAddress": "string",
    "status": "AVAILABLE",
    "size": 0,
    "createdAt": "2023-10-29T11:49:12.467Z",
    "updatedAt": "2023-10-29T11:49:12.467Z",
    "expirationDate": null,
    "user": null
    }
  ]
}
```

**Example Usage:**

```http
GET /api/ipam/ipranges/available?page=1&size=10
```

### allocate requested ip range to specific user

**HTTP Method:** POST

**Endpoint:** `/api/ipam/allocate/ipranges/{ipRangeId}/users/{userId}`

**Description:**
This endpoint allows users with the 'SCOPE_ROLE_USER' authority to allocate a specific IP range to a user. Users should be aware that allocated IP ranges are temporary and are subject to expiration and de-allocation based on system policies.this also allocates the ip-address corresponding to range with the same expiry as the iprange's.

**Request Parameters:**

- `ipRangeId` (Path Parameter): A long integer representing the ID of the IP range to be allocated.
- `userId` (Path Parameter): A long integer representing the ID of the user to whom the IP range will be allocated.

**Response Body:

- Content-Type: `application/json`.
- Schema

```json
{
  "message": "string"
}
```

### get all subnets allocated to specific user

**HTTP Method:** GET

**Endpoint:** `/api/ipam/users/{userId}/subnets`

**Description:**
This endpoint allows administrators with the 'SCOPE_ROLE_USER' authority to retrieve a list of his Subnets.

**Request Parameters:**

- `userId` (Path Parameter) - This endpoint allows users with the 'SCOPE_ROLE_USER' authority to get all ipranges that allocated to that user.
- `page` (Optional, Default: 0) - An integer indicating the page number.
- `size` (Optional, Default: 10) - An integer indicating the page size.

**Response Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "totalPages": 0,
  "currentPageSize": 0,
  "maxPageSize": 10,
  "totalElements": 0,
  "currentPage": 0,
  "hasNext": true,
  "hasPrevious": true,
  "data": [
    {
    "id": 0,
    "name": "string",
    "cidr": "string",
    "gateway": "string",
    "mask": "string",
    "status": "IN_USE",
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

**Example Usage:**

```http
GET /api/ipam/users/123/subnets?page=1&size=20
```

### get all available subnets

**HTTP Method:** GET

**Endpoint:** `/api/ipam/subnets/available`

**Description:**
This endpoint allows users with the 'SCOPE_ROLE_USER' authority to retrieve a list of available (unallocated) subnets.

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
    "name": "string",
    "cidr": "string",
    "gateway": "string",
    "mask": "string",
    "status": "AVAILABLE",
    "size": 0,
    "createdAt": "2023-10-29T11:49:12.467Z",
    "updatedAt": "2023-10-29T11:49:12.467Z",
    "expirationDate": null,
    "user": null
    }
  ]
}
```

**Example Usage:**

```http
GET /api/ipam/subnets/available?page=1&size=10
```

### allocate requested subnet to specific user

**HTTP Method:** POST

**Endpoint:** `/api/ipam/allocate/subnets/{subnetId}/users/{userId}`

**Description:**
This endpoint allows users with the 'SCOPE_ROLE_USER' authority to allocate a specific subnet to a user. Users should be aware that allocated subnet are temporary and are subject to expiration and de-allocation based on system policies.

**Request Parameters:**

- `subnetId` (Path Parameter): A long integer representing the ID of the subnet to be allocated.
- `userId` (Path Parameter): A long integer representing the ID of the user to whom the subnet will be allocated.

**Response Body:

- Content-Type: `application/json`.
- Schema

```json
{
  "message": "string"
}
```

_________________

## Common Endpoints <a name="common-endpoints"></a>

### generate domain name for in_use ip address

**Endpoint:** `/api/ipm/ipaddresses/{ipAddressId}/dns`

**Description:**
This endpoint allows assigning a randomly generated DNS (Domain Name System) name to an "in_use" IP address. The assigned DNS names are randomly generated and can only be generated for IP addresses marked as "in_use."

**Request Parameters:**

- `ipAddressId` (Path Parameter): A long integer representing the ID of the "in_use" IP address to which the DNS will be assigned.

**Response Body:**

- Content-Type: `application/json`
- Schema

```json
{
  "message": "string"
}
```
