package com.izakaya.ordersystem.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;
import com.izakaya.ordersystem.model.RestaurantTable;
import com.izakaya.ordersystem.service.TableService;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" })
public class TableController {

    @Autowired
    private TableService tableService;

    /**
     * 全テーブル取得
     */
    @GetMapping
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        List<RestaurantTable> tables = tableService.getAllTables();
        return ResponseEntity.ok(tables);
    }

    /**
     * 利用可能なテーブル取得
     */
    @GetMapping("/available")
    public ResponseEntity<List<RestaurantTable>> getAvailableTables() {
        List<RestaurantTable> tables = tableService.getAvailableTables();
        return ResponseEntity.ok(tables);
    }

    /**
     * テーブル詳細取得
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTable> getTableById(@PathVariable Long id) {
        return tableService.getTableById(id)
                .map(table -> ResponseEntity.ok(table))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * テーブル番号で取得
     */
    @GetMapping("/number/{tableNumber}")
    public ResponseEntity<RestaurantTable> getTableByNumber(@PathVariable String tableNumber) {
        return tableService.getTableByNumber(tableNumber)
                .map(table -> ResponseEntity.ok(table))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * QRコードでテーブル取得
     */
    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<RestaurantTable> getTableByQrCode(@PathVariable String qrCode) {
        return tableService.getTableByQrCode(qrCode)
                .map(table -> ResponseEntity.ok(table))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 収容人数以上の利用可能テーブル検索
     */
    @GetMapping("/capacity/{capacity}")
    public ResponseEntity<List<RestaurantTable>> getAvailableTablesByCapacity(@PathVariable Integer capacity) {
        List<RestaurantTable> tables = tableService.findAvailableTablesByCapacity(capacity);
        return ResponseEntity.ok(tables);
    }

    /**
     * テーブル作成
     */
    @PostMapping
    public ResponseEntity<RestaurantTable> createTable(@RequestBody Map<String, Object> tableRequest) {
        String tableNumber = (String) tableRequest.get("tableNumber");
        Integer capacity = Integer.valueOf(tableRequest.get("capacity").toString());

        try {
            RestaurantTable table = tableService.createTable(tableNumber, capacity);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * テーブル更新
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTable> updateTable(
            @PathVariable Long id,
            @RequestBody RestaurantTable tableDetails) {
        try {
            RestaurantTable table = tableService.updateTable(id, tableDetails);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * テーブルステータス更新
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<RestaurantTable> updateTableStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusRequest) {
        try {
            RestaurantTable.TableStatus status = RestaurantTable.TableStatus.valueOf(statusRequest.get("status"));
            RestaurantTable table = tableService.updateTableStatus(id, status);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * QRコード画像取得
     */
    @GetMapping("/{tableNumber}/qr-image")
    public ResponseEntity<Map<String, String>> getQrCodeImage(@PathVariable String tableNumber) {
        try {
            String qrCodeImage = tableService.generateQrCodeImage(tableNumber);
            return ResponseEntity.ok(Map.of("qrCodeImage", qrCodeImage));
        } catch (WriterException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * テーブル削除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        try {
            tableService.deleteTable(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}