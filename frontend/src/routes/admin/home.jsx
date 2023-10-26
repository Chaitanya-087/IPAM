import React, {useState, useEffect, useRef, useCallback} from "react";
import {
    Backdrop,
    Box,
    Button,
    Fade,
    IconButton,
    Modal,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TablePagination,
    TableRow,
    TextField,
    Typography,
    Tabs,
    Tab,
    Select,
    FormControl,
    InputLabel,
    MenuItem,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import {ToastContainer, toast} from "react-toastify";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import {Chip} from "@mui/joy";
import Stats from "./Stats";
import DataTable from "../../components/Table";

const columns = [
    {
        id: "address",
        label: "Address",
        minWidth: 170,
        component: function (value) {
            return (
                <Typography paragraph m='0' fontWeight='700' fontSize='14px'>
                    {value}
                </Typography>
            );
        },
    },
    {
        id: "dns",
        label: "DNS name",
        minWidth: 120,
        component: function (value) {
            return (
                <Typography paragraph m='0' color='#007fff' fontWeight='700' noWrap>
                    {value ?? "-----"}
                </Typography>
            );
        },
    },
    {
        id: "status",
        label: "Status",
        minWidth: 100,
        colorMap: {AVAILABLE: "success", IN_USE: "danger", RESERVED: "warning"},
        component: function (value) {
            return (
                <Chip key={value} color={this.colorMap[value]} size='sm'>
                    {value}
                </Chip>
            );
        },
    },
    {
        id: "expiration",
        label: "Expiration",
        minWidth: 170,
        component: function (value) {
            return (
                <Typography paragraph m='0' fontWeight='700' fontSize='12px'>
                    {value ? new Date(value).toLocaleString() : "-----"}
                </Typography>
            );
        },
    },
    {
        id: "updatedAt",
        label: "Updated At",
        minWidth: 170,
        component: function (value) {
            return (
                <Typography paragraph m='0' fontWeight='700' fontSize='12px'>
                    {value ? new Date(value).toLocaleString() : "-----"}
                </Typography>
            );
        },
    },
    {
        id: "user",
        label: "User",
        minWidth: 170,
        component: function (value) {
            return (
                <Typography paragraph m='0' fontWeight='700' fontSize='14px'>
                    {value ? value.name : "-----"}
                </Typography>
            );
        },
    },
    {id: "actions", label: "Actions", minWidth: 170},
];

const style = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    bgcolor: "white",
    boxShadow: 24,
    borderRadius: "8px",
    p: 4,
    width: "360px",
};

const toastConfig = {
    position: "top-right",
    autoClose: 1000,
    hideProgressBar: false,
    closeOnClick: true,
    draggable: true,
    progress: undefined,
    theme: "light",
};

const ActionButton = ({id, status, hasDns, fetchData, fetchStats}) => {
    const {axiosPrivate} = useAxiosPrivate();
    const reserve = async () => {
        await axiosPrivate.post(`/api/ipam/reserve/network-object/${id}`, {purpose: "purpose"});
        toast(`🦄 ip address reserved`, toastConfig);
        fetchData();
        fetchStats();
    };

    const generate = async () => {
        await axiosPrivate.post(`/api/ipam/ipaddresses/${id}/dns`);
        toast(`🦄 dns name generated`, toastConfig);
        fetchData();
    };

    if (status === "AVAILABLE") {
        return (
            <Button variant='contained' onClick={reserve} id='reserve-btn'>
                Reserve
            </Button>
        );
    } else if (status === "IN_USE") {
        return (
            <Button variant='contained' onClick={generate} disabled={hasDns} id='dns-btn'>
                Generate DNS
            </Button>
        );
    } else {
        return (
            <Button variant='contained' disabled id='reserve-btn'>
                Reserve
            </Button>
        );
    }
};

const IPAddressesTable = ({fetchStats}) => {
    const [rows, setRows] = useState([]);
    const [open, setOpen] = useState(false);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [form, setForm] = useState({address: ""});
    const hasMounted = useRef(false);
    const [ranges, setRanges] = useState([]);
    const [rangeId,setRangeId] = useState('');

    const handleChange = (event) => {
        setRangeId(event.target.value);
    };

    const {axiosPrivate} = useAxiosPrivate();

    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(+event.target.value);
        setPage(0);
    };

    const post = async (event) => {
        event.preventDefault();
        if (form.address === "") {
            return;
        }
        const URL = "/api/ipam/ipaddresses";
        const res = await axiosPrivate.post(URL, {...form});
        toast(`🦄 new ip address added to pool`, toastConfig);
        if (res.status === 201) {
            fetchData();
            handleClose();
        }
    };

    const fetchData = useCallback(async () => {
        try {
            const URL = rangeId === '' ? "/api/ipam/ipaddresses": `/api/ipam/ipranges/${rangeId}/ipaddresses`;
            const response = await axiosPrivate.get(URL);
            setRows(response.data);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }, [axiosPrivate,rangeId]);

    const fetchRanges = useCallback(async () => {
        try {
            const URL = "/api/ipam/ipranges";
            const response = await axiosPrivate.get(URL);
            setRanges(response.data);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }, [axiosPrivate]);
    useEffect(() => {
        if (hasMounted.current) {
            fetchData();
            fetchRanges();
        }
        return () => {
            hasMounted.current = true;
        };
    }, [fetchData, fetchRanges]);

    return (
        <React.Fragment>
            <Paper
                sx={{
                    width: "100%",
                    padding: "1rem",
                    display: "flex",
                    alignItems: "center",
                    overflow: "hidden",
                    borderRadius: "0",
                    backgroundColor: "transparent",
                    boxShadow: "none",
                }}>
                <Box sx={{display: "flex", alignItems: "center", gap: "1rem"}}>
                    <h1 id='ipaddress-title'>IP Addresses</h1>
                    <FormControl sx={{m: 1, minWidth: 200}}>
                        <InputLabel id='demo-simple-select-helper-label'>Ip Range</InputLabel>
                        <Select
                            labelId='demo-simple-select-helper-label'
                            id='demo-simple-select-helper'
                            value={rangeId}
                            label='Ip Range'
                            onChange={handleChange}>
                                <MenuItem key="all12312" value="">
                                    <em>None</em>
                                </MenuItem>
                                {
                                    ranges.map((range) => <MenuItem key={range.id} value={range.id}>
                                        <Typography paragraph m='0' fontWeight='700' fontSize='14px'>
                                            {range.startAddress} - {range.endAddress}
                                        </Typography>
                                    </MenuItem>)
                                }
                        </Select>
                    </FormControl>
                </Box>
                <Box sx={{flexGrow: 1}}></Box>
                <IconButton onClick={handleOpen}>
                    <AddIcon />
                </IconButton>
            </Paper>
            <Paper
                sx={{
                    width: "100%",
                    overflow: "hidden",
                    borderRadius: "0",
                    backgroundColor: "transparent",
                    boxShadow: "none",
                    border: "1px solid #e6e6e6",
                }}>
                <TableContainer sx={{maxHeight: 440, overflow: "auto"}}>
                    <Table stickyHeader aria-label='sticky table'>
                        <TableHead>
                            <TableRow>
                                {columns.map((column) => (
                                    <TableCell key={column.id} align={column.align} style={{minWidth: column.minWidth}}>
                                        <Typography paragraph fontWeight='700' fontSize='16px' m='0'>
                                            {column.label}
                                        </Typography>
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row) => {
                                return (
                                    <TableRow hover role='checkbox' tabIndex={-1} key={row.id}>
                                        {columns.map((column) => {
                                            const value = row[column.id];
                                            if (column.id === "actions") {
                                                return (
                                                    <TableCell key={column.id} align={column.align}>
                                                        <ActionButton
                                                            id={row.id}
                                                            status={row.status}
                                                            hasDns={!!row.dns}
                                                            fetchData={fetchData}
                                                            fetchStats={fetchStats}
                                                        />
                                                    </TableCell>
                                                );
                                            }
                                            return (
                                                <TableCell key={column.id} align={column.align}>
                                                    {column.component ? column.component(value) : value}
                                                </TableCell>
                                            );
                                        })}
                                    </TableRow>
                                );
                            })}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions={[10, 25, 100]}
                    component='div'
                    count={rows.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Paper>
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
                            Add IP Address
                        </Typography>
                        <TextField
                            sx={{width: "100%", marginBottom: "1rem"}}
                            id='outlined-basic'
                            label='ip address'
                            variant='outlined'
                            name='address'
                            value={form.address}
                            onChange={(e) => setForm((prev) => ({...prev, address: e.target.value}))}
                            s
                        />
                        <Button sx={{width: "100%"}} variant='contained' onClick={post}>
                            Add
                        </Button>
                    </Box>
                </Fade>
            </Modal>
            <ToastContainer id='popup' />
        </React.Fragment>
    );
};

const UsersTable = () => {
    const columns = [
        {id: "name", label: "Name", minWidth: 170},
        {id: "email", label: "Email", minWidth: 170},
        {id: "ipAddressesCount", label: "IP Addresses", minWidth: 170},
        {id: "ipRangesCount", label: "IP Ranges", minWidth: 170},
        {id: "subnetsCount", label: "Subnets", minWidth: 170},
    ];
    const [rows, setRows] = useState([]);
    const hasMounted = useRef(false);
    const {axiosPrivate} = useAxiosPrivate();
    useEffect(() => {
        const fetchUsers = async () => {
            const response = await axiosPrivate.get("/api/ipam/users");
            console.log(response.data);
            setRows(response.data);
        };
        if (hasMounted.current) {
            fetchUsers();
        }
        return () => {
            hasMounted.current = true;
        };
    }, [axiosPrivate]);
    return (
        <>
            <Paper
                sx={{
                    width: "100%",
                    padding: "1rem",
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    overflow: "hidden",
                    borderRadius: "0",
                    backgroundColor: "transparent",
                    boxShadow: "none",
                }}>
                <h1 id='users-title'>Users</h1>
            </Paper>
            <DataTable rows={rows} columns={columns} />
        </>
    );
};

const ReservationsTable = () => {
    const hasMounted = useRef(false);
    const [rows, setRows] = useState([]);
    const {axiosPrivate} = useAxiosPrivate();
    const columns = [
        {
            id: "id",
            label: "ID",
            minWidth: 170,
        },
        {
            id: "type",
            label: "Type",
            minWidth: 170,
        },
        {
            id: "identifier",
            label: "Identifier",
            minWidth: 170,
        },
        {
            id: "releaseDate",
            label: "Release Date",
            minWidth: 170,
            format: (value) => new Date(value).toLocaleString(),
        },
        {
            id: "purpose",
            label: "Purpose",
            minWidth: 170,
        },
    ];

    useEffect(() => {
        const fetchReservations = async () => {
            const response = await axiosPrivate.get("/api/ipam/reservations");
            console.log(response.data);
            setRows(response.data);
        };
        if (hasMounted.current) {
            fetchReservations();
        }
        return () => {
            hasMounted.current = true;
        };
    }, [axiosPrivate]);

    return (
        <>
            <Paper
                sx={{
                    width: "100%",
                    padding: "1rem",
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    overflow: "hidden",
                    borderRadius: "0",
                    backgroundColor: "transparent",
                    boxShadow: "none",
                }}>
                <h1>Reservations</h1>
            </Paper>
            <DataTable rows={rows} columns={columns} />
        </>
    );
};

export default function Home() {
    const hasMounted = useRef(false);
    const {axiosPrivate} = useAxiosPrivate();
    const [stats, setStats] = useState({reservedCount: 0, inuseCount: 0, availableCount: 0});
    const [currentTabIndex, setCurrentTabIndex] = useState(0);
    const handleChange = (event, newValue) => {
        setCurrentTabIndex(newValue);
    };

    const fetchStats = useCallback(async () => {
        try {
            const URL = "/api/ipam/admin/ip-scan";
            const response = await axiosPrivate.get(URL);
            setStats(response.data);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }, [axiosPrivate]);

    const tabs = [
        {id: "ipaddresses", label: "ip addresses", component: <IPAddressesTable fetchStats={fetchStats} />},
        {id: "users", label: "users", component: <UsersTable />},
        {id: "reservations", label: "reservations", component: <ReservationsTable />},
    ];
    useEffect(() => {
        if (hasMounted.current) {
            fetchStats();
        }
        return () => {
            hasMounted.current = true;
        };
    }, [fetchStats]);

    return (
        <React.Fragment>
            <Stats stats={stats} />
            <Box sx={{paddingTop: "1rem", borderBottom: "1px solid #e0e0e0"}}>
                <Tabs value={currentTabIndex} onChange={handleChange}>
                    {tabs.map((tab, index) => (
                        <Tab key={index} label={tab.label} id={tab.label} />
                    ))}
                </Tabs>
            </Box>
            {tabs[currentTabIndex].component}
        </React.Fragment>
    );
}
