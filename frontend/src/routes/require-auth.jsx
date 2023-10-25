import useAuth from "../hooks/useAuth";
import {Navigate} from "react-router-dom";
import {useLocation} from "react-router-dom";

const RequireAuth = ({children}) => {
    const location = useLocation();
    const {isAuthenticated} = useAuth();
    return isAuthenticated() ? children : <Navigate to='/login' state={{from: location}} />;
};

export default RequireAuth;
