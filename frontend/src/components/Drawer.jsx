import {Box, Divider, Drawer, List, ListItem, Toolbar, Typography, styled} from "@mui/material";
import HubIcon from "@mui/icons-material/Hub";
import LayersIcon from "@mui/icons-material/Layers";
import HomeIcon from "@mui/icons-material/Home";
import {NavLink} from "react-router-dom";
import useAuth from "../hooks/useAuth";
import {useNavigate} from "react-router-dom";
import PropTypes from "prop-types";
import "../styles/drawer.css";

const StyledButton = styled("button")(() => ({
    border: "1px solid rgb(218, 226, 237)",
    transitionProperty: "all",
    backgroundColor: "transparent",
    transitionDuration: "150ms",
    padding: "0.25rem",
    color: "inherit",
    borderRadius: "10px",
    userSelect: "none",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    ":hover": {
        backgroundColor: "rgba(144, 144, 144, 0.14)",
    },
}));

const MuiDrawer = (props) => {
    const {isOpen, setIsOpen, variant, width} = props;
    const navigate = useNavigate();
    const {logout} = useAuth();

    const handleClose = () => {
        setIsOpen(false);
    };
    const handleLogout = () => {
        logout();
        navigate("/login");
    };
    return (
        <Drawer variant={variant} anchor='left' open={isOpen} onClose={handleClose} onClick={handleClose}>
            <Box sx={{width}} role='presentation'>
                <Toolbar sx={{display: "flex", alignItems: "center", justifyContent: "center"}}>
                    <Typography variant='h6' color='#007fff' noWrap component='div' id='title'>
                        IPAM
                    </Typography>
                </Toolbar>
                <List sx={{borderTop: "1px solid #e5eaf2"}}>
                    <NavLink to='/'>
                        <HomeIcon />
                        <Typography>Home</Typography>
                    </NavLink>
                    <NavLink to='/subnets'>
                        <LayersIcon />
                        <Typography>Subnets</Typography>
                    </NavLink>
                    <NavLink to='ip-ranges'>
                        <HubIcon />
                        <Typography>IP Ranges</Typography>
                    </NavLink>
                    <Divider />
                    <ListItem>
                        <StyledButton onClick={handleLogout} sx={{width: "100%"}}>
                            <Typography variant='button' display='block'>
                                Logout
                            </Typography>
                        </StyledButton>
                    </ListItem>
                </List>
            </Box>
        </Drawer>
    );
};

MuiDrawer.propTypes = {
    isOpen: PropTypes.bool,
    setIsOpen: PropTypes.func,
    variant: PropTypes.string.isRequired,
    width: PropTypes.number.isRequired,
};

export default MuiDrawer;
