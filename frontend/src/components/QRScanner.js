import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Typography,
  Button,
  Alert,
  Paper,
  IconButton,
  Stack,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  CameraAlt as CameraIcon,
} from '@mui/icons-material';
import QrScanner from 'qr-scanner';

const QRScannerComponent = () => {
  const navigate = useNavigate();
  const videoRef = useRef(null);
  const [qrScanner, setQrScanner] = useState(null);
  const [scanning, setScanning] = useState(false);
  const [error, setError] = useState('');
  const [hasCamera, setHasCamera] = useState(true);

  useEffect(() => {
    const initializeScanner = async () => {
      try {
        // カメラの存在確認
        const hasCamera = await QrScanner.hasCamera();
        setHasCamera(hasCamera);
        
        if (!hasCamera) {
          setError('カメラが見つかりません。デバイスにカメラが接続されていることを確認してください。');
          return;
        }

        // QRスキャナーの初期化
        const scanner = new QrScanner(
          videoRef.current,
          (result) => handleScanResult(result),
          {
            onDecodeError: (err) => {
              // デコードエラーは通常の動作なので、エラーとして表示しない
              console.log('Decode error:', err);
            },
            highlightScanRegion: true,
            highlightCodeOutline: true,
          }
        );

        setQrScanner(scanner);
      } catch (err) {
        console.error('Scanner initialization error:', err);
        setError('QRスキャナーの初期化に失敗しました。');
      }
    };

    initializeScanner();

    // クリーンアップ
    return () => {
      if (qrScanner) {
        qrScanner.stop();
        qrScanner.destroy();
      }
    };
  }, []);

  const handleScanResult = (result) => {
    console.log('QR Code detected:', result);
    
    try {
      // QRコードのURLを解析
      const url = new URL(result.data);
      const tableNumber = url.searchParams.get('table');
      
      if (tableNumber) {
        // スキャン停止
        if (qrScanner) {
          qrScanner.stop();
        }
        
        // メニューページへ遷移
        navigate(`/menu/${tableNumber}`);
      } else {
        setError('無効なQRコードです。テーブルのQRコードをスキャンしてください。');
      }
    } catch (err) {
      console.error('QR code parsing error:', err);
      setError('QRコードの解析に失敗しました。正しいQRコードをスキャンしてください。');
    }
  };

  const startScanning = async () => {
    if (!qrScanner || !hasCamera) return;
    
    try {
      setError('');
      setScanning(true);
      await qrScanner.start();
    } catch (err) {
      console.error('Failed to start scanner:', err);
      setError('カメラの起動に失敗しました。カメラのアクセス許可を確認してください。');
      setScanning(false);
    }
  };

  const stopScanning = () => {
    if (qrScanner) {
      qrScanner.stop();
      setScanning(false);
    }
  };

  const handleManualEntry = () => {
    navigate('/menu');
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ py: 4 }}>
        {/* ヘッダー */}
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 4 }}>
          <IconButton onClick={() => navigate('/')} sx={{ mr: 2 }}>
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h5" component="h1" sx={{ fontWeight: 600 }}>
            QRコードスキャン
          </Typography>
        </Box>

        {/* エラー表示 */}
        {error && (
          <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError('')}>
            {error}
          </Alert>
        )}

        {/* カメラプレビュー */}
        {hasCamera && (
          <Paper sx={{ p: 2, mb: 3, textAlign: 'center' }}>
            <Box sx={{ position: 'relative', display: 'inline-block' }}>
              <video
                ref={videoRef}
                className="qr-scanner-video"
                style={{
                  width: '100%',
                  maxWidth: 400,
                  height: 'auto',
                  borderRadius: 8,
                  display: scanning ? 'block' : 'none',
                }}
              />
              
              {!scanning && (
                <Box
                  sx={{
                    width: '100%',
                    maxWidth: 400,
                    height: 300,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    bgcolor: 'grey.100',
                    borderRadius: 2,
                    border: '2px dashed',
                    borderColor: 'grey.300',
                  }}
                >
                  <CameraIcon sx={{ fontSize: 48, color: 'grey.400', mb: 2 }} />
                  <Typography variant="body2" color="text.secondary">
                    カメラを起動してQRコードをスキャン
                  </Typography>
                </Box>
              )}
            </Box>
          </Paper>
        )}

        {/* 操作ボタン */}
        <Stack spacing={2}>
          {hasCamera && (
            <>
              {!scanning ? (
                <Button
                  variant="contained"
                  size="large"
                  onClick={startScanning}
                  startIcon={<CameraIcon />}
                >
                  スキャン開始
                </Button>
              ) : (
                <Button
                  variant="outlined"
                  size="large"
                  onClick={stopScanning}
                  color="error"
                >
                  スキャン停止
                </Button>
              )}
            </>
          )}
          
          <Button
            variant="outlined"
            size="large"
            onClick={handleManualEntry}
          >
            テーブル番号を手動入力
          </Button>
          
          <Button
            variant="text"
            onClick={() => navigate('/')}
          >
            ホームに戻る
          </Button>
        </Stack>

        {/* 使用方法 */}
        <Paper sx={{ p: 3, mt: 4, bgcolor: 'info.50' }}>
          <Typography variant="h6" sx={{ mb: 2, color: 'info.main' }}>
            使用方法
          </Typography>
          <Typography variant="body2" sx={{ mb: 1 }}>
            1. 「スキャン開始」ボタンを押してカメラを起動
          </Typography>
          <Typography variant="body2" sx={{ mb: 1 }}>
            2. テーブル上のQRコードをカメラに向ける
          </Typography>
          <Typography variant="body2" sx={{ mb: 1 }}>
            3. 自動的にメニューページに移動します
          </Typography>
          <Typography variant="body2" color="text.secondary">
            ※ QRコードが読み取れない場合は「テーブル番号を手動入力」をご利用ください
          </Typography>
        </Paper>
      </Box>
    </Container>
  );
};

export default QRScannerComponent;