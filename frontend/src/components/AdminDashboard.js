import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

export default function AdminDashboard() {
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4">Admin Dashboard</Typography>
      <Paper sx={{ mt: 2, p: 2 }}>
        <Typography>管理者用ダッシュボード（ダミー）</Typography>
      </Paper>
    </Box>
  );
}
