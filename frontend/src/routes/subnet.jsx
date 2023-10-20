import {useState, useEffect} from "react";
import useAuth from "../hooks/useAuth";
import instance from "../api/axios";
import DataTable from "../components/Table";
import {Backdrop, Box, Button, Container, Fade, IconButton, Modal, Paper, TextField, Typography} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";

const columns = [
    {id: "id", label: "ID", minWidth: 100},
    {id: "name", label: "Name", minWidth: 170},
    {id: "cidr", label: "CIDR", minWidth: 170},
    {id: "mask", label: "Mask", minWidth: 100},
    {id: "gateway", label: "Gateway", minWidth: 170},
    {id: "size", label: "Size", minWidth: 100},
    {id: "status", label: "Status", minWidth: 100},
    {
        id: "expiration",
        label: "Expiration",
        minWidth: 200,
        format: (value) => (value ? new Date(value).toLocaleString() : "------"),
    },
    {
        id: "updatedAt",
        label: "Updated At",
        minWidth: 200,
        format: (value) => new Date(value).toLocaleString(),
    },
    {
        id: "user",
        label: "User",
        minWidth: 170,
        format: (value) => (value ? value : "------"),
    },
];

const style = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: 400,
    bgcolor: "white",
    boxShadow: 24,
    p: 4,
};

export default function SubnetsTable() {
    const [data, setData] = useState([]);
    const {authState, getRole} = useAuth();
    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);
    const [form, setForm] = useState({
        name: "",
        cidr: "",
        mask: "",
        gateway: "",
    });

    const addSubnet = async (e) => {
        e.preventDefault();
        // Validate the form fields here if needed
        if (form.name === "" || form.cidr === "" || form.mask === "" || form.gateway === "") {
            return;
        }
        const URL = "/api/ipam/subnets";
        const res = await instance.post(
            URL,
            {
                ...form,
                status: "AVAILABLE", // Set the default status, change as needed
            },
            {
                headers: {
                    Authorization: "Bearer " + authState?.token,
                },
            }
        );
        console.log(res);
        if (res.status === 201) {
            fetchData();
            handleClose();
        }
    };

    const fetchData = async () => {
        try {
            const URL = getRole() === "ROLE_ADMIN" ? "/api/ipam/subnets" : "/api/ipam/subnets/available";
            const response = await instance(URL, {
                headers: {
                    Authorization: "Bearer " + authState?.token,
                },
            });
            setData(response.data);
            console.log(response.data);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    return (
        <Container sx={{display: "flex", flexDirection: "column"}}>
            <Paper
                sx={{
                    width: "100%",
                    padding: "1rem",
                    display: "flex",
                    justifyContent: "space-between",
                    overflow: "hidden",
                    borderRadius: "0",
                    backgroundColor: "transparent",
                    boxShadow: "none",
                }}>
                <h1>Subnets</h1>
                {getRole() === "ROLE_ADMIN" ? (
                    <IconButton onClick={handleOpen}>
                        <AddIcon />
                    </IconButton>
                ) : (
                    ""
                )}
            </Paper>
            <DataTable columns={columns} rows={data} />
            <Modal
                aria-labelledby='transition-modal-title'
                aria-describedby='transition-modal-description'
                open={open}
                onClose={handleClose}
                closeAfterTransition
                slots={{backdrop: Backdrop}}
                slotProps={{
                    backdrop: {
                        timeout: 500,
                    },
                }}>
                <Fade in={open}>
                    <Box sx={style}>
                        <Typography id='transition-modal-title' variant='h6' component='h2'>
                            Add Subnet
                        </Typography>
                        <TextField
                            sx={{width: "100%", marginBottom: "1rem"}}
                            id='outlined-basic'
                            label='Name'
                            variant='outlined'
                            value={form.name}
                            onChange={(e) => setForm({...form, name: e.target.value})}
                        />
                        <TextField
                            sx={{width: "100%", marginBottom: "1rem"}}
                            id='outlined-basic'
                            label='CIDR'
                            variant='outlined'
                            value={form.cidr}
                            onChange={(e) => setForm({...form, cidr: e.target.value})}
                        />
                        <TextField
                            sx={{width: "100%", marginBottom: "1rem"}}
                            id='outlined-basic'
                            label='Mask'
                            variant='outlined'
                            value={form.mask}
                            onChange={(e) => setForm({...form, mask: e.target.value})}
                        />
                        <TextField
                            sx={{width: "100%", marginBottom: "1rem"}}
                            id='outlined-basic'
                            label='Gateway'
                            variant='outlined'
                            value={form.gateway}
                            onChange={(e) => setForm({...form, gateway: e.target.value})}
                        />
                        <Button sx={{width: "100%"}} variant='contained' onClick={addSubnet}>
                            Add
                        </Button>
                    </Box>
                </Fade>
            </Modal>
        </Container>
    );
}
