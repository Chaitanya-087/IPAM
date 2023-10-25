import {createContext, useState} from "react";

export const AuthContext = createContext();

export const AuthProvider = ({children}) => {
    const [authState, setAuthState] = useState(() => getPersistedAuthState());
    const [isLoading, setIsLoading] = useState(false);

    const isAuthenticated = () => {
        return !!getPersistedAuthState().token && !isTokenExpired(authState.token);
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
    };

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
