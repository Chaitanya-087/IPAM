import {Paper, Typography} from "@mui/material";
import {useEffect, useRef, useState} from "react";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import DataTable from "../../components/Table";

export default function UsersTable() {
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [rows, setRows] = useState([]);
    const [totalRows, setTotalRows] = useState(0);
    const hasMounted = useRef(false);
    const {axiosPrivate} = useAxiosPrivate();
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
            id: "email",
            label: "Email",
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
            id: "ipAddressesCount",
            label: "IP Addresses",
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
            id: "ipRangesCount",
            label: "IP Ranges",
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
            id: "subnetsCount",
            label: "Subnets",
            minWidth: 170,
            component: function ({value}) {
                return (
                    <Typography paragraph m='0' fontWeight='700' fontSize='14px'>
                        {value}
                    </Typography>
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

    useEffect(() => {
        const fetchUsers = async () => {
            setIsLoading(true)
            const response = await axiosPrivate.get(`/api/ipam/users?page=${page}&size=${rowsPerPage}`);
            setRows(response.data.data);
            setTotalRows(response.data.totalElements);
            setIsLoading(false)
        };
        if (hasMounted.current) {
            fetchUsers();
        }
        return () => {
            hasMounted.current = true;
        };
    }, [axiosPrivate, page, rowsPerPage]);

    return (
        <Paper
            sx={{
                width: "100%",
                padding: "1rem",
                display: "flex",
                flexDirection: "column",
                overflow: "hidden",
                borderRadius: "0",
                gap: "1rem",
                backgroundColor: "transparent",
                boxShadow: "none",
            }}>
            <h1 id='users-title'>Users</h1>
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
        </Paper>
    );
}
