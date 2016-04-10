package com.magic.card.search.browser.zip;

import android.content.Context;

import com.magic.card.search.commons.log.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileZipEntryIterator extends ZipEntryIterator<Void> implements ZipEntryIterator.ZipEntryProcessor<Void> {

    private Context context;

    private Log log = new Log(this);

    public FileZipEntryIterator(Context context) {
        this.context = context;
        this.zipEntryProcessor = this;
    }

    @Override
    public void init() {

    }

    @Override
    public void process(ZipEntry zipEntry, ZipInputStream zis) {
        try {
            log.d("inflating %s...", zipEntry.getName());
            FileOutputStream zos = context.openFileOutput(zipEntry.getName(), Context.MODE_PRIVATE);
            int length;
            byte[] buffer = new byte[1024];

            while((length = zis.read(buffer)) > 0){
                zos.write(buffer, 0, length);
            }

            zos.close();
            zis.closeEntry();
        } catch (IOException e) {
            log.e("process", e);
        }
    }

    @Override
    public Void getResults() {
        return null;
    }
}
