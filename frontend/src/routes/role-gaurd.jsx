import React, {useCallback, useEffect, useState} from "react";
import PropTypes from "prop-types";
import useAuth from "../hooks/useAuth";

const RoleGuard = ({componentName}) => {
    const {getRole} = useAuth();
    const [component, setComponent] = useState(null);

    const role = getRole();

    const importComponent = useCallback(
        async (name) => {
            let importedComponent;
            if (role === "ROLE_ADMIN") {
                const adminModule = await import(`./admin/pages/${name}` /* @vite-ignore */);
                importedComponent = adminModule.default;
            } else {
                const userModule = await import(`./user/pages/${name}` /* @vite-ignore */);
                importedComponent = userModule.default;
            }
            setComponent(() => importedComponent);
        },
        [role]
    );

    useEffect(() => {
        importComponent(componentName);
    }, [componentName, importComponent]);

    return component ? React.createElement(component) : null;
};

RoleGuard.propTypes = {
    componentName: PropTypes.string.isRequired,
};

export default RoleGuard;
