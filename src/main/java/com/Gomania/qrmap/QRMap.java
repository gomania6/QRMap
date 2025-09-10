package com.gomania.qrmap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class QRMap extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new GUIManager(), this);
        QRMapDatabase.init(this);
        getLogger().info("QRMap enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!player.hasPermission("qrmap.use")) { player.sendMessage("§cNo permission!"); return true; }

        if (args.length == 0) GUIManager.openQRGUI(player, 0);
        else {
            String text = String.join(" ", args);
            int id = QRMapDatabase.add(text, text); // имя карты = текст
            ItemStack map = QRMapGenerator.generateQRMap(player, id);
            if (map != null) { player.getInventory().addItem(map); player.sendMessage("§aQR map created!"); }
        }
        return true;
    }
}
