package com.izakaya.ordersystem.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.izakaya.ordersystem.model.MenuItem;
import com.izakaya.ordersystem.repository.MenuItemRepository;
import com.izakaya.ordersystem.repository.RestaurantTableRepository;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantTableRepository tableRepository;

    @Autowired
    private TableService tableService;

    @Override
    public void run(String... args) {
        initializeTables();
        initializeMenuItems();
    }

    private void initializeTables() {
        // 既にテーブルが存在する場合はスキップ
        if (tableRepository.count() > 0) {
            return;
        }

        // テーブルを作成（1番から8番まで）
        for (int i = 1; i <= 8; i++) {
            String tableNumber = String.valueOf(i);
            Integer capacity = i <= 4 ? 4 : 6; // 1-4番は4人用、5-8番は6人用

            tableService.createTable(tableNumber, capacity);
        }

        System.out.println("テーブルデータを初期化しました。");
    }

    private void initializeMenuItems() {
        // 既にメニューが存在する場合はスキップ
        if (menuItemRepository.count() > 0) {
            return;
        }

        List<MenuItem> menuItems = Arrays.asList(
                // 前菜
                new MenuItem("枝豆", "茹でたての枝豆。塩味で仕上げました。", new BigDecimal("380"), MenuItem.MenuCategory.APPETIZER),
                new MenuItem("冷奴", "絹豆腐の冷奴。薬味とだし醤油でどうぞ。", new BigDecimal("480"), MenuItem.MenuCategory.APPETIZER),
                new MenuItem("もろきゅう", "もろみ味噌と新鮮きゅうりの組み合わせ。", new BigDecimal("450"), MenuItem.MenuCategory.APPETIZER),
                new MenuItem("チャンジャ", "辛口のタラ内臓塩辛。お酒のお供に。", new BigDecimal("650"), MenuItem.MenuCategory.APPETIZER),

                // 刺身
                new MenuItem("お造り盛り合わせ", "本日のおすすめ5種盛り。", new BigDecimal("1680"), MenuItem.MenuCategory.SASHIMI),
                new MenuItem("マグロ赤身", "新鮮なマグロの赤身。", new BigDecimal("980"), MenuItem.MenuCategory.SASHIMI),
                new MenuItem("サーモン", "脂ののったサーモン。", new BigDecimal("880"), MenuItem.MenuCategory.SASHIMI),

                // 焼き物
                new MenuItem("焼き鳥盛り合わせ", "もも、つくね、ねぎまなど5本セット。", new BigDecimal("1280"), MenuItem.MenuCategory.GRILLED),
                new MenuItem("塩焼きサバ", "脂ののった塩焼きサバ。大根おろし付き。", new BigDecimal("780"), MenuItem.MenuCategory.GRILLED),
                new MenuItem("牛カルビ", "やわらかい牛カルビ焼き。", new BigDecimal("1480"), MenuItem.MenuCategory.GRILLED),

                // 揚げ物
                new MenuItem("唐揚げ", "ジューシーな鶏の唐揚げ。", new BigDecimal("780"), MenuItem.MenuCategory.FRIED),
                new MenuItem("アジフライ", "サクサクのアジフライ。タルタルソース付き。", new BigDecimal("880"), MenuItem.MenuCategory.FRIED),
                new MenuItem("天ぷら盛り合わせ", "海老、野菜など5種の天ぷら。", new BigDecimal("1380"), MenuItem.MenuCategory.FRIED),

                // ご飯物
                new MenuItem("親子丼", "ふわふわ卵の親子丼。", new BigDecimal("880"), MenuItem.MenuCategory.RICE),
                new MenuItem("海鮮丼", "新鮮な刺身がたっぷりの海鮮丼。", new BigDecimal("1480"), MenuItem.MenuCategory.RICE),
                new MenuItem("炒飯", "具だくさんの炒飯。", new BigDecimal("980"), MenuItem.MenuCategory.RICE),

                // 麺類
                new MenuItem("ラーメン", "醤油ベースの昔ながらのラーメン。", new BigDecimal("780"), MenuItem.MenuCategory.NOODLES),
                new MenuItem("焼きそば", "ソース味の焼きそば。", new BigDecimal("680"), MenuItem.MenuCategory.NOODLES),

                // ビール
                new MenuItem("生ビール（中ジョッキ）", "キリン一番搾り生ビール。", new BigDecimal("580"), MenuItem.MenuCategory.BEER),
                new MenuItem("生ビール（小ジョッキ）", "キリン一番搾り生ビール。", new BigDecimal("380"), MenuItem.MenuCategory.BEER),
                new MenuItem("瓶ビール（大瓶）", "キリンラガービール大瓶。", new BigDecimal("650"), MenuItem.MenuCategory.BEER),

                // 日本酒
                new MenuItem("冷酒（1合）", "本日のおすすめ冷酒。", new BigDecimal("680"), MenuItem.MenuCategory.SAKE),
                new MenuItem("熱燗（1合）", "温かい熱燗。", new BigDecimal("580"), MenuItem.MenuCategory.SAKE),

                // 焼酎
                new MenuItem("芋焼酎", "鹿児島産芋焼酎。ロック・水割り・お湯割り。", new BigDecimal("480"), MenuItem.MenuCategory.SHOCHU),
                new MenuItem("麦焼酎", "大分産麦焼酎。ロック・水割り・お湯割り。", new BigDecimal("480"), MenuItem.MenuCategory.SHOCHU),

                // ソフトドリンク
                new MenuItem("ウーロン茶", "すっきりとしたウーロン茶。", new BigDecimal("280"), MenuItem.MenuCategory.SOFT_DRINK),
                new MenuItem("オレンジジュース", "100%オレンジジュース。", new BigDecimal("350"), MenuItem.MenuCategory.SOFT_DRINK),
                new MenuItem("コーラ", "氷入りコーラ。", new BigDecimal("280"), MenuItem.MenuCategory.SOFT_DRINK),

                // デザート
                new MenuItem("アイスクリーム", "バニラアイスクリーム。", new BigDecimal("380"), MenuItem.MenuCategory.DESSERT),
                new MenuItem("わらび餅", "きな粉と黒蜜のわらび餅。", new BigDecimal("480"), MenuItem.MenuCategory.DESSERT));

        // 調理時間を設定
        menuItems.forEach(item -> {
            switch (item.getCategory()) {
                case APPETIZER -> item.setPreparationTimeMinutes(5);
                case SASHIMI -> item.setPreparationTimeMinutes(8);
                case GRILLED -> item.setPreparationTimeMinutes(15);
                case FRIED -> item.setPreparationTimeMinutes(12);
                case RICE -> item.setPreparationTimeMinutes(10);
                case NOODLES -> item.setPreparationTimeMinutes(8);
                case DESSERT -> item.setPreparationTimeMinutes(5);
                default -> item.setPreparationTimeMinutes(3);
            }
        });

        menuItemRepository.saveAll(menuItems);
        System.out.println("メニューデータを初期化しました。");
    }
}