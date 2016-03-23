package com.magic.card.search.browser.zip;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class NameZipEntryIterator extends ZipEntryIterator<List<String>> implements ZipEntryIterator.ZipEntryProcessor<List<String>> {

    private List<String> results;

    public NameZipEntryIterator() {
        this.zipEntryProcessor = this;
    }

    @Override
    public void init() {
        results = new ArrayList<>();
    }

    @Override
    public List<String> getResults() {
        return results;
    }

    @Override
    public void process(ZipEntry zipEntry, ZipInputStream zis) {
        results.add(zipEntry.getName());
    }
}
