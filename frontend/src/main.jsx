import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import {AuthProvider} from "./context/AuthContext.jsx";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Root from "./routes/root.jsx";
import ErrorPage from "./routes/error-page.jsx";
import LoginPage from "./routes/login-page.jsx";
import SignUpPage from "./routes/sign-up-page";
import RequireAuth from "./require-auth";

const router = createBrowserRouter([
    {
        path: "/",
        element: (
            <RequireAuth>
                <Root />
            </RequireAuth>
        ),
        errorElement: <ErrorPage />,
    },
    {
        path: "/login",
        element: <LoginPage />,
    },
    {
        path: "/sign-up",
        element: <SignUpPage />,
    },
]);

ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <AuthProvider>
            <RouterProvider router={router} />
        </AuthProvider>
    </React.StrictMode>
);
