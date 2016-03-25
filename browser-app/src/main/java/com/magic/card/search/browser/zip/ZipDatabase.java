package com.magic.card.search.browser.zip;

import com.magic.card.search.browser.R;
import com.magic.card.search.browser.android.CustomApplication;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;
import com.magic.card.search.commons.model.MTGCard;

import java.io.InputStream;
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
        InputStream inputStream = getCardsInputStream();
        return nameZipEntryIterator.iterate(inputStream);
    }

    public List<MTGCard> getCards(String entryName) {
        InputStream inputStream = getCardsInputStream();
        String json = getJsonAsString(entryName, inputStream);
        return mapperUtil.read(json);
    }

    private String getJsonAsString(String entryName, InputStream inputStream) {
        JsonZipEntryIterator zipEntryIterator = new JsonZipEntryIterator(entryName);
        return zipEntryIterator.iterate(inputStream);
    }

    private InputStream getCardsInputStream() {
        return application.getResources().openRawResource(R.raw.cards);
    }
}
