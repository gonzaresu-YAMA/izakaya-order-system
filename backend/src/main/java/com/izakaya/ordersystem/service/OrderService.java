package com.izakaya.ordersystem.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.izakaya.ordersystem.model.MenuItem;
import com.izakaya.ordersystem.model.Order;
import com.izakaya.ordersystem.model.OrderItem;
import com.izakaya.ordersystem.model.RestaurantTable;
import com.izakaya.ordersystem.repository.MenuItemRepository;
import com.izakaya.ordersystem.repository.OrderRepository;
import com.izakaya.ordersystem.repository.RestaurantTableRepository;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantTableRepository tableRepository;

    /**
     * 全注文取得
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * アクティブな注文取得
     */
    public List<Order> getActiveOrders() {
        return orderRepository.findActiveOrders();
    }

    /**
     * 厨房向け注文取得
     */
    public List<Order> getOrdersForKitchen() {
        return orderRepository.findOrdersForKitchen();
    }

    /**
     * 注文をIDで取得
     */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * テーブル別注文取得
     */
    public List<Order> getOrdersByTable(Long tableId) {
        return tableRepository.findById(tableId)
                .map(table -> orderRepository.findByTableOrderByCreatedAtDesc(table))
                .orElseThrow(() -> new RuntimeException("Table not found with id " + tableId));
    }

    /**
     * 新規注文作成
     */
    public Order createOrder(Long tableId, String customerNotes) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with id " + tableId));

        Order order = new Order(table);
        order.setCustomerNotes(customerNotes);

        return orderRepository.save(order);
    }

    /**
     * 注文にアイテム追加
     */
    public Order addItemToOrder(Long orderId, Long menuItemId, Integer quantity, String specialInstructions) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id " + menuItemId));

        if (!menuItem.getIsAvailable()) {
            throw new RuntimeException("Menu item is not available: " + menuItem.getName());
        }

        OrderItem orderItem = new OrderItem(order, menuItem, quantity, specialInstructions);
        order.addOrderItem(orderItem);

        return orderRepository.save(order);
    }

    /**
     * 注文ステータス更新
     */
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(newStatus);

                    // テーブルステータスも更新
                    if (newStatus == Order.OrderStatus.COMPLETED) {
                        order.getTable().setStatus(RestaurantTable.TableStatus.AVAILABLE);
                    } else if (newStatus == Order.OrderStatus.CONFIRMED) {
                        order.getTable().setStatus(RestaurantTable.TableStatus.OCCUPIED);
                    }

                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));
    }

    /**
     * 注文アイテムのステータス更新
     */
    public Order updateOrderItemStatus(Long orderId, Long orderItemId, OrderItem.ItemStatus newStatus) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    OrderItem orderItem = order.getOrderItems().stream()
                            .filter(item -> item.getId().equals(orderItemId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Order item not found with id " + orderItemId));

                    orderItem.setStatus(newStatus);
                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));
    }

    /**
     * 注文確定（ステータスをPENDING → CONFIRMED）
     */
    public Order confirmOrder(Long orderId) {
        return updateOrderStatus(orderId, Order.OrderStatus.CONFIRMED);
    }

    /**
     * 調理開始（ステータスをCONFIRMED → IN_PREPARATION）
     */
    public Order startPreparation(Long orderId) {
        return updateOrderStatus(orderId, Order.OrderStatus.IN_PREPARATION);
    }

    /**
     * 配膳準備完了（ステータスをIN_PREPARATION → READY）
     */
    public Order markAsReady(Long orderId) {
        return updateOrderStatus(orderId, Order.OrderStatus.READY);
    }

    /**
     * 配膳完了（ステータスをREADY → SERVED）
     */
    public Order markAsServed(Long orderId) {
        return updateOrderStatus(orderId, Order.OrderStatus.SERVED);
    }

    /**
     * 会計完了（ステータスをSERVED → COMPLETED）
     */
    public Order completeOrder(Long orderId) {
        return updateOrderStatus(orderId, Order.OrderStatus.COMPLETED);
    }

    /**
     * 注文キャンセル
     */
    public Order cancelOrder(Long orderId) {
        return updateOrderStatus(orderId, Order.OrderStatus.CANCELLED);
    }

    /**
     * 今日の注文取得
     */
    public List<Order> getTodaysOrders() {
        return orderRepository.findTodaysOrders();
    }

    /**
     * 指定期間の注文取得
     */
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrdersByDateRange(startDate, endDate);
    }
}