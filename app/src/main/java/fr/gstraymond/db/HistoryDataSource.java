package fr.gstraymond.db;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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

    public ArrayList<History> getAllHistory() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput(FILENAME)));
            ArrayList<History> cardHistories = new ArrayList<History>();
            String line;
            while ((line = br.readLine()) != null) {
                History history = new History(line);
                cardHistories.add(history);
                Log.d(getClass().getName(), "get all history : " + history);
            }
            br.close();
            Collections.reverse(cardHistories);
            return cardHistories;
        } catch (FileNotFoundException e) {
            Log.e(getClass().getName(), "getAllHistory", e);
        } catch (IOException e) {
            Log.e(getClass().getName(), "getAllHistory", e);
        } catch (ParseException e) {
            Log.e(getClass().getName(), "getAllHistory", e);
        }
        return new ArrayList<History>();
    }

    public int getLastId() {
        List<History> histories = getAllHistory();
        if (histories.isEmpty()) {
            return 0;
        }
        return histories.get(0).getId();
    }
}
