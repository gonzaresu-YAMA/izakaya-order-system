@echo off
chcp 65001 >nul

echo ===== 居酒屋注文システム セットアップ開始 =====

REM バックエンドセットアップ
echo 1. バックエンド（Spring Boot）のセットアップ...
cd backend

REM Mavenで依存関係をダウンロード
echo    - Maven依存関係をダウンロード中...
call mvnw.cmd dependency:resolve
if errorlevel 1 (
    echo    エラー: Maven依存関係のダウンロードに失敗しました
    pause
    exit /b 1
)

echo    - バックエンドセットアップ完了

REM フロントエンドセットアップ
echo 2. フロントエンド（React）のセットアップ...
cd ..\frontend

REM Node.js依存関係をインストール
echo    - npm依存関係をインストール中...
call npm install
if errorlevel 1 (
    echo    エラー: npm依存関係のインストールに失敗しました
    pause
    exit /b 1
)

echo    - フロントエンドセットアップ完了

REM 戻る
cd ..

echo.
echo ===== セットアップ完了 =====
echo.
echo 次の手順でアプリケーションを起動できます:
echo.
echo バックエンド起動:
echo   cd backend
echo   mvnw.cmd spring-boot:run
echo.
echo フロントエンド起動 (別のコマンドプロンプトで):
echo   cd frontend
echo   npm start
echo.
echo アクセスURL:
echo   フロントエンド: http://localhost:3000
echo   バックエンドAPI: http://localhost:8080/api
echo   H2データベース: http://localhost:8080/h2-console
echo.
pause