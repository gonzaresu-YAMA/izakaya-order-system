package com.izakaya.ordersystem.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izakaya.ordersystem.model.Order;
import com.izakaya.ordersystem.model.OrderItem;
import com.izakaya.ordersystem.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" })
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 全注文取得
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * アクティブな注文取得
     */
    @GetMapping("/active")
    public ResponseEntity<List<Order>> getActiveOrders() {
        List<Order> orders = orderService.getActiveOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * 厨房向け注文取得
     */
    @GetMapping("/kitchen")
    public ResponseEntity<List<Order>> getOrdersForKitchen() {
        List<Order> orders = orderService.getOrdersForKitchen();
        return ResponseEntity.ok(orders);
    }

    /**
     * 注文詳細取得
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(order))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * テーブル別注文取得
     */
    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<Order>> getOrdersByTable(@PathVariable Long tableId) {
        try {
            List<Order> orders = orderService.getOrdersByTable(tableId);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 今日の注文取得
     */
    @GetMapping("/today")
    public ResponseEntity<List<Order>> getTodaysOrders() {
        List<Order> orders = orderService.getTodaysOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * 新規注文作成
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> orderRequest) {
        Long tableId = Long.valueOf(orderRequest.get("tableId").toString());
        String customerNotes = (String) orderRequest.get("customerNotes");

        try {
            Order order = orderService.createOrder(tableId, customerNotes);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 注文にアイテム追加
     */
    @PostMapping("/{orderId}/items")
    public ResponseEntity<Order> addItemToOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> itemRequest) {

        Long menuItemId = Long.valueOf(itemRequest.get("menuItemId").toString());
        Integer quantity = Integer.valueOf(itemRequest.get("quantity").toString());
        String specialInstructions = (String) itemRequest.get("specialInstructions");

        try {
            Order order = orderService.addItemToOrder(orderId, menuItemId, quantity, specialInstructions);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 注文ステータス更新
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> statusRequest) {

        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(statusRequest.get("status"));
            Order order = orderService.updateOrderStatus(orderId, newStatus);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 注文確定
     */
    @PatchMapping("/{orderId}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.confirmOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 調理開始
     */
    @PatchMapping("/{orderId}/start-preparation")
    public ResponseEntity<Order> startPreparation(@PathVariable Long orderId) {
        try {
            Order order = orderService.startPreparation(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 配膳準備完了
     */
    @PatchMapping("/{orderId}/ready")
    public ResponseEntity<Order> markAsReady(@PathVariable Long orderId) {
        try {
            Order order = orderService.markAsReady(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 配膳完了
     */
    @PatchMapping("/{orderId}/served")
    public ResponseEntity<Order> markAsServed(@PathVariable Long orderId) {
        try {
            Order order = orderService.markAsServed(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 会計完了
     */
    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<Order> completeOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.completeOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 注文キャンセル
     */
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 注文アイテムのステータス更新
     */
    @PatchMapping("/{orderId}/items/{itemId}/status")
    public ResponseEntity<Order> updateOrderItemStatus(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestBody Map<String, String> statusRequest) {

        try {
            OrderItem.ItemStatus newStatus = OrderItem.ItemStatus.valueOf(statusRequest.get("status"));
            Order order = orderService.updateOrderItemStatus(orderId, itemId, newStatus);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}