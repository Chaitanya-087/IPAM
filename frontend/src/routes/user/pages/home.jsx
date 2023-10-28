import React, {useState, useEffect, useRef, useCallback} from "react";
import useAuth from "../../../hooks/useAuth";
import {Box, Button, FormControl, InputLabel, MenuItem, Paper, Select, Tab, Tabs, Typography} from "@mui/material";
import {ToastContainer, toast} from "react-toastify";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";
import {Chip} from "@mui/joy";
import DataTable from "../../../components/Table";

const toastConfig = {
    position: "top-right",
    autoClose: 1000,
    hideProgressBar: false,
    closeOnClick: true,
    draggable: true,
    progress: undefined,
    theme: "light",
};

function ActionButton(props) {
    const {id, status, fetchData} = props;
    const {axiosPrivate} = useAxiosPrivate();
    const {authState} = useAuth();

    const request = async () => {
        await axiosPrivate.post(`/api/ipam/allocate/ipaddresses/${id}/users/${authState?.id}`);
        toast(`🦄 ip address allocated`, toastConfig);
        fetchData();
    };

    const generateDns = async () => {
        await axiosPrivate.post(`/api/ipam/ipaddresses/${id}/dns`);
        toast(`🦄 dns generated`, toastConfig);
        fetchData();
    };

    if (status === "IN_USE") {
        return (
            <Button variant='contained' onClick={generateDns} id='reserve-btn'>
                Generate DNS
            </Button>
        );
    }
    return (
        <Button variant='contained' onClick={request} id='request-btn'>
            Request
        </Button>
    );
}

function IPAdressesTable({type}) {
    const columns = [
        {
            id: "address",
            label: "Address",
            minWidth: 170,
            component: function ({value}) {
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
            component: function ({value}) {
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
            component: function ({value}) {
                const colorMap = {AVAILABLE: "success", IN_USE: "danger", RESERVED: "warning"};
                return (
                    <Chip key={value} color={colorMap[value]} size='sm'>
                        {value}
                    </Chip>
                );
            },
        },
        {
            id: "expiration",
            label: "Expiration",
            minWidth: 170,
            component: function ({value}) {
                return (
                    <Typography paragraph m='0' fontWeight='700' fontSize='12px'>
                        {value ? new Date(value).toLocaleString() : "-----"}
                    </Typography>
                );
            },
        },
        {
            id: "status",
            label: "Actions",
            minWidth: 170,
            component: function ({value, id}) {
                return <ActionButton id={id} status={value} fetchData={fetchData} />;
            },
        },
    ];
    const [rows, setRows] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalRows, setTotalRows] = useState(0);

    const {authState} = useAuth();
    const {axiosPrivate} = useAxiosPrivate();
    const hasMounted = useRef(false);
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

    const fetchRanges = useCallback(async () => {
        try {
            const response = await axiosPrivate.get(`/api/ipam/ipranges/available`);
            setRanges(response.data.data);
        } catch (error) {
            console.log("Error fetching data:", error);
        }
    }, [axiosPrivate]);

    const fetchData = useCallback(async () => {
        let URL = `/api/ipam/ipaddresses/available?page=${page}&size=${rowsPerPage}`;
        if (type === "allocated") {
            URL = `/api/ipam/users/${authState?.id}/ipaddresses?page=${page}&size=${rowsPerPage}`;
        }
        if (rangeId) {
            URL = `/api/ipam/ipranges/${rangeId}/ipaddresses/available?page=${page}&size=${rowsPerPage}`;
        }
        const response = await axiosPrivate.get(URL);
        setRows(response.data.data);
        setTotalRows(response.data.totalElements);
    }, [authState?.id, axiosPrivate, page, rangeId, rowsPerPage, type]);

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
            <DataTable
                rows={rows}
                columns={columns}
                page={page}
                count={totalRows}
                rowsPerPage={rowsPerPage}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />
            <ToastContainer />
        </React.Fragment>
    );
}

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
