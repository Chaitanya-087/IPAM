import {AppBar, Box, Container, Toolbar, Typography} from "@mui/material";
import React from "react";
import {AccountCircle} from "@mui/icons-material";
import useAuth from "../hooks/useAuth";
import MuiDrawer from "./Drawer";
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings";
import DragHandleIcon from "@mui/icons-material/DragHandle";
import {styled} from "@mui/material/styles";
import {Outlet} from "react-router-dom";

const drawerWidth = 260;

const appBarStyles = {
    boxShadow: "none",
    borderBottom: "1px solid #e5eaf2",
    backdropFilter: "blur(8px)",
    backgroundColor: "rgba(255,255,255,0.9)",
    color: "#007fff",
};

const center = {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
};

const StyledButton = styled("button")(() => ({
    border: "1px solid rgb(218, 226, 237)",
    transitionProperty: "all",
    backgroundColor: "transparent",
    transitionDuration: "150ms",
    padding: "0.25rem",
    color: "inherit",
    borderRadius: "10px",
    userSelect: "none",
    ...center,
    ":hover": {
        backgroundColor: "rgba(144, 144, 144, 0.14)",
    },
}));

const createResponsiveBox = (displayOnMD, displayOnSM) =>
    styled(Box)(({theme}) => ({
        [theme.breakpoints.down("md")]: {
            display: displayOnMD,
        },
        [theme.breakpoints.up("sm")]: {
            display: displayOnSM,
        },
    }));
const Layout = styled(Box)(({theme}) => ({
    [theme.breakpoints.up("sm")]: {
        marginLeft: `${drawerWidth}px`,
        width: `calc(100% - ${drawerWidth}px)`,
    },
}));
const MobileBox = createResponsiveBox("flex", "none");
const TabletBox = createResponsiveBox("none", "flex");

const SharedLayout = () => {
    const {getRole, authState} = useAuth();
    const [isDrawerOpen, setIsDrawerOpen] = React.useState(false);

    return (
        <Box sx={{display: "flex"}}>
            <TabletBox>
                <MuiDrawer variant='permanent' width={drawerWidth} />
            </TabletBox>
            <Layout>
                <AppBar position='fixed' sx={appBarStyles}>
                    <Toolbar>
                        <MobileBox sx={{display: "flex", gap: "1rem"}}>
                            <StyledButton onClick={() => setIsDrawerOpen(true)} aria-label='open drawer'>
                                <DragHandleIcon />
                            </StyledButton>
                            <Typography variant='h6' noWrap component='div' id='title'>
                                IPAM
                            </Typography>
                        </MobileBox>

                        <Box sx={{flexGrow: 1}} />

                        <StyledButton aria-label='account of current user' aria-haspopup='true'>
                            {getRole() === "ROLE_ADMIN" ? (
                                <>
                                    <AdminPanelSettingsIcon /> admin
                                </>
                            ) : getRole() === "ROLE_USER" ? (
                                <>
                                    <AccountCircle /> {authState?.username}
                                </>
                            ) : (
                                <AccountCircle />
                            )}
                        </StyledButton>
                    </Toolbar>
                </AppBar>
                <Container sx={{maxWidth: "100vw", paddingBlock: "1rem"}}>
                    <Toolbar />
                    <Outlet />
                </Container>
            </Layout>
            <MuiDrawer isOpen={isDrawerOpen} setIsOpen={setIsDrawerOpen} variant='temporary' width={drawerWidth} />
        </Box>
    );
};

export default SharedLayout;
