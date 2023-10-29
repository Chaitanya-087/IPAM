import {Box, Divider, Typography, styled} from "@mui/material";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import EventAvailableIcon from "@mui/icons-material/EventAvailable";
import EventBusyIcon from "@mui/icons-material/EventBusy";
import "../../styles/stats.css";
import PropTypes from "prop-types";
import RefreshIcon from '@mui/icons-material/Refresh';

const center = {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
};

const StyledButton = styled("button")(() => ({
    border: "1px solid rgb(218, 226, 237)",
    transitionProperty: "all",
    backgroundColor: "transparent",
    transitionDuration: "150ms",
    padding: "0.25rem",
    color: "inherit",
    borderRadius: "10px",
    userSelect: "none",
    ...center,
    ":hover": {
        backgroundColor: "rgba(144, 144, 144, 0.14)",
    },
}));

export default function Stats({stats, fetchStats}) {
    return (
        <>
            <Box sx={{display: 'flex', alignItems: "center", flexWrap: "wrap", justifyContent: "space-between"}}>
                <Typography variant='h4' fontWeight="700" component='div'>
                    Stats
                </Typography>
                <StyledButton onClick={fetchStats}>
                    <RefreshIcon />
                </StyledButton>
            </Box>
            <div className='stats-group'>
                <div className='stat'>
                    <h4 className='title reserved'>Reserved</h4>
                    <Divider />
                    <div className='stat-info'>
                        <Typography variant='h3' component='div'>
                            {stats.reservedCount}
                        </Typography>
                        <div className='icon-container reserved'>
                            <EventBusyIcon />
                        </div>
                    </div>
                </div>
                <div className='stat'>
                    <h4 className='title inuse'>In Use</h4>
                    <Divider />
                    <div className='stat-info'>
                        <Typography variant='h3' component='div'>
                            {stats.inuseCount}
                        </Typography>
                        <div className='icon-container inuse'>
                            <ErrorOutlineIcon />
                        </div>
                    </div>
                </div>
                <div className='stat'>
                    <h4 className='title available'>Available</h4>
                    <Divider />
                    <div className='stat-info'>
                        <Typography variant='h3' component='div'>
                            {stats.availableCount}
                        </Typography>
                        <div className='icon-container available'>
                            <EventAvailableIcon />
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

Stats.propTypes = {
    stats: PropTypes.shape({
        reservedCount: PropTypes.number,
        inuseCount: PropTypes.number,
        availableCount: PropTypes.number,
    }),

    fetchStats: PropTypes.func,
};
