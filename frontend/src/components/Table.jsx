import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import {Typography} from "@mui/material";

function DataTable(props) {
    const {columns, rows, count, page, rowsPerPage ,onPageChange, onRowsPerPageChange} = props;
    return (
        <Paper
            sx={{
                width: "100%",
                overflow: "hidden",
                borderRadius: "0",
                backgroundColor: "transparent",
                boxShadow: "none",
                border: "1px solid #e6e6e6",
            }}>
            <TableContainer sx={{maxHeight: 440}}>
                <Table stickyHeader aria-label='sticky table'>
                    <TableHead>
                        <TableRow>
                            {columns.map((column) => (
                                <TableCell key={column.label} align={column.align} style={{minWidth: column.minWidth}}>
                                    <Typography paragraph fontWeight='700' fontSize='16px' m='0'>
                                        {column.label}
                                    </Typography>
                                </TableCell>
                            ))}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows.map((row) => {
                            return (
                                <TableRow hover role='checkbox' tabIndex={-1} key={row.id}>
                                    {columns.map((column) => {
                                        const value = row[column.id];
                                        return (
                                            <TableCell key={column.label} align={column.align}>
                                                <column.component value={value} id = {row.id}/>
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
                count={count}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={onPageChange}
                onRowsPerPageChange={onRowsPerPageChange}
            />
        </Paper>
    );
}

export default DataTable;
