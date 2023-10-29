import {Paper, Typography} from "@mui/material";
import {useEffect, useRef, useState} from "react";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import DataTable from "../../components/Table";

export default function ReservationsTable() {
    const hasMounted = useRef(false);
    const [rows, setRows] = useState([]);
    const [totalRows, setTotalRows] = useState(0);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const {axiosPrivate} = useAxiosPrivate();
    const [isLoading, setIsLoading] = useState(false);
    const columns = [
        {
            id: "id",
            label: "ID",
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
            id: "type",
            label: "Type",
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
            id: "identifier",
            label: "Identifier",
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
            id: "releaseDate",
            label: "Release Date",
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
            id: "purpose",
            label: "Purpose",
            minWidth: 170,
            component: function ({value}) {
                return (
                    <Typography paragraph m='0' fontWeight='700' fontSize='14px'>
                        {value ? value : "-----"}
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
        const fetchReservations = async () => {
            setIsLoading(true);
            const response = await axiosPrivate.get(`/api/ipam/reservations?page=${page}&size=${rowsPerPage}`);
            setRows(response.data.data);
            setTotalRows(response.data.totalElements);
            setIsLoading(false);
        };
        if (hasMounted.current) {
            fetchReservations();
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
            <h1>Reservations</h1>
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
