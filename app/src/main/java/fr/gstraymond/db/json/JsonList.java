package fr.gstraymond.db.json;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.gstraymond.search.model.response.Card;

public class JsonList {

    private Log log = new Log(this);

    private Context context;
    private MapperUtil<List<Card>> mapperUtil;
    private String listName;
    private List<Card> cards = new ArrayList<>();

    public JsonList(Context context, ObjectMapper objectMapper, String listName) {
        this.context = context;
        this.mapperUtil = MapperUtil.fromCollectionType(objectMapper, Card.class);
        this.listName = "lists_" + listName;
        this.cards = load();
    }

    private List<Card> load() {
        try {
            FileInputStream inputStream = context.openFileInput(listName);
            return new ArrayList<>(mapperUtil.read(inputStream));
        } catch (FileNotFoundException e) {
            log.w("get: %s", e);
            save();
            return cards;
        }
    }

    public void save() {
        try {
            FileOutputStream fos = context.openFileOutput(listName, Context.MODE_PRIVATE);
            fos.write(mapperUtil.asJsonString(cards).getBytes());
            fos.close();
        } catch (Exception e) {
            log.e("save", e);
        }
    }

    public boolean addOrRemove(Card card) {
        boolean contains = contains(card);
        if (!contains) cards.add(card);
        else cards.remove(card);
        save();
        log.d("addOrRemove %s -> removed? %s", card, contains);
        return !contains;
    }

    public boolean contains(Card card) {
        String id = getId(card);
        for (Card currentCard : cards) {
            if (getId(currentCard).equals(id)) return true;
        }
        return false;
    }

    private String getId(Card card) {
        return String.format("%s %s %s", card.getTitle(), card.getType(), card.getCastingCost());
    }

    public List<Card> getCards() {
        return cards;
    }
}
