# React + Vite

Project is created using vite (instant dev server) and react plugin

## Table of Content

- [Introduction](#introduction)
- [Base Url](#base-url)
- [Structure](#structure)
- [Routes](#routes)
- [Component analysis](#component-analysis)
- [Important Packages](#important-packages)

## Introduction

IP address management application use IPAM api built using java spring boot to power up the functionalities of ipam that is allocating ip-addresses, ip-ranges, subnets and reserve them which is operation done by an admin user and requesting these network objects is done by users and also admins looking up the stats
of ip's , ranges, subnets for availability and other necessary statuses.

## Base Url

In development environment application run on port 9090 that makes the url to be `http://localhost:9090`

## Structure

```
/src
|   App.css
|   index.css
|   main.jsx
|   
+---api
|       axios.js
|
+---assets
|       react.svg
|
+---components
|       Drawer.jsx
|       SharedLayout.jsx
|       Table.jsx
|
+---context
|       AuthContext.jsx
|
+---hooks
|       useAuth.jsx
|       useAxiosPrivate.jsx
|
+---routes
|   |   error-page.jsx
|   |   login-page.jsx
|   |   require-auth.jsx
|   |   role-gaurd.jsx
|   |   root.jsx
|   |   sign-up-page.jsx
|   |
|   +---admin
|   |   |   IpAddressesTable.jsx
|   |   |   ReservationTable.jsx
|   |   |   Stats.jsx
|   |   |   UsersTable.jsx
|   |   |
|   |   \---pages
|   |           home.jsx
|   |           ipRanges.jsx
|   |           subnets.jsx
|   |
|   \---user
|       \---pages
|               home.jsx
|               ipRanges.jsx
|               subnets.jsx
|
+---styles
       auth.css
       drawer.css
       stats.css

```

## Routes

`/` - base route (renders a shared layout at path `/src/components/SharedLayout` that is responsible to render IPAddressesTable and other necessary tabs)

- `/ip-ranges` (renders IPRanges component for both admin and user view using **dynamic imports**)
- `/subnets` (renders Subnets component for both admin and user view using **dynamic imports**)

`/login` - login page route
`/sign-up` - sign up page route

## Component analysis

`main.jsx` - root of the project contains all the necessary routes in the application and also provides auth-context to entire app

`api/axios.jsx` - This module configures Axios, a popular HTTP client for making network requests, for use within a JavaScript or TypeScript application. It defines a base URL and creates two Axios instances: `instance` and `axiosPrivate``. These instances can be used for making HTTP requests to the specified base URL.  

`context/AuthContext.jsx` - responsible for storing in the auth state - (username, token) and role and provide it to entire app and can persist the changes made to auth state in local storage by a successful login operation from **login.jsx** and **signup.jsx** file responsible to register new user through api

`hooks/` - custom hooks that are important to perform in generalize certain functionality

- `useAuth` - custom hook is designed to provide access to the authentication context within a React application. It allows components to access the user's authentication status and user information stored in the `AuthContext`.

- `useAxiosPrivate` - custom hook is designed to provide a pre-configured Axios instance (`axiosPrivate``) for making private HTTP requests within a React application. It includes logic to handle authentication and authorization, ensuring that requests are made with the appropriate headers and aborting requests if the user is not authenticated.

`routes/${role}/pages/${page}` - are dynamic routes import on demand with respect to role of the user this is decided by `RoleGuard` component and this is used in `CreateBrowserRouter` to perform this base client side routing

`routes/require-auth.jsx` - is a watch dog for a protected component that redirects to login page when ever the token expired are does not exist.

## Important Packages

```json
{
       "@emotion/react": "^11.11.1",
       "@emotion/styled": "^11.11.0",
       "@mui/icons-material": "^5.14.13",
       "@mui/joy": "^5.0.0-beta.11",
       "@mui/material": "^5.14.13",
       "react-router-dom": "^6.17.0",
       "react-spinners": "^0.13.8",
       "react-toastify": "^9.1.3",
       "axios": "^1.5.1",
}
```
