import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import useAuth from "../hooks/useAuth";

const RoleGuard = ({ componentName }) => {
  const { getRole } = useAuth();
  const role = getRole();
  const [Component, setComponent] = useState(null);

  useEffect(() => {
    const loadComponent = async () => {
      try {
        let importedComponent;

        if (role === "ROLE_ADMIN") {
          const adminModule = await import(`./admin/pages/${componentName}.jsx`/*@vite-ignore*/);
          importedComponent = adminModule.default;
        } else {
          const userModule = await import(`./user/pages/${componentName}.jsx`/*@vite-ignore*/);
          importedComponent = userModule.default;
        }

        setComponent(() => importedComponent);
      } catch (error) {
        console.error("Error importing component:", error);
      }
    };

    loadComponent();
  }, [componentName, role]);

  return Component ? <Component /> : null;
};

RoleGuard.propTypes = {
  componentName: PropTypes.string.isRequired,
};

export default RoleGuard;
