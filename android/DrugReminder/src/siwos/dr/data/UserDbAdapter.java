package siwos.dr.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class UserDbAdapter extends AppDbAdapter {

	private static UserDbAdapter instance;
	
	public static final String KEY_USERID = "_id";
	
	public static final String DATABASE_TABLE = "user";
	
	private UserDbAdapter(Context ctx) {
		super(ctx);
		open();
	}

	public static UserDbAdapter getInstance(Context ctx) {
		if (instance == null)
			instance = new UserDbAdapter(ctx);
		return instance;
	}
	
	private void insert(String id) {
		ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERID, id);
        mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	private void update(String old_id, String id) {
		ContentValues args = new ContentValues();
        args.put(KEY_USERID, id);
        mDb.update(DATABASE_TABLE, args, KEY_USERID + "= \"" + old_id + "\"", null);
	}
	
	public void setId(String id) {
		String _id = getId();
		if (_id == null)
			insert(id);
		else
			update(_id, id);
	}
	
	public String getId() {
        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_USERID}, null, null,
                    null, null, null, null);
        if (mCursor != null && mCursor.getCount()!=0) {
            mCursor.moveToFirst();
        } else return null;
        return mCursor.getString(mCursor.getColumnIndex(KEY_USERID));
	}
}

