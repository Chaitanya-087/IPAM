/* eslint-disable react/prop-types */
import {createContext, useState, useEffect, useContext} from "react";

export const AuthContext = createContext();

export const AuthProvider = ({children}) => {
    const [authState, setAuthState] = useState(() => getPersistedAuthState());
    const [isLoading, setIsLoading] = useState(false);

    const isAuthenticated = () => {
        return !!authState?.token && !isTokenExpired(authState.token);
    };

    const isTokenExpired = (token) => {
        const exp = parseInt(JSON.parse(atob(token.split(".")[1]))?.exp, 10);
        return Date.now() >= exp * 1000;
    };
    const persistAuthState = (newAuthState) => {
        localStorage.setItem("auth-state", JSON.stringify(newAuthState));
        setAuthState(newAuthState);
    };

    const getRole = () => {
        return isAuthenticated() ? JSON.parse(atob(authState?.token?.split(".")[1]))?.scope : "";
    };

    const logout = () => {
        persistAuthState({});
    }

    useEffect(() => {
        // Add any async logic for loading authentication state if needed
        const tokenExpiryCheckInterval = setInterval(() => {
            if (authState?.token && isTokenExpired(authState?.token)) {
                persistAuthState({});
                console.log("checking")
            }
        }, 1000);

        return () => {
            clearInterval(tokenExpiryCheckInterval);
        };
    }, []);

    const contextValues = {
        authState,
        isLoading,
        setIsLoading,
        isAuthenticated,
        persistAuthState,
        logout,
        getRole,
    };

    return <AuthContext.Provider value={contextValues}>{children}</AuthContext.Provider>;
};

const getPersistedAuthState = () => {
    const storedAuthState = localStorage.getItem("auth-state");
    return storedAuthState ? JSON.parse(storedAuthState) : {};
};


export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        return;
    }
    return context;    
}