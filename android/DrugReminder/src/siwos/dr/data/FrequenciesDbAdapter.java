package siwos.dr.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class FrequenciesDbAdapter extends AppDbAdapter{

	private static FrequenciesDbAdapter instance = null;
	
	public static final String KEY_NAME = "name";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_INTERVAL = "interval";
    public static final String DATABASE_TABLE = "frequencies";
	
    public static FrequenciesDbAdapter getInstance(Context ctx) {
    	if (instance == null)
    		instance = new FrequenciesDbAdapter(ctx);
    	return instance;
    }
    
	private FrequenciesDbAdapter(Context ctx) {
		super(ctx);
		open();
		if(fetchAll().getCount() == 0) {
			insert("once_a_day", 24);
			insert("twice_a_day", 12);
			insert("tree_times_a_day", 8);
			insert("four_times_a_day", 6);
			insert("every_two_hours", 2);
			insert("every_hour", 1);
		}
	}

	private void insert(String name, int interval) {
		ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_INTERVAL, interval);
        mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
    public Cursor fetchAll() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_INTERVAL}, null, null, null, null, null);
    }
    
    public Cursor fetchOne(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_INTERVAL}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Cursor fetchOne(String name) throws SQLException {

        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_INTERVAL}, KEY_NAME + "=\"" + name +"\"", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

	public Cursor fetchOneByInterval(int interval) {
		Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_INTERVAL}, KEY_INTERVAL + "=" + interval, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
	}
}
