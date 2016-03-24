package fr.gstraymond.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import fr.gstraymond.tools.Log;

public class BitmapCache {
    private LruCache<String, Bitmap> memLruCache;
    private Log log = new Log(this);

    public BitmapCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 30;
        log.d(String.format("BitmapCache --> cachesize %s", cacheSize));

        memLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                log.d(String.format("entryRemoved --> recycling %s", key));
                oldValue.recycle();
            }

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                int size = bitmap.getByteCount() / 1024;
                log.d(String.format("sizeof --> %s %s", size, key));
                return size;
            }
        };
    }

    public Bitmap get(String key) {
        return memLruCache.get(key);
    }

    public void put(String url, Bitmap bitmap) {
        memLruCache.put(url, bitmap);
    }
}
