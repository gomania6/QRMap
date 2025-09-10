package com.gomania.qrmap;

public class QRMapData {
    private int id;
    private String text;
    private String name;

    public QRMapData(int id, String text, String name) {
        this.id = id;
        this.text = text;
        this.name = name;
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public String getName() { return name; }
}
