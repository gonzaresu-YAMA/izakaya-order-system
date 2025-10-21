import React from 'react';
import { Box, Typography, List, ListItem, ListItemText } from '@mui/material';

export default function KitchenDashboard() {
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4">Kitchen Dashboard</Typography>
      <Typography sx={{ mt: 2 }}>ここに厨房向けのリアルタイム注文が表示されます（ダミー）</Typography>
      <List>
        <ListItem>
          <ListItemText primary="注文 #1 - 料理中" />
        </ListItem>
      </List>
    </Box>
  );
}
