package fr.gstraymond.db.json;


import android.content.Context;

import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;
import com.squareup.moshi.Moshi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.gstraymond.models.CardWithOccurrence;

public class JsonDeck {

    private Log log = new Log(this);

    private Context context;
    private MapperUtil<List<CardWithOccurrence>> mapperUtil;

    public JsonDeck(Context context, Moshi objectMapper) {
        this.context = context;
        this.mapperUtil = MapperUtil.fromCollectionType(objectMapper, CardWithOccurrence.class);
    }

    public List<CardWithOccurrence> load(String deckId) {
        try {
            FileInputStream inputStream = context.openFileInput("deck_" + deckId);
            return new ArrayList<>(mapperUtil.read(inputStream));
        } catch (FileNotFoundException e) {
            log.w("get: %s", e);
            return new ArrayList<>();
        }
    }

    public void save(String deckId, List<CardWithOccurrence> cards) {
        try {
            FileOutputStream fos = context.openFileOutput("deck_" + deckId, Context.MODE_PRIVATE);
            fos.write(mapperUtil.asJsonString(cards).getBytes());
            fos.close();
        } catch (Exception e) {
            log.e("save", e);
        }
    }

    // change occurrence
}
