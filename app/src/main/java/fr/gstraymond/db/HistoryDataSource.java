package fr.gstraymond.db;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.tools.Log;

public class HistoryDataSource {

    private static final String FILENAME = "history";

    private Context context;
    private Log log = new Log(this);

    public HistoryDataSource(Context context) {
        this.context = context;
    }

    public void appendHistory(SearchOptions options) {
        History history = new History(getLastId() + 1, options.getQuery(), false, options.getFacets());
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND);
            fos.write((history.toString() + "\n").getBytes());
            fos.close();
        } catch (Exception e) {
            log.e("appendHistory", e);
        }
    }

    public void clearHistory() {
        context.deleteFile(FILENAME);
    }

    public void clearNonFavoriteHistory() {
        List<History> allHistory = getAllHistory();
        List<History> cleanedHistory = new ArrayList<>();
        int id = 1;
        for (History h : allHistory) {
            if (h.isFavorite()) {
                cleanedHistory.add(h.setId(id++));
            }
        }
        writeHistory(cleanedHistory);
    }

    public ArrayList<History> getAllHistory() {
        ArrayList<History> cardHistories = new ArrayList<>();
        try {
            FileInputStream inputStream = context.openFileInput(FILENAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    History history = new History(line);
                    cardHistories.add(history);
                    log.d("get all history : " + history);
                } catch (Throwable t) {
                    log.e("init history", t);
                }
            }
            br.close();
            return cardHistories;
        } catch (FileNotFoundException e) {
            log.w("getAllHistory not found");
        } catch (Exception e) {
            log.e("getAllHistory", e);
        }
        return cardHistories;
    }

    public int getLastId() {
        List<History> histories = getAllHistory();
        if (histories.isEmpty()) {
            clearHistory();
            return 0;
        }
        return histories.get(histories.size() - 1).getId();
    }

    public void manageFavorite(History history, boolean add) {
        List<History> allHistory = getAllHistory();
        for (History h : allHistory) {
            if (h.getId() == history.getId()) {
                h.setFavorite(add);
                break;
            }
        }
        writeHistory(allHistory);
    }

    public void writeHistory(List<History> allHistory) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            for (History history : allHistory) {
                fos.write((history.toString() + "\n").getBytes());
            }
            fos.close();
        } catch (IOException e) {
            log.e("writeHistory", e);
        }
    }
}
