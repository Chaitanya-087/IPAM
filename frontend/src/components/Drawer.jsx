import {Box, Divider, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText} from "@mui/material";
import HubIcon from "@mui/icons-material/Hub";
import LayersIcon from "@mui/icons-material/Layers";
import HomeIcon from "@mui/icons-material/Home";
import {NavLink} from "react-router-dom";

const MuiDrawer = ({isDrawerOpen, setIsDrawerOpen}) => {
    return (
        <Drawer anchor='left' open={isDrawerOpen} onClose={() => setIsDrawerOpen(false)}>
            <Box sx={{width: 250}} role='presentation'>
                <List>
                    <ListItem disablePadding>
                        <ListItemButton>
                            <ListItemIcon>
                                <HomeIcon />
                            </ListItemIcon>
                            <NavLink to='/'>
                                <ListItemText primary='ip-addresses' />
                            </NavLink>
                        </ListItemButton>
                    </ListItem>
                    <ListItem disablePadding>
                        <ListItemButton>
                            <ListItemIcon>
                                <LayersIcon />
                            </ListItemIcon>
                            <NavLink to='/ip-ranges'>
                                <ListItemText primary='ip-ranges' />
                            </NavLink>
                        </ListItemButton>
                    </ListItem>
                    <ListItem disablePadding>
                        <ListItemButton>
                            <ListItemIcon>
                                <HubIcon />
                            </ListItemIcon>
                            <NavLink to='/subnets'>
                                <ListItemText primary='subnets' />
                            </NavLink>
                        </ListItemButton>
                    </ListItem>
                    <Divider />
                </List>
            </Box>
        </Drawer>
    );
};

export default MuiDrawer;
