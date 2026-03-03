import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lightweight LRU cache for scaled ImageIcons loaded from URLs.
 * Loading is typically done off the EDT (SwingWorker).
 */
public class ImageCache {
    private final Map<String, ImageIcon> lru;
    private final int maxEntries;

    public ImageCache(int maxEntries) {
        this.maxEntries = Math.max(16, maxEntries);
        this.lru = new LinkedHashMap<>(64, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ImageIcon> eldest) {
                return size() > ImageCache.this.maxEntries;
            }
        };
    }

    public synchronized ImageIcon get(String url) {
        return lru.get(url);
    }

    public synchronized void put(String url, ImageIcon icon) {
        if (url == null || icon == null) return;
        lru.put(url, icon);
    }

    public ImageIcon loadAndScale(String url, int w, int h) throws Exception {
        // URL(String) is deprecated in newer Java versions. Build via URI instead.
        ImageIcon icon = new ImageIcon(URI.create(url).toURL());
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
