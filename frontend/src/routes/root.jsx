import { Outlet } from "react-router-dom";
import Navbar from "../components/Navbar";
import RequireAuth from "../require-auth";

const Root = () => {
    return (
        <RequireAuth>
            <Navbar />
            <Outlet />
        </RequireAuth>
    );
};

export default Root;
