import { useAuth } from "./context/AuthContext";
import { Navigate } from "react-router-dom";

const RequireAuth = ({children}) => {
    const {isAuthenticated} = useAuth();
    return isAuthenticated() ? children : <Navigate to='/login' replace />;
};

export default RequireAuth;
