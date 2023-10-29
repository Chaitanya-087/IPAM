import SharedLayout from "../components/SharedLayout";
import RequireAuth from "./require-auth";
const Root = () => {
    return (
        <RequireAuth>
            <SharedLayout />
        </RequireAuth>
    );
};

export default Root;
