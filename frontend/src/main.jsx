import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import {AuthProvider} from "./context/AuthContext.jsx";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Root from "./routes/root.jsx";
import ErrorPage from "./routes/error-page.jsx";
import LoginPage from "./routes/login-page.jsx";
import SignUpPage from "./routes/sign-up-page";
import "react-toastify/dist/ReactToastify.css";
import RoleGuard from "./routes/role-gaurd";

const router = createBrowserRouter([
    {
        path: "/",
        element: <Root />,
        errorElement: <ErrorPage />,

        children: [
            {
                path: "/",
                element: <RoleGuard componentName="home" />,
            },
            {
                path: "/ip-ranges",
                element: <RoleGuard componentName="ipRanges" />,
            },
            {
                path: "/subnets",
                element: <RoleGuard componentName="subnets" />,
            },
        ],
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
