import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Container } from '@mui/material';

// コンポーネントのインポート
import HomePage from './components/HomePage';
import QRScanner from './components/QRScanner';
import MenuPage from './components/MenuPage';
import OrderCart from './components/OrderCart';
import KitchenDashboard from './components/KitchenDashboard';
import AdminDashboard from './components/AdminDashboard';
import ReceiptPage from './components/ReceiptPage';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* ホームページ */}
          <Route path="/" element={<HomePage />} />
          
          {/* QRコードスキャン */}
          <Route path="/scan" element={<QRScanner />} />
          
          {/* メニューページ（テーブル指定） */}
          <Route path="/menu" element={<MenuPage />} />
          <Route path="/menu/:tableNumber" element={<MenuPage />} />
          
          {/* 注文カート */}
          <Route path="/cart" element={<OrderCart />} />
          
          {/* 厨房ダッシュボード */}
          <Route path="/kitchen" element={<KitchenDashboard />} />
          
          {/* 管理者ダッシュボード */}
          <Route path="/admin" element={<AdminDashboard />} />
          
          {/* 領収書ページ */}
          <Route path="/receipt/:orderId" element={<ReceiptPage />} />
          
          {/* 404ページ */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;