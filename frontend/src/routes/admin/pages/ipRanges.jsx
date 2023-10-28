import React, {useState, useEffect, useRef, useCallback} from "react";
import {Backdrop, Box, Button, Fade, IconButton, Modal, Paper, TextField, Typography} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import {ToastContainer, toast} from "react-toastify";
import {Chip} from "@mui/joy";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";
import Stats from "../Stats";
import DataTable from "../../../components/Table";

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

    return (
        <Button variant='contained' onClick={reserve} disabled={status === "RESERVED" || status === "IN_USE"}>
            Reserve
        </Button>
    );
}

export default function IPRanges() {
    const columns = [
        {
            id: "startAddress",
            label: "Start Address",
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
            id: "endAddress",
            label: "End Address",
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
            id: "status",
            label: "Status",
            minWidth: 100,
            component: function ({value}) {
                const colorMap= {AVAILABLE: "success", IN_USE: "danger", RESERVED: "warning"}
                return (
                    <Chip key={value} color={colorMap[value]} size='sm'>
                        {value}
                    </Chip>
                );
            },
        },
        {
            id: "expiration",
            label: "Expiration Date",
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
            id: "updatedAt",
            label: "Updated At",
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
                return <ActionButton id={id} status={value} fetchData={fetchData} />;
            },
        },
    ];
    const [rows, setRows] = useState([]);
    const [open, setOpen] = useState(false);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalRows, setTotalRows] = useState(0);
    const [stats, setStats] = useState({reservedCount: 0, inuseCount: 0, availableCount: 0});
    const [form, setForm] = useState({startAddress: "", endAddress: ""});
    const hasMounted = useRef(false);
    const {axiosPrivate} = useAxiosPrivate();

    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);
    const handleChangePage = (newPage) => setPage(newPage);
    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(+event.target.value);
        setPage(0);
    };

    const post = async (event) => {
        event.preventDefault();
        if (form.startAddress === "" || form.endAddress === "") {
            return;
        }
        const URL = "/api/ipam/ipranges";
        const res = await axiosPrivate.post(URL, {...form});
        toast(`🦄 new ip address added to pool`, toastConfig);
        if (res.status === 201) {
            fetchData();
            handleClose();
        }
    };

    const fetchData = useCallback(async () => {
        try {
            const URL = `/api/ipam/ipranges?page=${page}&size=${rowsPerPage}`;
            const response = await axiosPrivate.get(URL);
            setRows(response.data.data);
            setTotalRows(response.data.totalElements);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }, [axiosPrivate, page, rowsPerPage]);

    const fetchStats = useCallback(async () => {
        try {
            const URL = "/api/ipam/admin/iprange-scan";
            const response = await axiosPrivate.get(URL);
            setStats(response.data);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }, [axiosPrivate]);

    useEffect(() => {
        if (hasMounted.current) {
            fetchData();
            fetchStats();
        }
        return () => {
            hasMounted.current = true;
        };
    }, [fetchData, fetchStats]);

    return (
        <React.Fragment>
            <Stats stats={stats} />
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
                <h1>IP Ranges</h1>
                <IconButton onClick={handleOpen}>
                    <AddIcon />
                </IconButton>
            </Paper>
            <DataTable
                columns={columns}
                rows={rows}
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
                            Add IP Range
                        </Typography>
                        <TextField
                            sx={{width: "100%", marginBottom: "1rem"}}
                            id='outlined-basic'
                            label='Start Address'
                            variant='outlined'
                            value={form.startAddress}
                            onChange={(e) => setForm((prev) => ({...prev, startAddress: e.target.value}))}
                        />
                        <TextField
                            sx={{width: "100%", marginBottom: "1rem"}}
                            id='outlined-basic'
                            label='End Address'
                            variant='outlined'
                            value={form.endAddress}
                            onChange={(e) => setForm((prev) => ({...prev, endAddress: e.target.value}))}
                        />
                        <Button sx={{width: "100%"}} variant='contained' onClick={post}>
                            Add
                        </Button>
                    </Box>
                </Fade>
            </Modal>
            <ToastContainer />
        </React.Fragment>
    );
}
