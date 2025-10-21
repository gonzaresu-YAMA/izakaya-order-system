#!/bin/bash

# 居酒屋注文システム セットアップスクリプト

echo "=== 居酒屋注文システム セットアップ開始 ==="

# バックエンドセットアップ
echo "1. バックエンド（Spring Boot）のセットアップ..."
cd backend

# Mavenで依存関係をダウンロード
echo "   - Maven依存関係をダウンロード中..."
./mvnw dependency:resolve

echo "   - バックエンドセットアップ完了"

# フロントエンドセットアップ
echo "2. フロントエンド（React）のセットアップ..."
cd ../frontend

# Node.js依存関係をインストール
echo "   - npm依存関係をインストール中..."
npm install

echo "   - フロントエンドセットアップ完了"

# 戻る
cd ..

echo ""
echo "=== セットアップ完了 ==="
echo ""
echo "次の手順でアプリケーションを起動できます:"
echo ""
echo "バックエンド起動:"
echo "  cd backend"
echo "  ./mvnw spring-boot:run"
echo ""
echo "フロントエンド起動 (別のターミナルで):"
echo "  cd frontend" 
echo "  npm start"
echo ""
echo "アクセスURL:"
echo "  フロントエンド: http://localhost:3000"
echo "  バックエンドAPI: http://localhost:8080/api"
echo "  H2データベース: http://localhost:8080/h2-console"
echo ""