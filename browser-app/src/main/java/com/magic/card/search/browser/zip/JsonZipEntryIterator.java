package com.magic.card.search.browser.zip;

import com.magic.card.search.commons.log.Log;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JsonZipEntryIterator extends ZipEntryIterator<String> implements ZipEntryIterator.ZipEntryProcessor<String> {

    private String entryName;
    private StringBuilder result;

    private Log log = new Log(this);

    public JsonZipEntryIterator(String entryName) {
        this.entryName = entryName;
        this.zipEntryProcessor = this;
    }

    @Override
    public void init() {
        result = new StringBuilder();
    }

    @Override
    public void process(ZipEntry zipEntry, ZipInputStream zis) {
        if (zipEntry.getName().equals(entryName)) {
            try {
                for (int c = zis.read(); c != -1; c = zis.read()) {
                    result.append((char) c);
                }
            } catch (IOException e) {
                log.e("process", e);
            }
        }
    }

    @Override
    public String getResults() {
        return result.toString();
    }
}
