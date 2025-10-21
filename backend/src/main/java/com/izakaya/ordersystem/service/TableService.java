package com.izakaya.ordersystem.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.izakaya.ordersystem.model.RestaurantTable;
import com.izakaya.ordersystem.repository.RestaurantTableRepository;

@Service
@Transactional
public class TableService {

    @Autowired
    private RestaurantTableRepository tableRepository;

    /**
     * 全テーブル取得
     */
    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    /**
     * 利用可能なテーブル取得
     */
    public List<RestaurantTable> getAvailableTables() {
        return tableRepository.findAvailableTables();
    }

    /**
     * テーブルをIDで取得
     */
    public Optional<RestaurantTable> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    /**
     * テーブル番号で取得
     */
    public Optional<RestaurantTable> getTableByNumber(String tableNumber) {
        return tableRepository.findByTableNumber(tableNumber);
    }

    /**
     * QRコードでテーブル取得
     */
    public Optional<RestaurantTable> getTableByQrCode(String qrCode) {
        return tableRepository.findByQrCode(qrCode);
    }

    /**
     * テーブル作成（QRコード自動生成）
     */
    public RestaurantTable createTable(String tableNumber, Integer capacity) {
        RestaurantTable table = new RestaurantTable(tableNumber, capacity);

        // QRコード生成
        String qrCodeData = generateQrCodeData(tableNumber);
        table.setQrCode(qrCodeData);

        return tableRepository.save(table);
    }

    /**
     * テーブル更新
     */
    public RestaurantTable updateTable(Long id, RestaurantTable tableDetails) {
        return tableRepository.findById(id)
                .map(table -> {
                    table.setTableNumber(tableDetails.getTableNumber());
                    table.setCapacity(tableDetails.getCapacity());
                    table.setStatus(tableDetails.getStatus());

                    // テーブル番号が変更された場合はQRコードを再生成
                    if (!table.getTableNumber().equals(tableDetails.getTableNumber())) {
                        String qrCodeData = generateQrCodeData(tableDetails.getTableNumber());
                        table.setQrCode(qrCodeData);
                    }

                    return tableRepository.save(table);
                })
                .orElseThrow(() -> new RuntimeException("Table not found with id " + id));
    }

    /**
     * テーブルステータス更新
     */
    public RestaurantTable updateTableStatus(Long id, RestaurantTable.TableStatus status) {
        return tableRepository.findById(id)
                .map(table -> {
                    table.setStatus(status);
                    return tableRepository.save(table);
                })
                .orElseThrow(() -> new RuntimeException("Table not found with id " + id));
    }

    /**
     * テーブル削除
     */
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    /**
     * QRコードデータ生成
     */
    private String generateQrCodeData(String tableNumber) {
        // テーブル識別用のURL生成（実際のフロントエンドURLに合わせて調整）
        return "http://localhost:3000/order?table=" + tableNumber;
    }

    /**
     * QRコード画像生成（Base64エンコード）
     */
    public String generateQrCodeImage(String tableNumber) throws WriterException, IOException {
        String qrCodeData = generateQrCodeData(tableNumber);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        byte[] qrCodeBytes = outputStream.toByteArray();

        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }

    /**
     * 収容人数以上の利用可能テーブル検索
     */
    public List<RestaurantTable> findAvailableTablesByCapacity(Integer capacity) {
        return tableRepository.findByCapacityGreaterThanEqualAndStatus(
                capacity, RestaurantTable.TableStatus.AVAILABLE);
    }
}