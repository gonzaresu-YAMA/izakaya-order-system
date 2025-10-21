import React from 'react';
import { Box, Typography, Button } from '@mui/material';

export default function OrderCart() {
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4">Order Cart</Typography>
      <Typography sx={{ mt: 2 }}>カートの中身はここに表示されます（ダミー）</Typography>
      <Button variant="contained" sx={{ mt: 2 }}>Checkout</Button>
    </Box>
  );
}
