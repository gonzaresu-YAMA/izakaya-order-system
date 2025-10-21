package com.izakaya.ordersystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.izakaya.ordersystem.model.RestaurantTable;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    /**
     * テーブル番号で検索
     */
    Optional<RestaurantTable> findByTableNumber(String tableNumber);

    /**
     * QRコードで検索
     */
    Optional<RestaurantTable> findByQrCode(String qrCode);

    /**
     * ステータス別のテーブル一覧取得
     */
    List<RestaurantTable> findByStatus(RestaurantTable.TableStatus status);

    /**
     * 利用可能なテーブル一覧取得
     */
    @Query("SELECT t FROM RestaurantTable t WHERE t.status = 'AVAILABLE'")
    List<RestaurantTable> findAvailableTables();

    /**
     * 収容人数以上のテーブル検索
     */
    List<RestaurantTable> findByCapacityGreaterThanEqualAndStatus(
            Integer capacity, RestaurantTable.TableStatus status);
}