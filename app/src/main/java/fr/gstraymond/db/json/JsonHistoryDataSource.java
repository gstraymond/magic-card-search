package fr.gstraymond.db.json;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.db.History;
import fr.gstraymond.db.HistoryDataSource;

public class JsonHistoryDataSource {

    private static final String FILENAME = "json_history";
    private static int MAX = 20;
    private Log log = new Log(this);

    private Context context;
    private MapperUtil<List<JsonHistory>> mapperUtil;
    private Comparator<JsonHistory> jsonHistoryComparator;

    public JsonHistoryDataSource(Context context, ObjectMapper objectMapper) {
        this.context = context;
        this.mapperUtil = MapperUtil.fromCollectionType(objectMapper, JsonHistory.class);
        this.jsonHistoryComparator = new Comparator<JsonHistory>() {
            @Override
            public int compare(JsonHistory lhs, JsonHistory rhs) {
                return rhs.getDate().compareTo(lhs.getDate());
            }
        };
    }

    public ArrayList<JsonHistory> getAllHistory() {
        try {
            FileInputStream inputStream = context.openFileInput(FILENAME);
            return new ArrayList<>(mapperUtil.read(inputStream));
        } catch (FileNotFoundException e) {
            log.e("getAllHistory", e);
            return new ArrayList<>();
        }
    }
    public void appendHistory(SearchOptions options) {
        JsonHistory jsonHistory = new JsonHistory(options.getQuery(), false, options.getFacets());
        ArrayList<JsonHistory> allHistory = getAllHistory();
        allHistory.add(jsonHistory);
        writeHistory(allHistory);
    }

    public void clearHistory() {
        context.deleteFile(FILENAME);
    }


    public void clearNonFavoriteHistory() {
        List<JsonHistory> allHistory = getAllHistory();
        List<JsonHistory> cleanedHistory = new ArrayList<>();
        for (JsonHistory h : allHistory) {
            if (h.isFavorite()) {
                cleanedHistory.add(h);
            }
        }
        writeHistory(cleanedHistory);
    }

    public void manageFavorite(JsonHistory jsonHistory, boolean add) {
        List<JsonHistory> jsonHistories = getAllHistory();
        for (JsonHistory history : jsonHistories) {
            log.d("h1 %s h2 %s", history.getDate().getTime(), jsonHistory.getDate().getTime());
            if (history.getDate().equals(jsonHistory.getDate())) {
                log.d("pouet");
                history.setFavorite(add);
                break;
            }
        }
        writeHistory(jsonHistories);
    }

    public void writeHistory(List<JsonHistory> jsonHistories) {
        Collections.sort(jsonHistories, jsonHistoryComparator);
        List<JsonHistory> subList = jsonHistories.subList(0, jsonHistories.size() > MAX ? MAX : jsonHistories.size());
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(mapperUtil.asJsonString(subList).getBytes());
            fos.close();
        } catch (Exception e) {
            log.e("appendHistory", e);
        }
    }

    public void migrate() {
        HistoryDataSource historyDataSource = new HistoryDataSource(context);
        log.d("migrate...");
        if (!historyDataSource.hasHistory()) {log.d("migrate... no"); return; }

        log.d("migrate... yes");

        ArrayList<History> allHistory = historyDataSource.getAllHistory();
        List<JsonHistory> jsonHistories = new ArrayList<>();
        for(History history : allHistory) {
            JsonHistory jsonHistory = new JsonHistory();
            jsonHistory.setDate(history.getDate());
            jsonHistory.setFacets(history.getFacets());
            jsonHistory.setFavorite(history.isFavorite());
            jsonHistory.setQuery(history.getQuery());
            jsonHistories.add(jsonHistory);
        }
        writeHistory(jsonHistories);
        historyDataSource.clearHistory();
    }
}
