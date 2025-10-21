import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Grid,
  Stack,
} from '@mui/material';
import {
  QrCodeScanner as QrIcon,
  Restaurant as RestaurantIcon,
  Kitchen as KitchenIcon,
  AdminPanelSettings as AdminIcon,
} from '@mui/icons-material';

const HomePage = () => {
  const navigate = useNavigate();

  const menuCards = [
    {
      title: 'QRコードで注文',
      description: 'テーブルのQRコードをスキャンして注文を開始',
      icon: <QrIcon sx={{ fontSize: 48, color: 'primary.main' }} />,
      action: () => navigate('/scan'),
      color: 'primary.main',
    },
    {
      title: '直接注文',
      description: 'テーブル番号を入力して注文',
      icon: <RestaurantIcon sx={{ fontSize: 48, color: 'secondary.main' }} />,
      action: () => navigate('/menu'),
      color: 'secondary.main',
    },
    {
      title: '厨房画面',
      description: '注文管理・調理進捗確認',
      icon: <KitchenIcon sx={{ fontSize: 48, color: 'success.main' }} />,
      action: () => navigate('/kitchen'),
      color: 'success.main',
    },
    {
      title: '管理画面',
      description: 'メニュー管理・売上確認',
      icon: <AdminIcon sx={{ fontSize: 48, color: 'warning.main' }} />,
      action: () => navigate('/admin'),
      color: 'warning.main',
    },
  ];

  return (
    <Container maxWidth="md">
      <Box sx={{ py: 4 }}>
        {/* ヘッダー */}
        <Box sx={{ textAlign: 'center', mb: 6 }}>
          <Typography
            variant="h3"
            component="h1"
            sx={{
              fontWeight: 'bold',
              color: 'primary.main',
              mb: 2,
              textShadow: '2px 2px 4px rgba(0,0,0,0.1)',
            }}
          >
            居酒屋「みどり亭」
          </Typography>
          <Typography
            variant="h5"
            color="text.secondary"
            sx={{ fontWeight: 300 }}
          >
            注文システム
          </Typography>
          <Box
            sx={{
              width: 100,
              height: 4,
              bgcolor: 'primary.main',
              mx: 'auto',
              mt: 2,
              borderRadius: 2,
            }}
          />
        </Box>

        {/* メニューカード */}
        <Grid container spacing={3}>
          {menuCards.map((card, index) => (
            <Grid item xs={12} sm={6} key={index}>
              <Card
                sx={{
                  height: '100%',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    boxShadow: '0 8px 25px rgba(0,0,0,0.15)',
                  },
                }}
                onClick={card.action}
              >
                <CardContent sx={{ p: 4, textAlign: 'center' }}>
                  <Stack spacing={2} alignItems="center">
                    <Box
                      sx={{
                        p: 2,
                        borderRadius: '50%',
                        bgcolor: `${card.color}15`,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      {card.icon}
                    </Box>
                    <Typography
                      variant="h6"
                      component="h2"
                      sx={{ fontWeight: 600 }}
                    >
                      {card.title}
                    </Typography>
                    <Typography
                      variant="body2"
                      color="text.secondary"
                      sx={{ textAlign: 'center' }}
                    >
                      {card.description}
                    </Typography>
                    <Button
                      variant="contained"
                      sx={{
                        mt: 2,
                        bgcolor: card.color,
                        '&:hover': {
                          bgcolor: card.color,
                          opacity: 0.9,
                        },
                      }}
                    >
                      開始
                    </Button>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        {/* フッター */}
        <Box sx={{ textAlign: 'center', mt: 6, pt: 4, borderTop: '1px solid #eee' }}>
          <Typography variant="body2" color="text.secondary">
            © 2024 居酒屋さくら亭. All rights reserved.
          </Typography>
          <Typography variant="caption" color="text.secondary">
            営業時間: 17:00 - 24:00 | 定休日: 月曜日
          </Typography>
        </Box>
      </Box>
    </Container>
  );
};

export default HomePage;