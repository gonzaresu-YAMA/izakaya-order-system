# 居酒屋注文システム (Izakaya Order System) 

## システム概要
QRコードを使用した居酒屋の注文システムです。顧客はQRコードをスキャンして注文を行い、注文情報はリアルタイムで厨房に送信されます。会計時には領収書が自動生成・印刷されます。

##  クイックスタート

### 自動セットアップ（推奨）
```bash
# Windows
setup.bat

# Linux/Mac
chmod +x setup.sh && ./setup.sh
```

### 手動セットアップ
```bash
# 1. バックエンド起動
cd backend
./mvnw spring-boot:run

# 2. フロントエンド起動（別ターミナル）
cd frontend
npm install && npm start
```

### アクセスURL
- **顧客画面**: http://localhost:3000
- **厨房画面**: http://localhost:3000/kitchen  
- **管理画面**: http://localhost:3000/admin
- **API**: http://localhost:8080/api
- **データベース**: http://localhost:8080/h2-console

##  アーキテクチャ
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React SPA     │    │  Spring Boot    │    │   H2 Database   │
│  (Frontend)     │◄──►│   (Backend)     │◄──►│   (In-Memory)   │
│  Port: 3000     │    │   Port: 8080    │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
        │                       │
        └───────WebSocket────────┘
          (リアルタイム通信)
```

##  主要機能

###  顧客向け機能
-  QRコードスキャンによるテーブル認証
-  カテゴリ別メニュー表示・閲覧
-  商品検索機能
-  注文カート機能
-  特別な指示入力
-  注文送信

###  厨房向け機能  
-  リアルタイム注文受信
-  注文ステータス管理
-  調理進捗管理
-  テーブル別注文表示

###  会計機能
-  注文合計金額自動計算
-  領収書HTML生成
-  領収書印刷対応
-  税込み価格表示

###  管理機能
-  メニュー管理（CRUD）
-  テーブル管理
-  QRコード自動生成
-  売上データ表示

##  画面フロー

```
[ホーム] → [QRスキャン] → [メニュー選択] → [カート] → [注文完了]
                                                         ↓
[領収書] ← [会計] ← [配膳完了] ← [調理完了] ← [厨房画面]
```

##  技術スタック

### バックエンド
- **Java 17** - プログラミング言語
- **Spring Boot 3.2** - アプリケーションフレームワーク
- **Spring Data JPA** - データアクセス
- **Spring WebSocket** - リアルタイム通信
- **H2 Database** - インメモリデータベース
- **Maven** - ビルドツール

### フロントエンド  
- **React 18** - UIフレームワーク
- **Material-UI** - UIコンポーネント
- **React Router** - ルーティング
- **Axios** - HTTP通信
- **QR Scanner** - QRコード読取
- **Socket.IO Client** - WebSocket通信

##  データベース設計

### 主要エンティティ
```sql
RestaurantTable (テーブル)
├── id, tableNumber, capacity
├── qrCode, status
└── createdAt, updatedAt

MenuItem (メニューアイテム)  
├── id, name, description
├── price, category, imageUrl
├── isAvailable, preparationTimeMinutes
└── createdAt, updatedAt

Order (注文)
├── id, tableId, totalAmount
├── status, customerNotes
├── orderTime, completedTime
└── createdAt, updatedAt

OrderItem (注文アイテム)
├── id, orderId, menuItemId
├── quantity, specialInstructions
├── status
└── createdAt, updatedAt
```

##  使用方法

### 1. 顧客の注文手順
1. テーブルのQRコードをスマートフォンでスキャン
2. メニューからアイテムを選択してカートに追加
3. 特別な指示があれば入力
4. 注文内容を確認して送信

### 2. 厨房での対応手順
1. 厨房画面でリアルタイム注文を確認
2. 「調理開始」ボタンで調理ステータスに変更
3. 調理完了後「配膳準備完了」ボタンをクリック
4. 配膳後「配膳完了」ボタンで完了

### 3. 会計手順
1. 注文完了後「会計完了」ボタンをクリック  
2. 領収書ページで内容を確認
3. 印刷ボタンで領収書を印刷

## 🔧 開発者向け情報

### プロジェクト構造
```
izakaya-order-system/
├── backend/                 # Spring Boot プロジェクト
│   ├── src/main/java/
│   │   └── com/izakaya/ordersystem/
│   │       ├── model/       # エンティティクラス
│   │       ├── repository/  # データアクセス層
│   │       ├── service/     # ビジネスロジック層
│   │       ├── controller/  # REST コントローラー
│   │       └── config/      # 設定クラス
│   └── pom.xml             # Maven設定
├── frontend/               # React プロジェクト  
│   ├── src/
│   │   ├── components/     # Reactコンポーネント
│   │   ├── services/       # API通信
│   │   └── App.js         # メインアプリ
│   └── package.json       # npm設定
├── setup.bat              # Windows用セットアップ
├── setup.sh               # Linux/Mac用セットアップ
└── DEVELOPMENT_GUIDE.md   # 詳細な開発ガイド
```

### API エンドポイント
- `GET /api/menu/available` - 利用可能なメニュー取得
- `POST /api/orders` - 新規注文作成
- `PATCH /api/orders/{id}/status` - 注文ステータス更新
- `GET /api/receipts/{orderId}/html` - 領収書HTML取得

### 環境変数
```bash
# 開発環境（デフォルト）
SPRING_PROFILES_ACTIVE=development
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb

# 本番環境
SPRING_PROFILES_ACTIVE=production  
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/izakaya
```

##  トラブルシューティング

### よくある問題
1. **ポート競合**: 8080/3000ポートが使用済みの場合は別のポートを使用
2. **CORS エラー**: フロントエンドの proxy 設定を確認
3. **Java バージョン**: Java 17以上が必要
4. **Node バージョン**: Node.js 18以上が必要

### ログ確認
```bash
# バックエンドログ
tail -f backend/logs/spring.log

# データベースコンソール
http://localhost:8080/h2-console
```

##  本番環境デプロイ

### Docker対応（予定）
```bash
# 全体ビルド  
docker-compose build

# 本番起動
docker-compose up -d
```

### パフォーマンス最適化
- Redis キャッシュ導入
- データベース最適化
- CDN利用
- 画像最適化

##  コントリビュート

### 開発参加手順
1. フォークしてクローン
2. フィーチャーブランチ作成
3. 変更をコミット
4. プルリクエスト作成

### コード規約
- Java: Google Java Style Guide
- JavaScript: ESLint + Prettier
- コミット: Conventional Commits

# 追記  
今回のプロジェクトは、勉強用に作成されたものであり、実際の商用利用には適していない可能性があります。実際の運用を検討される場合は、セキュリティやスケーラビリティの観点から十分な検討とテストを行ってください。

## ローカルでPostgreSQLをDockerで起動する

開発環境でH2の代わりにPostgreSQLを使いたい場合は、ルートにある `docker-compose.yml` を使ってDBを立ち上げられます。

手順:

```bash
# ルートで docker-compose を起動
docker-compose up -d
```

デフォルトで作成される接続情報:

- ホスト: localhost
- ポート: 5432
- データベース: izakaya_db
- ユーザー: izakaya
- パスワード: izakaya_pass

バックエンド(Spring Boot)をPostgresに接続するには、環境変数を設定します。例 (PowerShell):

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/izakaya_db'
$env:SPRING_DATASOURCE_USERNAME = 'izakaya'
$env:SPRING_DATASOURCE_PASSWORD = 'izakaya_pass'
# その後、backend を起動
cd backend
.\mvnw.cmd spring-boot:run
```

注意:
- 初回起動時は `spring.jpa.hibernate.ddl-auto` が `update` に設定されているため、アプリがテーブルを自動作成します。プロダクションでは適切なマイグレーション戦略を採用してください。
- `docker-compose.yml` のパスワードはサンプル用途のため、本番では環境変数またはシークレット管理を使用してください。