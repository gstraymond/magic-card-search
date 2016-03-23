package com.magic.card.search.browser.zip;

import com.magic.card.search.commons.log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class ZipEntryIterator<A> {

    private Log log = new Log(this);

    protected ZipEntryProcessor<A> zipEntryProcessor;

    interface ZipEntryProcessor<T> {
        void init();

        void process(ZipEntry zipEntry, ZipInputStream zis);

        T getResults();
    }

    public A iterate(InputStream inputStream) {
        long now = System.currentTimeMillis();
        zipEntryProcessor.init();
        try {
            ZipEntry entry;
            ZipInputStream zis = new ZipInputStream(inputStream);
            while ((entry = zis.getNextEntry()) != null) {
                zipEntryProcessor.process(entry, zis);
            }
        } catch (IOException e) {
            log.e("getAllEntries", e);
        }
        log.d(String.format("iterate took %sms", System.currentTimeMillis() - now));
        return zipEntryProcessor.getResults();
    }
}
