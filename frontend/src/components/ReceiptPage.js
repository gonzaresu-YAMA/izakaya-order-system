import React from 'react';
import { Box, Typography, Paper } from '@mui/material';
import { useParams } from 'react-router-dom';

export default function ReceiptPage() {
  const { orderId } = useParams();
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4">Receipt</Typography>
      <Paper sx={{ mt: 2, p: 2 }}>
        <Typography>領収書 (注文ID: {orderId}) — ダミー表示</Typography>
      </Paper>
    </Box>
  );
}
