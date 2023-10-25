import {useEffect} from "react";
import {useNavigate, useLocation} from "react-router-dom";
import axiosPrivate from "../api/axios"; // Import your axios instance
import useAuth from "./useAuth";

const useAxiosPrivate = () => {
    const {authState, isAuthenticated} = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const abortController = new AbortController();
        const requestInterceptor = axiosPrivate.interceptors.request.use(
            (config) => {
                if (!isAuthenticated()) {
                    abortController.abort();
                    navigate("/login", {state: {from: location}, replace: true});
                }
                if (!config.headers["Authorization"] && authState.token) {
                    config.headers["Authorization"] = `Bearer ${authState.token}`;
                }
                return {...config, signal: abortController.signal};
            },
            (error) => {
                return Promise.reject(error)}
        );

        return () => {
            axiosPrivate.interceptors.request.eject(requestInterceptor);
        };
    }, [authState, navigate, isAuthenticated, location]);

    return {axiosPrivate};
};

export default useAxiosPrivate;
