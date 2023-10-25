import { Box } from '@mui/material'
import React from 'react'
import IPAddressesTable from './ip-addresses'
import IPRangesTable from './ip-ranges'
import SubnetsTable from './subnet'

const Home = () => {
  return (
    <Box flexWrap="wrap" display="flex" padding="1rem">
      <IPAddressesTable />
      <IPRangesTable />
      <SubnetsTable />
    </Box>
  )
}

export default Home
