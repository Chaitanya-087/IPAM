import React, {useState, useEffect, useRef, useCallback} from "react";
import {Box, Button, Paper, Tab, Tabs, Typography} from "@mui/material";
import {ToastContainer, toast} from "react-toastify";
import {Chip} from "@mui/joy";
import useAuth from "../../../hooks/useAuth";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";
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

function SubnetsTable({type}) {
    const [rows, setRows] = useState([]);
    const {authState} = useAuth();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const hasMounted = useRef(false);
    const {axiosPrivate} = useAxiosPrivate();
    const [totalRows, setTotalRows] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const columns = [
        {
            id: "name",
            label: "Name",
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
            id: "cidr",
            label: "CIDR",
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
            id: "mask",
            label: "Mask",
            minWidth: 100,
            component: function ({value}) {
                return (
                    <Typography paragraph m='0' fontWeight='700' fontSize='14px'>
                        {value}
                    </Typography>
                );
            },
        },
        {
            id: "gateway",
            label: "Gateway",
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
            id: "size",
            label: "Size",
            minWidth: 100,
            component: function ({value}) {
                return (
                    <Typography paragraph m='0' fontWeight='700' fontSize='14px'>
                        {value}
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
            minWidth: 200,
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
                return (
                    <Button variant='contained' onClick={() => request(id)} disabled={value === "IN_USE"}>
                        Request
                    </Button>
                );
            },
        },
    ];

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(+event.target.value);
        setPage(0);
    };

    const request = async (id) => {
        await axiosPrivate.post(`/api/ipam/allocate/subnets/${id}/users/${authState?.id}`);
        toast(`🦄 subnet allocated`, toastConfig);
        fetchData();
    };

    const fetchData = useCallback(async () => {
        try {
            setIsLoading(true);
            const URL =
                type === "available"
                    ? `/api/ipam/subnets/available?page=${page}&size=${rowsPerPage}`
                    : `/api/ipam/users/${authState?.id}/subnets?page=${page}&size=${rowsPerPage}`;
            const response = await axiosPrivate.get(URL);
            setRows(response.data.data);
            setTotalRows(response.data.totalElements);
            setIsLoading(false);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }, [authState?.id, axiosPrivate, page, rowsPerPage, type]);

    useEffect(() => {
        if (hasMounted.current) {
            fetchData();
        }
        return () => {
            hasMounted.current = true;
        };
    }, [fetchData]);

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
                <h1>Subnets - {type}</h1>
            </Paper>
            <DataTable
                rows={rows}
                columns={columns}
                count={totalRows}
                page={page}
                onPageChange={handleChangePage}
                rowsPerPage={rowsPerPage}
                onRowsPerPageChange={handleChangeRowsPerPage}
                isLoading={isLoading}
            />
            <ToastContainer />
        </React.Fragment>
    );
}

export default function Subnets() {
    const [currentTabIndex, setCurrentTabIndex] = useState(0);
    const handleChange = (event, newValue) => {
        setCurrentTabIndex(newValue);
    };
    const tabs = [
        {
            label: "Available",
            component: <SubnetsTable type='available' />,
        },
        {
            label: "Allocated",
            component: <SubnetsTable type='allocated' />,
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
