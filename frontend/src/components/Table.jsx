import Paper from "@mui/material/Paper";
import PropTypes from "prop-types";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import {Typography} from "@mui/material";
import {BarLoader} from "react-spinners";

const defaultHeight = 440;

export default function DataTable(props) {
    const {columns, rows, count, page, rowsPerPage, onPageChange, onRowsPerPageChange, isLoading} = props;
    return (
        <Paper
            sx={{
                width: "100%",
                overflow: "hidden",
                borderRadius: "0",
                backgroundColor: "transparent",
                boxShadow: "none",
                border: "1px solid #e6e6e6",
                position: "relative",
            }}>
            {isLoading && (
                <div style={{display: "flex", justifyContent: "center", alignItems: "center", width: "100%", zindex: 9999, height: defaultHeight, backdropFilter: "blur(4px)", backgroundColor: "rgba(255, 255, 255, 0.508)", position: "absolute"}}>
                    <BarLoader color='#007fff' loading={isLoading} size={150} />
                </div>
            )}
            <TableContainer sx={{height: defaultHeight}}>
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
                                                <column.component value={value} id={row.id} />
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

DataTable.defaultProps = {
    isLoading: false,
};

DataTable.propTypes = {
    columns: PropTypes.array.isRequired,
    rows: PropTypes.array.isRequired,
    count: PropTypes.number.isRequired,
    page: PropTypes.number.isRequired,
    rowsPerPage: PropTypes.number.isRequired,
    onPageChange: PropTypes.func.isRequired,
    onRowsPerPageChange: PropTypes.func.isRequired,
    isLoading: PropTypes.bool,
}
