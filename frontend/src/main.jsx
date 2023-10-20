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
import IPRangesTable from "./routes/ip-ranges";
import SubnetsTable from "./routes/subnet";
import IPAddressesTable from "./routes/ip-addresses";
import "react-toastify/dist/ReactToastify.css";

const router = createBrowserRouter([
    {
        path: "/",
        element: (
            <RequireAuth>
                <Root />
            </RequireAuth>
        ),
        errorElement: <ErrorPage />,
        children: [
            {
                path: "/",
                element: <IPAddressesTable />,
            },
            {
                path: "/ip-ranges",
                element: <IPRangesTable />,
            },
            {
                path: "/subnets",
                element: <SubnetsTable />,
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
