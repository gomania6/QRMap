package com.gomania.qrmap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class GUIManager implements Listener {

    private static final int NEXT_SLOT = 53;
    private static final int PREV_SLOT = 45;
    private static final int PAGE_SIZE = 52;

    public static void openQRGUI(Player player, int page) {
        Map<Integer, QRMapData> allMaps = QRMapDatabase.getAll();
        int maxPage = (allMaps.size() - 1) / PAGE_SIZE;
        if (page > maxPage) page = maxPage;
        if (page < 0) page = 0;

        Inventory inv = Bukkit.createInventory(null, 54, "QR Maps - Page " + (page + 1) + "/" + (maxPage + 1));
        int index = 0;
        for (QRMapData data : allMaps.values()) {
            if (index < page * PAGE_SIZE) { index++; continue; }
            if (index >= (page+1)*PAGE_SIZE) break;

            int slot = index % PAGE_SIZE;
            if (slot >= PREV_SLOT) slot++;

            ItemStack map = new ItemStack(Material.FILLED_MAP);
            MapMeta meta = (MapMeta) map.getItemMeta();
            meta.setDisplayName(data.getName());
            meta.setLore(java.util.Arrays.asList("§aTake (LMB)", "§cDelete (RMB)"));
            meta.getPersistentDataContainer().set(new NamespacedKey("qrmap", "qr_id"), PersistentDataType.INTEGER, data.getId());
            map.setItemMeta(meta);
            inv.setItem(slot, map);
            index++;
        }

        // Arrows
        ItemStack prev = new ItemStack(page > 0 ? Material.ARROW : Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName("§7Prev");
        prev.setItemMeta(prevMeta);
        inv.setItem(PREV_SLOT, prev);

        ItemStack next = new ItemStack(page < maxPage ? Material.ARROW : Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName("§7Next");
        next.setItemMeta(nextMeta);
        inv.setItem(NEXT_SLOT, next);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().startsWith("QR Maps")) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player player)) return;

        int slot = e.getRawSlot();
        if (slot == PREV_SLOT || slot == NEXT_SLOT) {
            int page = Integer.parseInt(e.getView().getTitle().split("Page ")[1].split("/")[0]) - 1;
            if (slot == PREV_SLOT) openQRGUI(player, page-1);
            else openQRGUI(player, page+1);
            return;
        }

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.FILLED_MAP) return;

        MapMeta meta = (MapMeta) clicked.getItemMeta();
        Integer id = meta.getPersistentDataContainer().get(new NamespacedKey("qrmap","qr_id"), PersistentDataType.INTEGER);
        if (id == null) return;

        if (e.isLeftClick()) {
            if (!player.isOp()) { player.sendMessage("§cOnly OP can take maps!"); return; }
            ItemStack map = QRMapGenerator.generateQRMap(player, id);
            if (map != null) player.getInventory().addItem(map);
        } else if (e.isRightClick()) {
            QRMapDatabase.delete(id);
            player.sendMessage("§cMap deleted!");
            int page = Integer.parseInt(e.getView().getTitle().split("Page ")[1].split("/")[0]) - 1;
            openQRGUI(player, page);
        }
    }
}
