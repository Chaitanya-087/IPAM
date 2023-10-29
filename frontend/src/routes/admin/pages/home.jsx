import React, {useState, useEffect, useRef, useCallback} from "react";
import {Box, Tabs, Tab} from "@mui/material";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";
import Stats from "../Stats";
import ReservationsTable from "../ReservationTable";
import UsersTable from "../UsersTable";
import IPAddressesTable from "../IpAddressesTable";

export default function Home() {
    const hasMounted = useRef(false);
    const {axiosPrivate} = useAxiosPrivate();
    const [stats, setStats] = useState({reservedCount: 0, inuseCount: 0, availableCount: 0});
    const [currentTabIndex, setCurrentTabIndex] = useState(0);
    const handleChange = (event, newValue) => {
        setCurrentTabIndex(newValue);
    };

    const fetchStats = useCallback(async () => {
        try {
            const URL = "/api/ipam/admin/ip-scan";
            const response = await axiosPrivate.get(URL);
            setStats(response.data);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }, [axiosPrivate]);

    const tabs = [
        {id: "ipaddresses", label: "ip addresses", component: <IPAddressesTable />},
        {id: "users", label: "users", component: <UsersTable />},
        {id: "reservations", label: "reservations", component: <ReservationsTable />},
    ];

    useEffect(() => {
        if (hasMounted.current) {
            fetchStats();
        }
        return () => {
            hasMounted.current = true;
        };
    }, [fetchStats]);

    return (
        <React.Fragment>
            <Stats stats={stats} fetchStats={fetchStats} />
            <Box sx={{paddingTop: "1rem", borderBottom: "1px solid #e0e0e0"}}>
                <Tabs value={currentTabIndex} onChange={handleChange}>
                    {tabs.map((tab, index) => (
                        <Tab key={index} label={tab.label} id={tab.label} />
                    ))}
                </Tabs>
            </Box>
            {tabs[currentTabIndex].component}
        </React.Fragment>
    );
}
