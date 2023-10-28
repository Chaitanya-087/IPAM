import {Divider, Typography} from "@mui/material";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import EventAvailableIcon from "@mui/icons-material/EventAvailable";
import EventBusyIcon from "@mui/icons-material/EventBusy";
import "../../styles/stats.css"

const Stats = ({stats}) => {
    return (
        <>
            <Typography variant='h4' component='div'>
                Stats
            </Typography>
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
};

export default Stats;
