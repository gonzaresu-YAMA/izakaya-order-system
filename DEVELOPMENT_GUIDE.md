# 居酒屋注文システム 開発・運用ガイド

## システム概要

### アーキテクチャ
- **バックエンド**: Java 17 + Spring Boot 3.2.0
- **フロントエンド**: React 18 + Material-UI
- **データベース**: H2 (開発用) / PostgreSQL (本番用)
- **通信**: REST API + WebSocket

### 主要機能
1. **顧客向け機能**
   - QRコードスキャンによるテーブル認証
   - メニュー閲覧・検索
   - 注文カート機能
   - 注文送信

2. **厨房向け機能**
   - リアルタイム注文受信
   - 注文ステータス管理
   - 調理進捗管理

3. **管理機能**
   - メニュー管理
   - テーブル管理
   - 売上レポート
   - 領収書印刷

## セットアップ手順

### 前提条件
- Java 17以上
- Node.js 18以上
- Git

### 初回セットアップ
```bash
# 1. リポジトリのクローン
git clone <repository-url>
cd izakaya-order-system

# 2. Windowsの場合
setup.bat

# 3. Linux/Macの場合
chmod +x setup.sh
./setup.sh
```

### 開発環境起動

#### バックエンド起動
```bash
cd backend
./mvnw spring-boot:run

# Windowsの場合
mvnw.cmd spring-boot:run
```

#### フロントエンド起動（別ターミナル）
```bash
cd frontend
npm start
```

### アクセスURL
- **フロントエンド**: http://localhost:3000
- **バックエンドAPI**: http://localhost:8080/api
- **H2データベース**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - ユーザー名: `sa`
  - パスワード: `password`

## API仕様

### メニューAPI
```
GET    /api/menu/available          # 利用可能なメニュー取得
GET    /api/menu/category/{category} # カテゴリ別メニュー取得
GET    /api/menu/{id}               # メニュー詳細取得
POST   /api/menu                    # メニュー作成（管理者用）
PUT    /api/menu/{id}               # メニュー更新（管理者用）
DELETE /api/menu/{id}               # メニュー削除（管理者用）
```

### 注文API
```
GET    /api/orders                  # 全注文取得
GET    /api/orders/active           # アクティブ注文取得
GET    /api/orders/kitchen          # 厨房向け注文取得
POST   /api/orders                  # 新規注文作成
POST   /api/orders/{id}/items       # 注文アイテム追加
PATCH  /api/orders/{id}/status      # 注文ステータス更新
PATCH  /api/orders/{id}/confirm     # 注文確定
```

### テーブルAPI
```
GET    /api/tables                  # 全テーブル取得
GET    /api/tables/available        # 利用可能テーブル取得
GET    /api/tables/number/{number}  # テーブル番号で取得
POST   /api/tables                  # テーブル作成
PATCH  /api/tables/{id}/status      # テーブルステータス更新
```

### 領収書API
```
GET    /api/receipts/{orderId}/text # 領収書テキスト取得
GET    /api/receipts/{orderId}/html # 領収書HTML取得
GET    /api/receipts/{orderId}/pdf  # 領収書PDF取得
```

## データベース設計

### 主要テーブル
1. **restaurant_tables** - テーブル情報
2. **menu_items** - メニューアイテム
3. **orders** - 注文情報
4. **order_items** - 注文アイテム詳細

### サンプルデータ
初回起動時に以下のデータが自動作成されます：
- テーブル1-8番（1-4番は4人用、5-8番は6人用）
- 各カテゴリのサンプルメニュー（前菜、刺身、焼き物等）

## フロントエンド構成

### 主要コンポーネント
```
src/
├── components/
│   ├── HomePage.js          # ホームページ
│   ├── QRScanner.js         # QRスキャナー
│   ├── MenuPage.js          # メニュー表示
│   ├── OrderCart.js         # 注文カート
│   ├── KitchenDashboard.js  # 厨房画面
│   ├── AdminDashboard.js    # 管理画面
│   └── ReceiptPage.js       # 領収書表示
├── services/
│   └── api.js               # API通信
└── App.js                   # メインアプリケーション
```

### ルーティング
```
/                 # ホームページ
/scan             # QRスキャン
/menu/:tableNumber# メニュー表示
/cart             # 注文カート
/kitchen          # 厨房画面
/admin            # 管理画面
/receipt/:orderId # 領収書表示
```

## 運用ガイド

### QRコード生成
テーブル作成時に自動でQRコードが生成されます。
QRコードには以下のURL形式が含まれます：
```
http://localhost:3000/menu?table={tableNumber}
```

### 注文フロー
1. 顧客がQRコードをスキャン
2. メニューページでアイテムを選択
3. カートで注文内容を確認
4. 注文送信
5. 厨房でリアルタイム受信
6. 調理・配膳
7. 会計・領収書印刷

### 領収書印刷
- HTML形式での印刷対応
- PDF生成機能（要拡張）
- レシートプリンター対応（要実装）

## トラブルシューティング

### よくある問題

#### 1. バックエンドが起動しない
```bash
# Javaバージョン確認
java -version

# 必要に応じてJAVA_HOMEを設定
export JAVA_HOME=/path/to/jdk-17
```

#### 2. フロントエンドが起動しない
```bash
# Node.jsバージョン確認
node --version

# 依存関係の再インストール
rm -rf node_modules package-lock.json
npm install
```

#### 3. CORSエラー
プロキシ設定を確認：
```json
// package.json
"proxy": "http://localhost:8080"
```

#### 4. データベース接続エラー
H2コンソールで接続情報を確認：
- URL: `jdbc:h2:mem:testdb`
- ユーザー: `sa`
- パスワード: `password`

### ログ確認
```bash
# バックエンドログ
tail -f backend/logs/application.log

# フロントエンド開発サーバーログ
# ターミナル出力を確認
```

## 本番環境デプロイ

### 環境変数設定
```bash
# データベース設定
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/izakaya
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=password

# 本番用プロファイル
export SPRING_PROFILES_ACTIVE=production
```

### ビルド・デプロイ
```bash
# バックエンドビルド
cd backend
./mvnw clean package

# フロントエンドビルド
cd frontend
npm run build

# 本番環境起動
java -jar backend/target/order-system-1.0.0.jar
```

## セキュリティ考慮事項

### 実装予定
- JWT認証（管理者機能用）
- HTTPS対応
- SQL Injectionガード
- XSS対策
- CSRF保護

## パフォーマンス最適化

### 実装済み
- データベースインデックス
- React.memoでコンポーネント最適化
- 画像遅延読み込み

### 今後の改善点
- Redis キャッシュ導入
- CDN利用
- データベースコネクションプール調整

## 監視・メトリクス

### Spring Boot Actuator
```bash
# ヘルスチェック
curl http://localhost:8080/actuator/health

# メトリクス
curl http://localhost:8080/actuator/metrics
```

### ログ監視
- アプリケーションログ
- アクセスログ  
- エラーログ
- パフォーマンスログ

## 今後の機能拡張

### 短期計画
- [ ] 会員登録・ログイン機能
- [ ] 注文履歴機能
- [ ] プッシュ通知
- [ ] 多言語対応

### 中期計画
- [ ] 予約システム
- [ ] ポイント機能
- [ ] クーポン機能
- [ ] 売上分析ダッシュボード

### 長期計画
- [ ] マルチテナント対応
- [ ] モバイルアプリ
- [ ] 配達機能
- [ ] AI推奨システム

## サポート

### 開発者連絡先
- プロジェクト管理者: [連絡先]
- 技術サポート: [連絡先]

### ドキュメント
- APIドキュメント: http://localhost:8080/swagger-ui.html
- コード規約: [リンク]
- テスト手順: [リンク]