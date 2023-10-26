import React, {useState, useEffect, useRef, useCallback} from "react";
import useAuth from "../../hooks/useAuth";
import {
    Box,
    Button,
    FormControl,
    InputLabel,
    MenuItem,
    Paper,
    Select,
    Tab,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TablePagination,
    TableRow,
    Tabs,
    Typography,
} from "@mui/material";
import {ToastContainer, toast} from "react-toastify";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import {Chip} from "@mui/joy";

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
    {id: "actions", label: "Actions", minWidth: 170},
];

const toastConfig = {
    position: "top-right",
    autoClose: 1000,
    hideProgressBar: false,
    closeOnClick: true,
    draggable: true,
    progress: undefined,
    theme: "light",
};

const IPAdressesTable = ({type}) => {
    const [rows, setRows] = useState([]);
    const {authState} = useAuth();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const hasMounted = useRef(false);
    const {axiosPrivate} = useAxiosPrivate();
    const [rangeId, setRangeId] = useState("");
    const [ranges, setRanges] = useState([]);

    const handleChange = (event) => {
        setRangeId(event.target.value);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
        setRangeId("");
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(+event.target.value);
        setPage(0);
    };

    const request = async (id) => {
        await axiosPrivate.post(`/api/ipam/allocate/ipaddresses/${id}/users/${authState?.id}`);
        toast(`🦄 ip address allocated`, toastConfig);
        fetchData();
    };

    const generateDns = async (id) => {
        await axiosPrivate.post(`/api/ipam/ipaddresses/${id}/dns`);
        toast(`🦄 dns generated`, toastConfig);
        fetchData();
    };
    const fetchRanges = useCallback(async () => {
        try {
            const response = await axiosPrivate.get(`/api/ipam/ipranges/available`);
            setRanges(response.data);
        } catch (error) {
            console.log("Error fetching data:", error);
        }
    }, [axiosPrivate]);

    const fetchData = useCallback(async () => {
        try {
            let URL =
                type === "available"
                    ? "/api/ipam/ipaddresses/available"
                    : `/api/ipam/users/${authState?.id}/ipaddresses`;

            URL = rangeId ? `/api/ipam/ipranges/${rangeId}/ipaddresses/available` : URL;

            const response = await axiosPrivate.get(URL);
            setRows(response.data);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }, [authState?.id, axiosPrivate, type, rangeId]);

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
                    justifyContent: "space-between",
                    alignItems: "center",
                    overflow: "hidden",
                    borderRadius: "0",
                    backgroundColor: "transparent",
                    boxShadow: "none",
                }}>
                <h1>IP Addresses - {type}</h1>
                {type === "available" && (
                    <FormControl sx={{m: 1, minWidth: 200}}>
                        <InputLabel id='demo-simple-select-helper-label'>Ip Range</InputLabel>
                        <Select
                            labelId='demo-simple-select-helper-label'
                            id='demo-simple-select-helper'
                            value={rangeId}
                            label='Ip Range'
                            onChange={handleChange}>
                            <MenuItem key='all12324' value=''>
                                <em>None</em>
                            </MenuItem>
                            {ranges.map((range) => (
                                <MenuItem key={range.id} value={range.id}>
                                    <Typography paragraph m='0' fontWeight='700' fontSize='14px'>
                                        {range.startAddress} - {range.endAddress}
                                    </Typography>
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                )}
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
                                            if (column.id === "actions")
                                                return (
                                                    <TableCell key={column.id} align={column.align}>
                                                        {type === "available" ? (
                                                            <Button variant='contained' onClick={() => request(row.id)}>
                                                                Request
                                                            </Button>
                                                        ) : (
                                                            <Button
                                                                variant='contained'
                                                                onClick={() => generateDns(row.id)}
                                                                disabled={!!row.dns}>
                                                                Generate DNS
                                                            </Button>
                                                        )}
                                                    </TableCell>
                                                );
                                            else
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
            <ToastContainer />
        </React.Fragment>
    );
};

export default function Home() {
    const [currentTabIndex, setCurrentTabIndex] = useState(0);
    const handleChange = (event, newValue) => {
        setCurrentTabIndex(newValue);
    };
    const tabs = [
        {
            label: "Available",
            component: <IPAdressesTable type='available' />,
        },
        {
            label: "Allocated",
            component: <IPAdressesTable type='allocated' />,
        },
    ];
    return (
        <React.Fragment>
            <Box sx={{paddingTop: "1rem", borderBottom: "1px solid #e0e0e0"}}>
                <Tabs value={currentTabIndex}>
                    {tabs.map((tab, index) => (
                        <Tab key={index} label={tab.label} onClick={(event) => handleChange(event, index)} />
                    ))}
                </Tabs>
            </Box>
            {tabs[currentTabIndex].component}
        </React.Fragment>
    );
}
