package com.izakaya.ordersystem.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.izakaya.ordersystem.model.Order;
import com.izakaya.ordersystem.model.RestaurantTable;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * テーブル別の注文一覧取得
     */
    List<Order> findByTableOrderByCreatedAtDesc(RestaurantTable table);

    /**
     * ステータス別の注文一覧取得
     */
    List<Order> findByStatusOrderByCreatedAtAsc(Order.OrderStatus status);

    /**
     * アクティブな注文一覧取得（未完了の注文）
     */
    @Query("SELECT o FROM Order o WHERE o.status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY o.createdAt ASC")
    List<Order> findActiveOrders();

    /**
     * 指定期間の注文一覧取得
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 今日の注文一覧取得
     */
    /*
     * 今日の注文一覧取得
     * データベース依存の DATE()/CURRENT_DATE 比較で型不整合が起きるため、
     * Java 側で本日の開始/終了を作成して既存の findOrdersByDateRange を再利用します。
     */
    default List<Order> findTodaysOrders() {
        java.time.LocalDateTime start = java.time.LocalDate.now().atStartOfDay();
        java.time.LocalDateTime end = start.plusDays(1);
        return findOrdersByDateRange(start, end);
    }

    /**
     * テーブルの未完了注文取得
     */
    @Query("SELECT o FROM Order o WHERE o.table = :table AND o.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Order> findActiveOrdersByTable(@Param("table") RestaurantTable table);

    /**
     * 厨房向け：調理が必要な注文一覧
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('CONFIRMED', 'IN_PREPARATION') ORDER BY o.createdAt ASC")
    List<Order> findOrdersForKitchen();
}