package com.gomania.qrmap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class QRMapDatabase {

    private static final Map<Integer, QRMapData> maps = new HashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(1);
    private static File dataFile;
    private static final Gson gson = new Gson();

    public static void init(JavaPlugin plugin) {
        File folder = plugin.getDataFolder();
        if (!folder.exists()) folder.mkdirs();
        dataFile = new File(folder, "maps.json");
        load();
    }

    public static Map<Integer, QRMapData> getAll() { return maps; }

    public static int add(String text, String name) {
        int id = counter.getAndIncrement();
        maps.put(id, new QRMapData(id, text, name));
        save();
        return id;
    }

    public static void delete(int id) {
        maps.remove(id);
        save();
    }

    public static QRMapData get(int id) { return maps.get(id); }

    public static void load() {
        try {
            if (!dataFile.exists()) return;
            Type type = new TypeToken<Map<Integer, QRMapData>>(){}.getType();
            FileReader reader = new FileReader(dataFile);
            Map<Integer, QRMapData> loaded = gson.fromJson(reader, type);
            reader.close();
            if (loaded != null) {
                maps.clear();
                maps.putAll(loaded);
                int maxId = loaded.keySet().stream().max(Integer::compareTo).orElse(0);
                counter.set(maxId + 1);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load QRMapDatabase: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            FileWriter writer = new FileWriter(dataFile);
            gson.toJson(maps, writer);
            writer.close();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to save QRMapDatabase: " + e.getMessage());
        }
    }
}
