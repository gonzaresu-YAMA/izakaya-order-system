package com.izakaya.ordersystem.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.izakaya.ordersystem.model.MenuItem;
import com.izakaya.ordersystem.repository.MenuItemRepository;

@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * 全メニューアイテム取得
     */
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    /**
     * 利用可能なメニューアイテム取得
     */
    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByIsAvailableTrue();
    }

    /**
     * カテゴリ別メニューアイテム取得
     */
    public List<MenuItem> getMenuItemsByCategory(MenuItem.MenuCategory category) {
        return menuItemRepository.findByCategoryAndIsAvailableTrue(category);
    }

    /**
     * メニューアイテムをIDで取得
     */
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    /**
     * 名前で検索
     */
    public List<MenuItem> searchMenuItemsByName(String name) {
        return menuItemRepository.findByNameContainingAndAvailable(name);
    }

    /**
     * 価格範囲で検索
     */
    public List<MenuItem> getMenuItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return menuItemRepository.findByPriceRangeAndAvailable(minPrice, maxPrice);
    }

    /**
     * メニューアイテム作成
     */
    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    /**
     * メニューアイテム更新
     */
    public MenuItem updateMenuItem(Long id, MenuItem menuItemDetails) {
        return menuItemRepository.findById(id)
                .map(menuItem -> {
                    menuItem.setName(menuItemDetails.getName());
                    menuItem.setDescription(menuItemDetails.getDescription());
                    menuItem.setPrice(menuItemDetails.getPrice());
                    menuItem.setCategory(menuItemDetails.getCategory());
                    menuItem.setImageUrl(menuItemDetails.getImageUrl());
                    menuItem.setIsAvailable(menuItemDetails.getIsAvailable());
                    menuItem.setPreparationTimeMinutes(menuItemDetails.getPreparationTimeMinutes());
                    return menuItemRepository.save(menuItem);
                })
                .orElseThrow(() -> new RuntimeException("Menu item not found with id " + id));
    }

    /**
     * メニューアイテム削除
     */
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    /**
     * メニューアイテムの可用性切り替え
     */
    public MenuItem toggleAvailability(Long id) {
        return menuItemRepository.findById(id)
                .map(menuItem -> {
                    menuItem.setIsAvailable(!menuItem.getIsAvailable());
                    return menuItemRepository.save(menuItem);
                })
                .orElseThrow(() -> new RuntimeException("Menu item not found with id " + id));
    }
}