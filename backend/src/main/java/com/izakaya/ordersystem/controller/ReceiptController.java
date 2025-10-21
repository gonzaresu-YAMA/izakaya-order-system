package com.izakaya.ordersystem.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izakaya.ordersystem.service.OrderService;
import com.izakaya.ordersystem.service.ReceiptService;

@RestController
@RequestMapping("/api/receipts")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" })
public class ReceiptController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReceiptService receiptService;

    /**
     * 領収書テキスト取得
     */
    @GetMapping("/{orderId}/text")
    public ResponseEntity<Map<String, String>> getReceiptText(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(order -> {
                    String receiptText = receiptService.generateReceiptText(order);
                    return ResponseEntity.ok(Map.of("receiptText", receiptText));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 領収書HTML取得（印刷用）
     */
    @GetMapping("/{orderId}/html")
    public ResponseEntity<String> getReceiptHtml(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(order -> {
                    String receiptHtml = receiptService.generateReceiptHtml(order);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.TEXT_HTML);
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(receiptHtml);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 領収書PDF取得
     */
    @GetMapping("/{orderId}/pdf")
    public ResponseEntity<byte[]> getReceiptPdf(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(order -> {
                    byte[] pdfBytes = receiptService.generateReceiptPdf(order);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("inline", "receipt_" + orderId + ".pdf");
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(pdfBytes);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 領収書ダウンロード（PDF）
     */
    @GetMapping("/{orderId}/download")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(order -> {
                    byte[] pdfBytes = receiptService.generateReceiptPdf(order);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "receipt_" + orderId + ".pdf");
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(pdfBytes);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}