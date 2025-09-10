package com.gomania.qrmap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QRMapAPI {

    // Передаем текст и имя, создаем запись в базе и получаем ID
    public static void giveQRMap(Player player, String text, String mapName) {
        int id = QRMapDatabase.add(text, mapName); // сохраняем и получаем ID
        ItemStack map = QRMapGenerator.generateQRMap(player, id); // используем ID
        if (map != null) player.getInventory().addItem(map);
    }
}
