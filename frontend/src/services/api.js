import axios from 'axios';

// API base URL
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    console.log('API Request:', config);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    console.log('API Response:', response);
    return response;
  },
  (error) => {
    console.error('API Response Error:', error);
    if (error.response) {
      // サーバーからエラーレスポンスが返された場合
      console.error('Error Response Data:', error.response.data);
      console.error('Error Response Status:', error.response.status);
    } else if (error.request) {
      // リクエストが送信されたが、レスポンスが受信されなかった場合
      console.error('No response received:', error.request);
    } else {
      // リクエストの設定中にエラーが発生した場合
      console.error('Request setup error:', error.message);
    }
    return Promise.reject(error);
  }
);

// Menu API
export const menuAPI = {
  // 利用可能なメニュー取得
  getAvailableItems: () => api.get('/menu/available'),
  
  // カテゴリ別メニュー取得
  getItemsByCategory: (category) => api.get(`/menu/category/${category}`),
  
  // メニューアイテム詳細取得
  getItemById: (id) => api.get(`/menu/${id}`),
  
  // メニュー検索
  searchItems: (name) => api.get(`/menu/search?name=${encodeURIComponent(name)}`),
  
  // 価格範囲でメニュー検索
  getItemsByPriceRange: (minPrice, maxPrice) => 
    api.get(`/menu/price-range?minPrice=${minPrice}&maxPrice=${maxPrice}`),
};

// Table API
export const tableAPI = {
  // 全テーブル取得
  getAllTables: () => api.get('/tables'),
  
  // 利用可能なテーブル取得
  getAvailableTables: () => api.get('/tables/available'),
  
  // テーブル番号で取得
  getTableByNumber: (tableNumber) => api.get(`/tables/number/${tableNumber}`),
  
  // QRコードでテーブル取得
  getTableByQrCode: (qrCode) => api.get(`/tables/qr/${encodeURIComponent(qrCode)}`),
  
  // テーブルステータス更新
  updateTableStatus: (id, status) => 
    api.patch(`/tables/${id}/status`, { status }),
  
  // QRコード画像取得
  getQrCodeImage: (tableNumber) => api.get(`/tables/${tableNumber}/qr-image`),
};

// Order API
export const orderAPI = {
  // 全注文取得
  getAllOrders: () => api.get('/orders'),
  
  // アクティブな注文取得
  getActiveOrders: () => api.get('/orders/active'),
  
  // 厨房向け注文取得
  getOrdersForKitchen: () => api.get('/orders/kitchen'),
  
  // 注文詳細取得
  getOrderById: (id) => api.get(`/orders/${id}`),
  
  // テーブル別注文取得
  getOrdersByTable: (tableId) => api.get(`/orders/table/${tableId}`),
  
  // 今日の注文取得
  getTodaysOrders: () => api.get('/orders/today'),
  
  // 新規注文作成
  createOrder: (tableId, customerNotes = '') => 
    api.post('/orders', { tableId, customerNotes }),
  
  // 注文にアイテム追加
  addItemToOrder: (orderId, menuItemId, quantity, specialInstructions = '') =>
    api.post(`/orders/${orderId}/items`, {
      menuItemId,
      quantity,
      specialInstructions,
    }),
  
  // 注文ステータス更新
  updateOrderStatus: (orderId, status) =>
    api.patch(`/orders/${orderId}/status`, { status }),
  
  // 注文確定
  confirmOrder: (orderId) => api.patch(`/orders/${orderId}/confirm`),
  
  // 調理開始
  startPreparation: (orderId) => api.patch(`/orders/${orderId}/start-preparation`),
  
  // 配膳準備完了
  markAsReady: (orderId) => api.patch(`/orders/${orderId}/ready`),
  
  // 配膳完了
  markAsServed: (orderId) => api.patch(`/orders/${orderId}/served`),
  
  // 会計完了
  completeOrder: (orderId) => api.patch(`/orders/${orderId}/complete`),
  
  // 注文キャンセル
  cancelOrder: (orderId) => api.patch(`/orders/${orderId}/cancel`),
  
  // 注文アイテムのステータス更新
  updateOrderItemStatus: (orderId, itemId, status) =>
    api.patch(`/orders/${orderId}/items/${itemId}/status`, { status }),
};

// Receipt API
export const receiptAPI = {
  // 領収書テキスト取得
  getReceiptText: (orderId) => api.get(`/receipts/${orderId}/text`),
  
  // 領収書HTML取得
  getReceiptHtml: (orderId) => api.get(`/receipts/${orderId}/html`),
  
  // 領収書PDF取得
  getReceiptPdf: (orderId) => api.get(`/receipts/${orderId}/pdf`, {
    responseType: 'blob',
  }),
  
  // 領収書ダウンロード
  downloadReceipt: (orderId) => api.get(`/receipts/${orderId}/download`, {
    responseType: 'blob',
  }),
};

export default api;