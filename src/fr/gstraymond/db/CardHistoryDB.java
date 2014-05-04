package fr.gstraymond.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CardHistoryDB extends SQLiteOpenHelper {

	public static final String TABLE = "card_history";

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_QUERY = "query";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_FAVORITE = "favorite";

	private static final String DB_NAME = TABLE + ".db";
	private static final int DB_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, " + COLUMN_QUERY
			+ " text not null, " + COLUMN_DATE + " integer not null, "
			+ COLUMN_FAVORITE + " integer(1) not null";

	public CardHistoryDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(CardHistoryDB.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(db);
	}

}
