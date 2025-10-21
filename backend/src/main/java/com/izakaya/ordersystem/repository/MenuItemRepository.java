package com.izakaya.ordersystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.izakaya.ordersystem.model.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * カテゴリ別のメニューアイテム取得
     */
    List<MenuItem> findByCategory(MenuItem.MenuCategory category);

    /**
     * 利用可能なメニューアイテム取得
     */
    List<MenuItem> findByIsAvailableTrue();

    /**
     * カテゴリ別かつ利用可能なメニューアイテム取得
     */
    List<MenuItem> findByCategoryAndIsAvailableTrue(MenuItem.MenuCategory category);

    /**
     * 名前で検索（部分一致）
     */
    @Query("SELECT m FROM MenuItem m WHERE m.name LIKE %:name% AND m.isAvailable = true")
    List<MenuItem> findByNameContainingAndAvailable(@Param("name") String name);

    /**
     * 価格範囲でメニューアイテム検索
     */
    @Query("SELECT m FROM MenuItem m WHERE m.price BETWEEN :minPrice AND :maxPrice AND m.isAvailable = true ORDER BY m.price")
    List<MenuItem> findByPriceRangeAndAvailable(
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice);
}