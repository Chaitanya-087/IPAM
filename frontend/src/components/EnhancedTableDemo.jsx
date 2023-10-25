import * as React from "react";
import PropTypes from "prop-types";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Paper from "@mui/material/Paper";
import Checkbox from "@mui/material/Checkbox";
import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import DeleteIcon from "@mui/icons-material/Delete";
import FilterListIcon from "@mui/icons-material/FilterList";

function createData(id, name, calories, fat, carbs, protein) {
    return {
        id,
        name,
        calories,
        fat,
        carbs,
        protein,
    };
}

const rows = [
    createData(1, "Cupcake", 305, 3.7, 67, 4.3),
    createData(2, "Donut", 452, 25.0, 51, 4.9),
    createData(3, "Eclair", 262, 16.0, 24, 6.0),
    createData(4, "Frozen yoghurt", 159, 6.0, 24, 4.0),
    createData(5, "Gingerbread", 356, 16.0, 49, 3.9),
    createData(6, "Honeycomb", 408, 3.2, 87, 6.5),
    createData(7, "Ice cream sandwich", 237, 9.0, 37, 4.3),
    createData(8, "Jelly Bean", 375, 0.0, 94, 0.0),
    createData(9, "KitKat", 518, 26.0, 65, 7.0),
    createData(10, "Lollipop", 392, 0.2, 98, 0.0),
    createData(11, "Marshmallow", 318, 0, 81, 2.0),
    createData(12, "Nougat", 360, 19.0, 9, 37.0),
    createData(13, "Oreo", 437, 18.0, 63, 4.0),
];

const headCells = [
    {
        id: "name",
        numeric: false,
        disablePadding: true,
        label: "Dessert (100g serving)",
        minWidth: 170,
    },
    {
        id: "calories",
        numeric: true,
        disablePadding: false,
        label: "Calories",
        minWidth: 100,
    },
    {
        id: "fat",
        numeric: true,
        disablePadding: false,
        label: "Fat (g)",
        minWidth: 100,
    },
    {
        id: "carbs",
        numeric: true,
        disablePadding: false,
        label: "Carbs (g)",
        minWidth: 100,
    },
    {
        id: "protein",
        numeric: true,
        disablePadding: false,
        label: "Protein (g)",
        minWidth: 100,
    },
];

function EnhancedTableHead(props) {
    const {onSelectAllClick, numSelected, rowCount} = props;

    return (
        <TableHead>
            <TableRow>
                <TableCell padding='checkbox'>
                    <Checkbox
                        color='primary'
                        indeterminate={numSelected > 0 && numSelected < rowCount}
                        checked={rowCount > 0 && numSelected === rowCount}
                        onChange={onSelectAllClick}
                        inputProps={{
                            "aria-label": "select all desserts",
                        }}
                    />
                </TableCell>
                {headCells.map((headCell) => (
                    <TableCell
                        key={headCell.id}
                        sx={{minWidth: headCell.minWidth}}
                        align={headCell.numeric ? "right" : "left"}
                        padding={headCell.disablePadding ? "none" : "normal"}>
                        {headCell.label}
                    </TableCell>
                ))}
            </TableRow>
        </TableHead>
    );
}

EnhancedTableHead.propTypes = {
    numSelected: PropTypes.number.isRequired,
    onSelectAllClick: PropTypes.func.isRequired,
    rowCount: PropTypes.number.isRequired,
};

function EnhancedTableToolbar(props) {
    const {numSelected} = props;

    return (
        <Toolbar
            sx={{
                pl: {sm: 2},
                pr: {xs: 1, sm: 1},
            }}>
            {numSelected > 0 ? (
                <Typography sx={{flex: "1 1 100%"}} color='inherit' variant='subtitle1' component='div'>
                    {numSelected} selected
                </Typography>
            ) : (
                <Typography sx={{flex: "1 1 100%"}} variant='h6' id='tableTitle' component='div'>
                    Nutrition
                </Typography>
            )}

            {numSelected > 0 ? (
                <Tooltip title='Delete'>
                    <IconButton>
                        <DeleteIcon />
                    </IconButton>
                </Tooltip>
            ) : (
                <Tooltip title='Filter list'>
                    <IconButton>
                        <FilterListIcon />
                    </IconButton>
                </Tooltip>
            )}
        </Toolbar>
    );
}

EnhancedTableToolbar.propTypes = {
    numSelected: PropTypes.number.isRequired,
};

export default function EnhancedTableDemo() {
    const [selected, setSelected] = React.useState([]);
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(5);

    const handleSelectAllClick = (event) => {
        if (event.target.checked) {
            const newSelected = rows.map((n) => n.id);
            setSelected(newSelected);
            return;
        }
        setSelected([]);
    };

    const handleClick = (event, id) => {
        const selectedIndex = selected.indexOf(id);
        let newSelected = [];

        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selected, id);
        } else if (selectedIndex === 0) {
            newSelected = newSelected.concat(selected.slice(1));
        } else if (selectedIndex === selected.length - 1) {
            newSelected = newSelected.concat(selected.slice(0, -1));
        } else if (selectedIndex > 0) {
            newSelected = newSelected.concat(selected.slice(0, selectedIndex), selected.slice(selectedIndex + 1));
        }
        setSelected(newSelected);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const isSelected = (id) => selected.indexOf(id) !== -1;

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
            <EnhancedTableToolbar numSelected={selected.length} />
            <TableContainer sx={{maxHeight: 440}}>
                <Table sx={{minWidth: 440}} aria-labelledby='tableTitle' stickyHeader>
                    <EnhancedTableHead
                        numSelected={selected.length}
                        onSelectAllClick={handleSelectAllClick}
                        rowCount={rows.length}
                    />
                    <TableBody>
                        {rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row, index) => {
                            const isItemSelected = isSelected(row.id);
                            const labelId = `enhanced-table-checkbox-${index}`;
                            return (
                                <TableRow
                                    hover
                                    onClick={(event) => handleClick(event, row.id)}
                                    role='checkbox'
                                    aria-checked={isItemSelected}
                                    tabIndex={-1}
                                    key={row.id}
                                    selected={isItemSelected}
                                    sx={{cursor: "pointer"}}>
                                    <TableCell padding='checkbox'>
                                        <Checkbox
                                            color='primary'
                                            checked={isItemSelected}
                                            inputProps={{
                                                "aria-labelledby": labelId,
                                            }}
                                        />
                                    </TableCell>
                                    <TableCell component='th' id={labelId} scope='row' padding='none'>
                                        {row.name}
                                    </TableCell>
                                    <TableCell align='right'>{row.calories}</TableCell>
                                    <TableCell align='right'>{row.fat}</TableCell>
                                    <TableCell align='right'>{row.carbs}</TableCell>
                                    <TableCell align='right'>{row.protein}</TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
            </TableContainer>
            <TablePagination
                rowsPerPageOptions={[5, 10, 25]}
                component='div'
                count={rows.length}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />
        </Paper>
    );
}
