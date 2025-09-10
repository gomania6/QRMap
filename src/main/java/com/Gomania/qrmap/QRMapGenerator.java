package com.gomania.qrmap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.awt.image.BufferedImage;

import com.google.zxing.WriterException;

public class QRMapGenerator {

    public static ItemStack generateQRMap(Player player, int mapId) {
        QRMapData data = QRMapDatabase.get(mapId);
        if (data == null) return null;

        try {
            BufferedImage qrImage = QRUtils.generateQRCodeImage(data.getText(), 128, 128);
            ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
            MapView mapView = Bukkit.createMap(player.getWorld());
            mapView.getRenderers().forEach(mapView::removeRenderer);

            mapView.addRenderer(new MapRenderer() {
                private boolean rendered = false;

                @Override
                public void render(MapView view, MapCanvas canvas, Player p) {
                    if (rendered) return;
                    for (int x = 0; x < qrImage.getWidth(); x++) {
                        for (int y = 0; y < qrImage.getHeight(); y++) {
                            int color = qrImage.getRGB(x, y);
                            canvas.setPixel(x, y, MapPalette.matchColor(new java.awt.Color(color)));
                        }
                    }
                    rendered = true;
                }
            });

            MapMeta meta = (MapMeta) mapItem.getItemMeta();
            meta.setMapView(mapView);
            meta.setDisplayName(data.getName());
            meta.getPersistentDataContainer().set(
                    new NamespacedKey("qrmap", "qr_id"),
                    PersistentDataType.INTEGER,
                    data.getId()
            );
            mapItem.setItemMeta(meta);
            return mapItem;

        } catch (WriterException e) {
            player.sendMessage("§cFailed to generate QR code!");
            e.printStackTrace();
            return null;
        }
    }
}
