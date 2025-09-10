## **QRMap – Full Plugin Functionality**

### 1. **QR Map Creation**

* Command: `/qrmap [URL]`

  * If a URL is provided, the plugin generates a QR code for it and places it on a map in the player’s inventory.
  * If no URL is given, the GUI opens, showing all previously created QR maps.

* Each map includes:

  * A scannable QR code.
  * Map name — the domain of the URL (e.g., `www.youtube.com`).

* **Server-side saving:**

  * All maps are stored in `plugins/QRMap/maps` as PNG files.
  * File names are sanitized automatically to remove forbidden characters (`:`, `/`, `?`, etc.).

---

### 2. **GUI for Viewing Maps**

* Players can open the GUI without arguments using `/qrmap`.
* Interface features:

  * 54-slot inventory (9x6), slots 45 and 53 are arrows for pagination.
  * Map slots (0-44 and 46-52) display the maps with:

    * `§aTake (Left Click)`
    * `§cDelete (Right Click)`
  * Pagination:

    * Clicking arrows changes the page.
    * Supports more than 54 maps.

---

### 3. **Interacting with Maps**

* **Left Click (LMB) on a map:**

  * Adds a copy of the map to the player’s inventory.
  * Requires OP permissions to take maps.
* **Right Click (RMB) on a map:**

  * Deletes the map from the server (`maps/*.png`).
  * GUI updates automatically after deletion.

---

### 4. **QRMap API**

* The plugin provides an **API** for other plugins:

```java
QRMapAPI.giveQRMap(player, "https://example.com", "example.com");
```

* Allows programmatically giving QR maps to players without using commands.

---

### 5. **Technical Details**

* QR codes are generated using ZXing (`QRCodeWriter`).
* Maps use `MapView` and `MapRenderer`.
* Supports saving QR maps as PNG files for reuse.
* Uses `PersistentDataContainer` to store the original URL in the map.

---

### 6. **Additional Features**

* Automatic URL processing:

  * GUI and map display only the domain for clarity.
  * When taking a map via LMB or using the API, the full URL is preserved.
* Compatible with Paper/Spigot API 1.21.
* Multi-page GUI for large numbers of maps.

---

### 7. **Example Use Cases**

1. Player creates a QR map:

```
/qrmap https://www.youtube.com/watch?v=abcd
```

→ QR map appears in inventory; file saved as `plugins/QRMap/maps/www.youtube.com.png`.

2. Player opens the GUI:

```
/qrmap
```

→ GUI displays all maps with buttons "Take" (LMB) and "Delete" (RMB), with arrows for page navigation.

3. Another plugin gives a QR map to a player:

```java
QRMapAPI.giveQRMap(player, "https://example.com", "example.com");
```

→ Player receives the map directly in inventory without a command.
