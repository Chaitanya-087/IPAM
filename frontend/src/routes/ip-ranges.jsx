import { useState, useEffect } from "react";
import useAuth from "../hooks/useAuth";
import instance from "../api/axios";
import DataTable from "../components/Table";
import {
  Backdrop,
  Box,
  Button,
  Container,
  Fade,
  IconButton,
  Modal,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
const columns = [
    { id: 'id', label: 'ID', minWidth: 100 },
    { id: 'startAddress', label: 'Start Address', minWidth: 170 },
    { id: 'endAddress', label: 'End Address', minWidth: 170 },
    { id: 'status', label: 'Status', minWidth: 100 },
    {
      id: 'expiration',
      label: 'Expiration Date',
      minWidth: 200,
      format: (value) => (value ? new Date(value).toLocaleString() : '------'),
    },
    {
      id: 'size',
      label: 'Size',
      minWidth: 100,
      format: (value) => (value ? value.toString() : 'N/A'),
    },
    {
      id: 'updatedAt',
      label: 'Updated At',
      minWidth: 200,
      format: (value) => new Date(value).toLocaleString(),
    },
    { id: 'user', label: 'User', minWidth: 170, format: (value) => (value ? value : '------') },
  ];
  

const style = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 400,
  bgcolor: "white",
  boxShadow: 24,
  p: 4,
};

export default function IPRangesTable() {
  const [data, setData] = useState([]);
  const { authState, getRole } = useAuth();
  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);
  const [startAddress, setStartAddress] = useState("");
  const [endAddress, setEndAddress] = useState("");

  const addIPRange = async (e) => {
    e.preventDefault();
    if (startAddress === "" || endAddress === "") {
      return;
    }
    const URL = "/api/ipam/ipranges";
    const res = await instance.post(
      URL,
      {
        startAddress,
        endAddress,
      },
      {
        headers: {
          Authorization: "Bearer " + authState?.token,
        },
      }
    );
    console.log(res);
    if (res.status === 201) {
      fetchData();
      handleClose();
    }
  };

  const fetchData = async () => {
    try {
      const URL =
        getRole() === "ROLE_ADMIN"
          ? "/api/ipam/ipranges"
          : "/api/ipam/ipranges/available";
      const response = await instance(URL, {
        headers: {
          Authorization: "Bearer " + authState?.token,
        },
      });
      setData(response.data);
      console.log(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <Container sx={{ display: "flex", flexDirection: "column" }}>
      <Paper
        sx={{
          width: "100%",
          padding: "1rem",
          display: "flex",
          justifyContent: "space-between",
          overflow: "hidden",
          borderRadius: "0",
          backgroundColor: "transparent",
          boxShadow: "none",
        }}
      >
        <h1>IP Ranges</h1>
        {getRole() === "ROLE_ADMIN" ? (
          <IconButton onClick={handleOpen}>
            <AddIcon />
          </IconButton>
        ) : (
          ""
        )}
      </Paper>
      <DataTable columns={columns} rows={data} />
      <Modal
        aria-labelledby="transition-modal-title"
        aria-describedby="transition-modal-description"
        open={open}
        onClose={handleClose}
        closeAfterTransition
        slots={{ backdrop: Backdrop }}
        slotProps={{
          backdrop: {
            timeout: 500,
          },
        }}
      >
        <Fade in={open}>
          <Box sx={style}>
            <Typography id="transition-modal-title" variant="h6" component="h2">
              Add IP Range
            </Typography>
            <TextField
              sx={{ width: "100%", marginBottom: "1rem" }}
              id="outlined-basic"
              label="Start Address"
              variant="outlined"
              value={startAddress}
              onChange={(e) => setStartAddress(e.target.value)}
            />
            <TextField
              sx={{ width: "100%", marginBottom: "1rem" }}
              id="outlined-basic"
              label="End Address"
              variant="outlined"
              value={endAddress}
              onChange={(e) => setEndAddress(e.target.value)}
            />
            <Button sx={{ width: "100%" }} variant="contained" onClick={addIPRange}>
              Add
            </Button>
          </Box>
        </Fade>
      </Modal>
    </Container>
  );
}
