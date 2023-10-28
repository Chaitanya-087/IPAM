import {Chip} from "@mui/joy";
import {
    Backdrop,
    Box,
    Button,
    Fade,
    FormControl,
    IconButton,
    InputLabel,
    MenuItem,
    Modal,
    Paper,
    Select,
    TextField,
    Typography,
} from "@mui/material";
import React, {useCallback, useEffect, useRef, useState} from "react";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import {ToastContainer, toast} from "react-toastify";
import DataTable from "../../components/Table";
import AddIcon from "@mui/icons-material/Add";

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

function ActionButton(props) {
    const {id, status, fetchData} = props;
    const {axiosPrivate} = useAxiosPrivate();

    const reserve = async () => {
        await axiosPrivate.post(`/api/ipam/reserve/network-object/${id}`, {purpose: "purpose"});
        toast(`🦄 ip address reserved`, toastConfig);
        fetchData();
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
            <Button variant='contained' onClick={generate} id='dns-btn'>
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
}

export default function IPAddressesTable() {
    const [rows, setRows] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalRows, setTotalRows] = useState(0);
    const [open, setOpen] = useState(false);
    const [form, setForm] = useState({address: ""});
    const hasMounted = useRef(false);
    const [ranges, setRanges] = useState([]);
    const [rangeId, setRangeId] = useState("");
    const {axiosPrivate} = useAxiosPrivate();

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
            id: "updatedAt",
            label: "Updated At",
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
            id: "user",
            label: "User",
            minWidth: 170,
            component: function ({value}) {
                return (
                    <Typography paragraph m='0' fontWeight='700' fontSize='14px'>
                        {value ? value.name : "-----"}
                    </Typography>
                );
            },
        },
        {
            id: "status",
            label: "Actions",
            minWidth: 170,
            component: function ({value, id}) {
                return <ActionButton status={value} id={id} fetchData={fetchData} />;
            },
        },
    ];

    const handleChange = (event) => setRangeId(event.target.value);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);
    const handleChangePage = (event, newPage) => setPage(newPage);
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
            let url = `/api/ipam/ipaddresses?page=${page}&size=${rowsPerPage}`;
            if (rangeId) {
                url = `/api/ipam/ipranges/${rangeId}/ipaddresses?page=${page}&size=${rowsPerPage}`;
                setPage(0);
                setRowsPerPage(10);
            }

            const response = await axiosPrivate.get(url);
            setRows(response.data.data);
            setTotalRows(response.data.totalElements);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }, [axiosPrivate, page, rowsPerPage, rangeId]);

    const fetchRanges = useCallback(async () => {
        try {
            const URL = "/api/ipam/ipranges";
            const response = await axiosPrivate.get(URL);
            setRanges(response.data.data);
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
                            <MenuItem key='all12312' value=''>
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
                </Box>
                <Box sx={{flexGrow: 1}}></Box>
                <IconButton onClick={handleOpen}>
                    <AddIcon />
                </IconButton>
            </Paper>
            <DataTable
                rows={rows}
                columns={columns}
                count={totalRows}
                page={page}
                onPageChange={handleChangePage}
                rowsPerPage={rowsPerPage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />
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
}
