import { createContext, useState } from "react";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const values = {
        user,
        setUser,
        isAuthenticated,
        setIsAuthenticated
    }
    
    return (
        <AuthContext.Provider value={values} >
            {children}
        </AuthContext.Provider>
    )

}