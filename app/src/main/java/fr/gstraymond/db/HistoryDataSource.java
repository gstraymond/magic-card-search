package fr.gstraymond.db;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import fr.gstraymond.biz.SearchOptions;

public class HistoryDataSource {

    private static final String FILENAME = "history";

    private Context context;

    public HistoryDataSource(Context context) {
        this.context = context;
    }

    public void appendHistory(SearchOptions options) {
        History history = new History(getLastId() + 1, options.getQuery(), false, options.getFacets());
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND);
            fos.write((history.toString() + "\n").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(getClass().getName(), "appendHistory", e);
        } catch (IOException e) {
            Log.e(getClass().getName(), "appendHistory", e);
        }
    }

    public void clearHistory() {
        context.deleteFile(FILENAME);
    }

    public void clearNonFavoriteHistory() {
        List<History> allHistory = getAllHistory();
        List<History> cleanedHistory = new ArrayList<History>();
        int id = 1;
        for (History h : allHistory) {
            if (h.isFavorite()) {
                cleanedHistory.add(h.setId(id++));
            }
        }
        writeHistory(cleanedHistory);
    }

    public ArrayList<History> getAllHistory() {
        ArrayList<History> cardHistories = new ArrayList<History>();
        try {
            FileInputStream inputStream = context.openFileInput(FILENAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    History history = new History(line);
                    cardHistories.add(history);
                    Log.d(getClass().getName(), "get all history : " + history);
                } catch (Throwable t) {
                    Log.e(getClass().getName(), "init history", t);
                }
            }
            br.close();
            return cardHistories;
        } catch (FileNotFoundException e) {
            Log.e(getClass().getName(), "getAllHistory not found");
        } catch (IOException e) {
            Log.e(getClass().getName(), "getAllHistory", e);
        }
        return cardHistories;
    }

    public int getLastId() {
        List<History> histories = getAllHistory();
        if (histories.isEmpty()) {
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
        } catch (FileNotFoundException e) {
            Log.e(getClass().getName(), "appendHistory", e);
        } catch (IOException e) {
            Log.e(getClass().getName(), "appendHistory", e);
        }
    }
}
