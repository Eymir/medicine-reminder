package siwos.dr.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class MedicamentsDbAdapter extends AppDbAdapter {

private static MedicamentsDbAdapter instance = null;
	
	public static final String KEY_NAME = "name";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DESCRIPTION = "description";
    
    public static final String DATABASE_TABLE = "medicaments";
	
    public static MedicamentsDbAdapter getInstance(Context ctx) {
    	if (instance == null)
    		instance = new MedicamentsDbAdapter(ctx);
    	return instance;
    }
    
	private MedicamentsDbAdapter(Context ctx) {
		super(ctx);
		open();
	}

	public long insert(String name) {
		Cursor c = fetchOne(name);
		if ( c.getCount() != 0 )
			return -1;
		ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        //TODO: sprawdzic czy na pewno zwraca klucz glowny rekordu
        return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public long insert(String name, String desc) {
		Cursor c = fetchOne(name);
		if ( c.getCount() != 0 ) {
			String cur_desc = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
			if (cur_desc.length() < desc.length())
				update(name, desc);
			return -1;
		}
		ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_DESCRIPTION, desc);
        //TODO: sprawdzic czy na pewno zwraca klucz glowny rekordu
        return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public int update(String name, String desc) {
		ContentValues args = new ContentValues();
        args.put(KEY_DESCRIPTION, desc);
        return mDb.update(DATABASE_TABLE, args, KEY_NAME + "= \"" + name + "\"", null);
	}
	
    public Cursor fetchAll() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DESCRIPTION}, null, null, null, null, null);
    }
    
    public Cursor fetchOne(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_DESCRIPTION}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return mCursor;
    }

    public Cursor fetchOne(String name) {
    	Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_DESCRIPTION}, KEY_NAME + "=\"" + name + "\"", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
       	return mCursor;
    }
    
    public Cursor fetchMedicamentsJoinTreatments() {
    	SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
    	builder.setTables(
    			DATABASE_TABLE + 
    			" LEFT JOIN " + 
    			TreatmentsDbAdapter.DATABASE_TABLE +
    			" ON (" + 
    			DATABASE_TABLE + "." + KEY_ROWID + 
    			" = " + 
    			TreatmentsDbAdapter.DATABASE_TABLE + "." + TreatmentsDbAdapter.KEY_MEDICAMENT_ID +
    			")");

    	String sql = builder.buildQuery(
    			new String[] {
    					DATABASE_TABLE + "." + KEY_ROWID,
    					KEY_NAME, 
    					KEY_DESCRIPTION, 
    					},
    			"", 
    			new String[] {}, 
    			null, 
    			null, 
    			DATABASE_TABLE + "." + KEY_ROWID + " ASC", 
    			null);
    	
    	Log.d("ToDbAdapter", sql);
        return mDb.rawQuery(sql, new String[] {});

    }
}
