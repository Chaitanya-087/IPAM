# CheckList
- [ ] [BackEnd Docs](#task-1)
- [ ] [FrontEnd Docs](#task-2)
- [X] [Database Layer](https://github.com/Chaitanya-087/IPAM/blob/main/api/ipam_ER_Diagram.png)
- [X] [Test Report](https://github.com/Chaitanya-087/IPAM/blob/main/api/backend_test_coverage_report.png)

# Requirements
## IPAM - IP Address Management System

## Table of Contents
- [1. Introduction](#1-introduction)
  - [1.1 Purpose](#11-purpose)
  - [1.2 Scope](#12-scope)
  - [1.3 References](#14-references)

- [2. Functional Requirements](#2-functional-requirements)
  - [2.1 Tracking Individual IP Addresses](#21-tracking-individual-ip-addresses)
  - [2.2 Managing IP Address Ranges](#22-managing-ip-address-ranges)
  - [2.3 Reserving IP Addresses](#23-reserving-ip-addresses)
  - [2.4 Freeing Reserved IP Addresses](#24-freeing-reserved-ip-addresses)

- [3. Non-Functional Requirements](#3-non-functional-requirements)
  - [3.1 Technology Stack](#31-technology-stack)
  - [3.2 User Interface](#32-user-interface)
  - [3.3 Swagger Documentation](#33-swagger-documentation)

- [4. System Architecture](#4-system-architecture)

- [5. User Interface](#5-user-interface)

- [6. Data Models](#6-data-models)

- [7. Business Logic](#7-business-logic)

- [8. Security](#8-security)

## 1. Introduction

### 1.1 Purpose
The purpose of the IPAM (IP Address Management) project is to develop a Java application for tracking and managing IP addresses, including both individual addresses and address ranges. The system aims to efficiently allocate, reserve, and free up IP addresses while providing a user-friendly interface.

### 1.2 Scope
The scope of this project includes the development of a Java application using Java 17 and Spring Boot 3 for the backend, and a React-based user interface for the frontend. The application will allow users to manage individual IP addresses and IP address ranges, track their status (assigned, free, reserved), and enforce business logic for freeing up reserved addresses.

### 1.3 References
- [What is IPAM? | IP Address Management Solutions - ManageEngine OpManager](https://www.manageengine.com/products/oputils/what-is-ipam.html)

## 2. Functional Requirements

### 2.1 Tracking Individual IP Addresses
- The system should maintain a database table to track individual IP addresses.
- Each IP address should have a status, which can be "assigned," "free," or "reserved."

### 2.2 Managing IP Address Ranges
- The system should support the management of IP address ranges.
- Users should be able to reserve a range of IP addresses if needed.

### 2.3 Reserving IP Addresses
- Users should be able to reserve individual IP addresses.
- Reserved IP addresses should be automatically freed up based on predefined business logic.

### 2.4 Freeing Reserved IP Addresses
- The system should automatically free up reserved IP addresses based on business logic.
- Reserved IP addresses should not be reserved indefinitely.

## 3. Non-Functional Requirements

### 3.1 Technology Stack
- Backend: Java 17, Spring Boot 3
- Frontend: React, Material UI 
- Database: MySQL
- Testing: Mockito, selenium

### 3.2 User Interface
- The system must provide a user-friendly React-based UI for interacting with IP address management functionalities.

### 3.3 Swagger Documentation
- The system should include Swagger documentation for API endpoints.

# OpenAPI definition

This is an OpenAPI (v3.0.1) specification for an API. It defines various paths and components used by the API.

## Servers

- **Generated server URL:** `[http://localhost:8080](http://localhost:8080)`

## Security

- **Bearer Authentication**: This API uses Bearer Authentication for security.

## Paths

### /api/ipam/subnets

#### GET `/api/ipam/subnets`

- **Description**: Get all Subnet Data Transfer Objects
- **Parameters**:
  - `page` (optional, integer, default: 0)
  - `size` (optional, integer, default: 10)
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `PageResponseSubnet`

#### POST `/api/ipam/subnets`

- **Description**: Add a Subnet
- **Request Body**:
  - Content-Type: `application/json`
  - Schema: `SubnetForm`
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `SubnetDTO`

### /api/ipam/reserve/network-object/{id}

#### POST `/api/ipam/reserve/network-object/{id}`

- **Description**: Reserve a network object by ID
- **Parameters**:
  - `id` (required, integer)
- **Request Body**:
  - Content-Type: `application/json`
  - Schema: `Reservation`
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `MessageResponse`

### /api/ipam/ipranges

#### GET `/api/ipam/ipranges`

- **Description**: Get all IP Range Data Transfer Objects
- **Parameters**:
  - `page` (optional, integer, default: 0)
  - `size` (optional, integer, default: 10)
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `PageResponseIPRange`

#### POST `/api/ipam/ipranges`

- **Description**: Add an IP Range
- **Request Body**:
  - Content-Type: `application/json`
  - Schema: `IPRangeForm`
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `IPRangeDTO`

### /api/ipam/ipaddresses

#### GET `/api/ipam/ipaddresses`

- **Description**: Get IP Addresses
- **Parameters**:
  - `page` (optional, integer, default: 0)
  - `size` (optional, integer, default: 10)
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `PageResponseIPAddress`

#### POST `/api/ipam/ipaddresses`

- **Description**: Add an IP Address
- **Request Body**:
  - Content-Type: `application/json`
  - Schema: `IPAddressForm`
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `IPAddress`

### /api/ipam/ipaddresses/{ipAddressId}/dns

#### POST `/api/ipam/ipaddresses/{ipAddressId}/dns`

- **Description**: Assign DNS to an IP Address
- **Parameters**:
  - `ipAddressId` (required, integer)
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `MessageResponse`

### /api/ipam/allocate/subnets/{subnetId}/users/{userId}

#### POST `/api/ipam/allocate/subnets/{subnetId}/users/{userId}`

- **Description**: Allocate a subnet to a user
- **Parameters**:
  - `subnetId` (required, integer)
  - `userId` (required, integer)
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `MessageResponse`

### /api/ipam/allocate/ipranges/{ipRangeId}/users/{userId}

#### POST `/api/ipam/allocate/ipranges/{ipRangeId}/users/{userId}`

- **Description**: Allocate an IP range to a user
- **Parameters**:
  - `ipRangeId` (required, integer)
  - `userId` (required, integer)
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `MessageResponse`

### /api/ipam/allocate/ipaddresses/{ipAddressId}/users/{userId}

#### POST `/api/ipam/allocate/ipaddresses/{ipAddressId}/users/{userId}`

- **Description**: Allocate an IP address to a user
- **Parameters**:
  - `ipAddressId` (required, integer)
  - `userId` (required, integer)
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `MessageResponse`

### /api/auth/token

#### POST `/api/auth/token`

- **Description**: Generate Token for user
- **Request Body**:
  - Content-Type: `application/json`
  - Schema: `LoginBody`
- **Responses**:
  - 200: Token generated successfully
    - Content-Type: `application/json`
    - Schema: `JwtResponse`
  - 400: Invalid username/password supplied
    - Content-Type: `*/*`
    - Schema: `JwtResponse`
  - 401: Unauthorized
    - Content-Type: `*/*`
    - Schema: `JwtResponse`
  - 404: User not found
    - Content-Type: `*/*`
    - Schema: `JwtResponse`

### /api/auth/signup

#### POST `/api/auth/signup`

- **Description**: Register a user
- **Request Body**:
  - Content-Type: `application/json`
  - Schema: `SignupBody`
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: `MessageResponse`

### /api/ipam

#### GET `/api/ipam`

- **Description**: Hello
- **Responses**:
  - 200: OK
    - Content-Type: `*/*`
    - Schema: string

### /api/ipam/users

#### GET `/api/ipam/users`

- **Description**: Get all users
- **Parameters**:
  - `page` (optional, integer, default: 0)
  - `size` (optional, integer, default: 10)
- **Responses**:
  - 200: All users
    - Content-Type: `application/json`
    - Schema: `PageResponseUser`
  - 401: Unauthorized
    - Content-Type: `*/*`
    - Exception: UnauthorizedException
  - 403: Forbidden
    - Content-Type: `*/*`
    - Schema: `PageResponseUser`

### /api/ipam

