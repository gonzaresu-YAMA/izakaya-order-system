package com.izakaya.ordersystem.service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.izakaya.ordersystem.model.Order;
import com.izakaya.ordersystem.model.OrderItem;

@Service
public class ReceiptService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.JAPAN);

    /**
     * 領収書テキスト生成
     */
    public String generateReceiptText(Order order) {
        StringBuilder receipt = new StringBuilder();

        // ヘッダー
        receipt.append("=".repeat(40)).append("\n");
        receipt.append("           居酒屋「さくら亭」\n");
        receipt.append("=".repeat(40)).append("\n");
        receipt.append("領収書\n\n");

        // 注文情報
        receipt.append("注文番号: ").append(String.format("%06d", order.getId())).append("\n");
        receipt.append("テーブル: ").append(order.getTable().getTableNumber()).append("\n");
        receipt.append("日時: ").append(order.getOrderTime().format(DATE_FORMATTER)).append("\n");
        receipt.append("-".repeat(40)).append("\n");

        // 注文アイテム
        receipt.append("商品名                    数量    金額\n");
        receipt.append("-".repeat(40)).append("\n");

        for (OrderItem item : order.getOrderItems()) {
            String itemName = item.getMenuItem().getName();
            if (itemName.length() > 20) {
                itemName = itemName.substring(0, 17) + "...";
            }

            String quantity = String.valueOf(item.getQuantity());
            String price = CURRENCY_FORMATTER.format(
                    item.getMenuItem().getPrice().multiply(
                            java.math.BigDecimal.valueOf(item.getQuantity())));

            receipt.append(String.format("%-20s %4s %8s\n", itemName, quantity, price));

            // 特別指示がある場合
            if (item.getSpecialInstructions() != null && !item.getSpecialInstructions().isEmpty()) {
                receipt.append("  ※").append(item.getSpecialInstructions()).append("\n");
            }
        }

        receipt.append("-".repeat(40)).append("\n");

        // 合計
        receipt.append(String.format("小計: %26s\n", CURRENCY_FORMATTER.format(order.getTotalAmount())));
        receipt.append(String.format("消費税(10%%): %20s\n",
                CURRENCY_FORMATTER.format(order.getTotalAmount().multiply(java.math.BigDecimal.valueOf(0.1)))));
        receipt.append(String.format("合計: %26s\n",
                CURRENCY_FORMATTER.format(order.getTotalAmount().multiply(java.math.BigDecimal.valueOf(1.1)))));

        receipt.append("=".repeat(40)).append("\n");

        // フッター
        if (order.getCustomerNotes() != null && !order.getCustomerNotes().isEmpty()) {
            receipt.append("お客様メモ: ").append(order.getCustomerNotes()).append("\n");
            receipt.append("-".repeat(40)).append("\n");
        }

        receipt.append("ご利用ありがとうございました！\n");
        receipt.append("またのお越しをお待ちしております。\n\n");

        receipt.append("住所: 〒100-0001 東京都千代田区千代田1-1\n");
        receipt.append("電話: 03-1234-5678\n");
        receipt.append("=".repeat(40)).append("\n");

        return receipt.toString();
    }

    /**
     * 領収書PDF生成（簡易版）
     */
    public byte[] generateReceiptPdf(Order order) {
        // 実際のPDF生成はiTextライブラリを使用
        // ここでは簡易的にテキスト版をバイト配列として返す
        String receiptText = generateReceiptText(order);
        return receiptText.getBytes();
    }

    /**
     * 領収書HTML生成（印刷用）
     */
    public String generateReceiptHtml(Order order) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>領収書</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: 'MS Gothic', monospace; font-size: 12px; }\n");
        html.append(".receipt { width: 300px; margin: 0 auto; }\n");
        html.append(".header { text-align: center; border-bottom: 2px solid #000; padding: 10px 0; }\n");
        html.append(".title { font-size: 16px; font-weight: bold; }\n");
        html.append(".info { margin: 10px 0; }\n");
        html.append(".items { border-top: 1px solid #000; border-bottom: 1px solid #000; }\n");
        html.append(".item { display: flex; justify-content: space-between; padding: 2px 0; }\n");
        html.append(".total { text-align: right; margin: 10px 0; }\n");
        html.append(".footer { text-align: center; margin-top: 20px; font-size: 10px; }\n");
        html.append("@media print { body { -webkit-print-color-adjust: exact; } }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");

        html.append("<div class='receipt'>\n");

        // ヘッダー
        html.append("<div class='header'>\n");
        html.append("<div class='title'>居酒屋「さくら亭」</div>\n");
        html.append("<div>領収書</div>\n");
        html.append("</div>\n");

        // 注文情報
        html.append("<div class='info'>\n");
        html.append("<div>注文番号: ").append(String.format("%06d", order.getId())).append("</div>\n");
        html.append("<div>テーブル: ").append(order.getTable().getTableNumber()).append("</div>\n");
        html.append("<div>日時: ").append(order.getOrderTime().format(DATE_FORMATTER)).append("</div>\n");
        html.append("</div>\n");

        // 注文アイテム
        html.append("<div class='items'>\n");
        for (OrderItem item : order.getOrderItems()) {
            html.append("<div class='item'>\n");
            html.append("<span>").append(item.getMenuItem().getName()).append(" x").append(item.getQuantity())
                    .append("</span>\n");
            html.append("<span>").append(CURRENCY_FORMATTER.format(
                    item.getMenuItem().getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity()))))
                    .append("</span>\n");
            html.append("</div>\n");

            if (item.getSpecialInstructions() != null && !item.getSpecialInstructions().isEmpty()) {
                html.append("<div style='font-size: 10px; color: #666; margin-left: 10px;'>※")
                        .append(item.getSpecialInstructions()).append("</div>\n");
            }
        }
        html.append("</div>\n");

        // 合計
        html.append("<div class='total'>\n");
        html.append("<div>小計: ").append(CURRENCY_FORMATTER.format(order.getTotalAmount())).append("</div>\n");
        html.append("<div>消費税(10%): ").append(CURRENCY_FORMATTER.format(
                order.getTotalAmount().multiply(java.math.BigDecimal.valueOf(0.1)))).append("</div>\n");
        html.append("<div style='font-weight: bold; border-top: 1px solid #000; padding-top: 5px;'>合計: ")
                .append(CURRENCY_FORMATTER.format(
                        order.getTotalAmount().multiply(java.math.BigDecimal.valueOf(1.1))))
                .append("</div>\n");
        html.append("</div>\n");

        // フッター
        html.append("<div class='footer'>\n");
        html.append("<div>ご利用ありがとうございました！</div>\n");
        html.append("<div>またのお越しをお待ちしております。</div>\n");
        html.append("<br>\n");
        html.append("<div>住所: 〒100-0001 東京都千代田区千代田1-1</div>\n");
        html.append("<div>電話: 03-1234-5678</div>\n");
        html.append("</div>\n");

        html.append("</div>\n");
        html.append("</body>\n</html>");

        return html.toString();
    }
}