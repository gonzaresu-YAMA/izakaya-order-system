import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import {
  Container,
  Box,
  Typography,
  Card,
  CardContent,
  CardMedia,
  Button,
  Grid,
  Chip,
  IconButton,
  AppBar,
  Toolbar,
  Badge,
  Tabs,
  Tab,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Snackbar,
  Alert,
  Fab,
  Stack,
  Divider,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  ShoppingCart as CartIcon,
  Add as AddIcon,
  Remove as RemoveIcon,
  Search as SearchIcon,
} from '@mui/icons-material';
import { menuAPI, tableAPI } from '../services/api';

const MenuPage = () => {
  const { tableNumber } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  
  // URLパラメータからテーブル番号を取得
  const urlParams = new URLSearchParams(location.search);
  const tableParam = tableNumber || urlParams.get('table');

  // State
  const [menuItems, setMenuItems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [cart, setCart] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [tableInfo, setTableInfo] = useState(null);
  const [selectedItem, setSelectedItem] = useState(null);
  const [itemDialog, setItemDialog] = useState(false);
  const [specialInstructions, setSpecialInstructions] = useState('');
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  // カテゴリー定義
  const categoryLabels = {
    APPETIZER: '前菜',
    SASHIMI: '刺身',
    GRILLED: '焼き物',
    FRIED: '揚げ物',
    HOT_POT: '鍋物',
    RICE: 'ご飯物',
    NOODLES: '麺類',
    DESSERT: 'デザート',
    SOFT_DRINK: 'ソフトドリンク',
    ALCOHOLIC: 'アルコール',
    BEER: 'ビール',
    SAKE: '日本酒',
    SHOCHU: '焼酎',
    WINE: 'ワイン',
    COCKTAIL: 'カクテル',
  };

  useEffect(() => {
    loadMenuData();
    if (tableParam) {
      loadTableInfo();
    }
  }, [tableParam]);

  const loadMenuData = async () => {
    try {
      setLoading(true);
      const response = await menuAPI.getAvailableItems();
      const items = response.data;
      setMenuItems(items);

      // カテゴリー抽出
      const uniqueCategories = [...new Set(items.map(item => item.category))];
      setCategories(uniqueCategories);
      
      if (uniqueCategories.length > 0) {
        setSelectedCategory(uniqueCategories[0]);
      }
    } catch (err) {
      console.error('Failed to load menu:', err);
      setError('メニューの読み込みに失敗しました。');
    } finally {
      setLoading(false);
    }
  };

  const loadTableInfo = async () => {
    try {
      const response = await tableAPI.getTableByNumber(tableParam);
      setTableInfo(response.data);
    } catch (err) {
      console.error('Failed to load table info:', err);
      setError('テーブル情報の取得に失敗しました。');
    }
  };

  const filteredItems = menuItems.filter(item => {
    const matchesCategory = selectedCategory ? item.category === selectedCategory : true;
    const matchesSearch = searchQuery 
      ? item.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.description.toLowerCase().includes(searchQuery.toLowerCase())
      : true;
    return matchesCategory && matchesSearch;
  });

  const handleItemClick = (item) => {
    setSelectedItem(item);
    setSpecialInstructions('');
    setItemDialog(true);
  };

  const handleAddToCart = (quantity = 1) => {
    if (!selectedItem) return;

    const existingItem = cart.find(item => 
      item.id === selectedItem.id && 
      item.specialInstructions === specialInstructions
    );

    if (existingItem) {
      setCart(cart.map(item =>
        item.id === selectedItem.id && item.specialInstructions === specialInstructions
          ? { ...item, quantity: item.quantity + quantity }
          : item
      ));
    } else {
      setCart([...cart, {
        ...selectedItem,
        quantity,
        specialInstructions,
      }]);
    }

    setItemDialog(false);
    setSnackbar({
      open: true,
      message: `${selectedItem.name} をカートに追加しました`,
      severity: 'success',
    });
  };

  const updateCartItemQuantity = (itemId, instructions, newQuantity) => {
    if (newQuantity <= 0) {
      setCart(cart.filter(item => 
        !(item.id === itemId && item.specialInstructions === instructions)
      ));
    } else {
      setCart(cart.map(item =>
        item.id === itemId && item.specialInstructions === instructions
          ? { ...item, quantity: newQuantity }
          : item
      ));
    }
  };

  const getTotalItems = () => {
    return cart.reduce((total, item) => total + item.quantity, 0);
  };

  const getTotalPrice = () => {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const handleGoToCart = () => {
    navigate('/cart', { 
      state: { 
        cart, 
        tableInfo: tableInfo || { tableNumber: tableParam } 
      } 
    });
  };

  const formatPrice = (price) => {
    return `¥${price.toLocaleString()}`;
  };

  if (loading) {
    return (
      <Container>
        <Box sx={{ py: 4, textAlign: 'center' }}>
          <Typography>メニューを読み込んでいます...</Typography>
        </Box>
      </Container>
    );
  }

  return (
    <>
      {/* ヘッダー */}
      <AppBar position="sticky" sx={{ bgcolor: 'primary.main' }}>
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={() => navigate('/')}>
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            メニュー {tableParam && `- テーブル ${tableParam}`}
          </Typography>
          <IconButton color="inherit" onClick={handleGoToCart}>
            <Badge badgeContent={getTotalItems()} color="secondary">
              <CartIcon />
            </Badge>
          </IconButton>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ pb: 10 }}>
        {/* エラー表示 */}
        {error && (
          <Alert severity="error" sx={{ mt: 2 }} onClose={() => setError('')}>
            {error}
          </Alert>
        )}

        {/* 検索バー */}
        <Box sx={{ mt: 2, mb: 3 }}>
          <TextField
            fullWidth
            placeholder="メニューを検索..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            InputProps={{
              startAdornment: <SearchIcon sx={{ mr: 1, color: 'text.secondary' }} />,
            }}
          />
        </Box>

        {/* カテゴリータブ */}
        <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
          <Tabs
            value={selectedCategory}
            onChange={(e, newValue) => setSelectedCategory(newValue)}
            variant="scrollable"
            scrollButtons="auto"
          >
            {categories.map(category => (
              <Tab
                key={category}
                label={categoryLabels[category] || category}
                value={category}
              />
            ))}
          </Tabs>
        </Box>

        {/* メニューアイテム */}
        <Grid container spacing={2}>
          {filteredItems.map(item => (
            <Grid item xs={12} sm={6} md={4} key={item.id}>
              <Card
                sx={{
                  height: '100%',
                  cursor: 'pointer',
                  transition: 'all 0.2s',
                  '&:hover': {
                    transform: 'translateY(-2px)',
                    boxShadow: 3,
                  },
                }}
                onClick={() => handleItemClick(item)}
              >
                {item.imageUrl && (
                  <CardMedia
                    component="img"
                    height="120"
                    image={item.imageUrl}
                    alt={item.name}
                  />
                )}
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography variant="h6" component="h3" sx={{ mb: 1 }}>
                    {item.name}
                  </Typography>
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ mb: 2, height: 40, overflow: 'hidden' }}
                  >
                    {item.description}
                  </Typography>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="h6" color="primary.main" sx={{ fontWeight: 600 }}>
                      {formatPrice(item.price)}
                    </Typography>
                    {item.preparationTimeMinutes && (
                      <Chip
                        label={`${item.preparationTimeMinutes}分`}
                        size="small"
                        color="secondary"
                      />
                    )}
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        {filteredItems.length === 0 && (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <Typography color="text.secondary">
              該当するメニューが見つかりません
            </Typography>
          </Box>
        )}
      </Container>

      {/* カートへボタン（フローティング） */}
      {cart.length > 0 && (
        <Fab
          color="primary"
          sx={{
            position: 'fixed',
            bottom: 16,
            right: 16,
            zIndex: 1000,
          }}
          onClick={handleGoToCart}
        >
          <Badge badgeContent={getTotalItems()} color="secondary">
            <CartIcon />
          </Badge>
        </Fab>
      )}

      {/* アイテム詳細ダイアログ */}
      <Dialog open={itemDialog} onClose={() => setItemDialog(false)} maxWidth="sm" fullWidth>
        {selectedItem && (
          <>
            <DialogTitle>
              {selectedItem.name}
            </DialogTitle>
            <DialogContent>
              <Typography variant="body1" sx={{ mb: 2 }}>
                {selectedItem.description}
              </Typography>
              <Typography variant="h6" color="primary.main" sx={{ mb: 2 }}>
                {formatPrice(selectedItem.price)}
              </Typography>
              {selectedItem.preparationTimeMinutes && (
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  調理時間: 約{selectedItem.preparationTimeMinutes}分
                </Typography>
              )}
              <Divider sx={{ my: 2 }} />
              <TextField
                fullWidth
                label="特別な指示（オプション）"
                multiline
                rows={2}
                value={specialInstructions}
                onChange={(e) => setSpecialInstructions(e.target.value)}
                placeholder="例: 辛さ控えめ、アレルギー対応など"
                sx={{ mb: 2 }}
              />
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setItemDialog(false)}>
                キャンセル
              </Button>
              <Button
                variant="contained"
                onClick={() => handleAddToCart(1)}
                startIcon={<AddIcon />}
              >
                カートに追加
              </Button>
            </DialogActions>
          </>
        )}
      </Dialog>

      {/* スナックバー */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </>
  );
};

export default MenuPage;