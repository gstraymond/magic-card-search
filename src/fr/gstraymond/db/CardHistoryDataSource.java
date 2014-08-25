package fr.gstraymond.db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class CardHistoryDataSource {
	
	private final String filename = "history";
	
	private Context context;

	public CardHistoryDataSource(Context context) {
		this.context = context;
	}

	public void appendHistory(String query) {
		CardHistory cardHistory = new CardHistory(getLastId() + 1, query, false);
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(filename, Context.MODE_APPEND);
			fos.write(cardHistory.toString().getBytes());
		} catch (FileNotFoundException e) {
			Log.e(getClass().getName(), "appendHistory", e);
		} catch (IOException e) {
			Log.e(getClass().getName(), "appendHistory", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					Log.e(getClass().getName(), "appendHistory", e);
				}
			}
		}
	}

	public void deleteCardHistory(CardHistory cardHistory) {
	}
	
	public void clearHistory() {
		context.deleteFile(filename);
	}

	public List<CardHistory> getAllCardHistory() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));
		    List<CardHistory> cardHistories = new ArrayList<CardHistory>();
		    String line = "";
		    while ((line = br.readLine()) != null) {
		    	CardHistory cardHistory = new CardHistory(line);
				cardHistories.add(cardHistory);
				Log.d(getClass().getName(), "get all history : " + cardHistory);
		    }
			return cardHistories;
		} catch (FileNotFoundException e) {
			Log.e(getClass().getName(), "getAllCardHistory", e);
		} catch (IOException e) {
			Log.e(getClass().getName(), "getAllCardHistory", e);
		} catch (ParseException e) {
			Log.e(getClass().getName(), "getAllCardHistory", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Log.e(getClass().getName(), "getAllCardHistory", e);
				}
			}
		}
		return new ArrayList<CardHistory>();
	}
	
	public int getLastId() {
		List<CardHistory> histories = getAllCardHistory();
		if (histories.isEmpty()) {
			return 0;
		}
		return histories.get(histories.size() - 1).getId();
	}
}
