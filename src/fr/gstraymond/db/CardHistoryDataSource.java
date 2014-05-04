package fr.gstraymond.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CardHistoryDataSource {

	private String[] allColumns = { CardHistoryDB.COLUMN_ID,
			CardHistoryDB.COLUMN_QUERY };

	private SQLiteDatabase database;
	private CardHistoryDB dbHelper;

	public CardHistoryDataSource(Context context) {
		dbHelper = new CardHistoryDB(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public CardHistory createCardHistory(String query) {
		ContentValues values = new ContentValues();
		values.put(CardHistoryDB.COLUMN_QUERY, query);
		values.put(CardHistoryDB.COLUMN_DATE, new Date().getTime());
		values.put(CardHistoryDB.COLUMN_FAVORITE, 0);
		long insertId = database.insert(CardHistoryDB.TABLE, null, values);
		Cursor cursor = database.query(CardHistoryDB.TABLE, allColumns,
				CardHistoryDB.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		CardHistory cardHistory = toCardHistory(cursor);
		cursor.close();
		return cardHistory;
	}

	private CardHistory toCardHistory(Cursor cursor) {
		int i = 0;
		CardHistory cardHistory = new CardHistory();
		cardHistory.setId(cursor.getInt(i++));
		cardHistory.setQuery(cursor.getString(i++));
		cardHistory.setDate(new Date(cursor.getLong(i++)));
		cardHistory.setFavorite(cursor.getInt(i++) == 0 ? false : true);
		return cardHistory;
	}

	public void deleteCardHistory(CardHistory cardHistory) {
		int id = cardHistory.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(CardHistoryDB.TABLE, CardHistoryDB.COLUMN_ID + " = "
				+ id, null);
	}

	public List<CardHistory> getAllCardHistory() {
		List<CardHistory> cardHistories = new ArrayList<CardHistory>();

		Cursor cursor = database.query(CardHistoryDB.TABLE, allColumns, null,
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CardHistory cardHistory = toCardHistory(cursor);
			cardHistories.add(cardHistory);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return cardHistories;
	}
}
