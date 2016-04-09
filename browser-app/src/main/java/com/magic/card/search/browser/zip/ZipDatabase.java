package com.magic.card.search.browser.zip;

import android.content.Context;
import android.content.SharedPreferences;
import com.magic.card.search.browser.R;
import com.magic.card.search.browser.android.CustomApplication;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;
import com.magic.card.search.commons.model.MTGCard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZipDatabase {

    private CustomApplication application;
    private Log log = new Log(this);
    private MapperUtil<List<MTGCard>> mapperUtil;
    private NameZipEntryIterator nameZipEntryIterator;

    public ZipDatabase(CustomApplication application) {
        this.application = application;
        this.mapperUtil = MapperUtil.fromCollectionType(application.getObjectMapper(), MTGCard.class);
        this.nameZipEntryIterator = new NameZipEntryIterator();
    }

    public List<String> getAllEntries() {
        //InputStream inputStream = getCardsInputStream();
        //return nameZipEntryIterator.iterate(inputStream);
        List<String> list = new ArrayList<>();
        for (String f : application.fileList()) {
            if (f.endsWith(".json")) {
                list.add(f);
            }
        }
        return list;
    }

    public List<MTGCard> getCards(String entryName) {
        //InputStream inputStream = getCardsInputStream();
        //String json = getJsonAsString(entryName, inputStream);
        try {
            FileInputStream fileInputStream = application.openFileInput(entryName);
            return mapperUtil.read(fileInputStream);
        } catch (FileNotFoundException e) {
            log.e("getCards", e);
        }
        return Collections.emptyList();
    }

    public void unzip() {
        SharedPreferences pref = application.getSharedPreferences("database", Context.MODE_PRIVATE);
        if (!pref.getString("filename", "???").equals("foo")) {
            FileZipEntryIterator fileZipEntryIterator = new FileZipEntryIterator(application);
            fileZipEntryIterator.iterate(getCardsInputStream());
        }
        pref.edit().putString("filename", "foo").apply();
    }

    /*private String getJsonAsString(String entryName, InputStream inputStream) {
        JsonZipEntryIterator zipEntryIterator = new JsonZipEntryIterator(entryName);
        return zipEntryIterator.iterate(inputStream);
    }*/

    private InputStream getCardsInputStream() {
        return application.getResources().openRawResource(R.raw.cards);
    }
}
