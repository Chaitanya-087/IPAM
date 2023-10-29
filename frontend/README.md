# React + Vite
Project is created using vite (instant dev server) and react plugin 

## Table of Content
- [Introduction](#introduction)
- [Structure](#structure)
- [Component analysis](#component-analysis)

## Introduction

IP address management application use IPAM api built using java spring boot to power up the functionalities of ipam that is allocating ip-addresses, ip-ranges, subnets and reserve them which is operation done by an admin user and requesting these network objects is done by users and also admins looking up the stats 
of ip's , ranges, subnets for availability and other necessary statuses.

## Structure

```
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

## Component analysis

`main.jsx` - root of the project contains all the neccessary routes in the application and also provides authcontext to entire app`
