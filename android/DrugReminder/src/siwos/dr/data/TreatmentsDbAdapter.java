package siwos.dr.data;

import java.util.Date;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class TreatmentsDbAdapter extends AppDbAdapter {

	private static TreatmentsDbAdapter instance = null;
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_ID_WEB = "id_web";
	public static final String KEY_START_DATE = "start_date";
	public static final String KEY_PILLS = "pills";
	public static final String KEY_PILLS_TAKEN = "pills_taken";
	public static final String KEY_FREQUENCY_ID = "frequency_id";
	public static final String KEY_MEDICAMENT_ID = "medicament_id";
	public static final String KEY_ACTIVE = "active";
	public static final String KEY_SCHEDULED = "scheduled";
	
    public static final String DATABASE_TABLE = "treatments";
    
    //TODO: id od serwera zeby nie zapisywac istniejacych treatmentow
    
    public static TreatmentsDbAdapter getInstance(Context ctx) {
    	if (instance == null)
    		instance = new TreatmentsDbAdapter(ctx);
    	return instance;
    } 
	
	private TreatmentsDbAdapter(Context ctx) {
		super(ctx);
		open();
	}

	public Cursor fetchAll() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,
        	KEY_ID_WEB,
	    	KEY_START_DATE,
	    	KEY_PILLS,
	    	KEY_PILLS_TAKEN,
	    	KEY_FREQUENCY_ID,
	    	KEY_MEDICAMENT_ID,
	    	KEY_ACTIVE,
	    	KEY_SCHEDULED}, null, null, null, null, null);
    }
    
	public Cursor fetchAllCurrent() {
		Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
            		KEY_ID_WEB,
        	    	KEY_START_DATE,
        	    	KEY_PILLS,
        	    	KEY_PILLS_TAKEN,
        	    	KEY_FREQUENCY_ID,
        	    	KEY_MEDICAMENT_ID,
        	    	KEY_ACTIVE,
        	    	KEY_SCHEDULED}, KEY_PILLS + ">" + KEY_PILLS_TAKEN, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
	}
	
    public Cursor fetchOne(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
            		KEY_ID_WEB,
        	    	KEY_START_DATE,
        	    	KEY_PILLS,
        	    	KEY_PILLS_TAKEN,
        	    	KEY_FREQUENCY_ID,
        	    	KEY_MEDICAMENT_ID,
        	    	KEY_ACTIVE,
        	    	KEY_SCHEDULED}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
       	return mCursor;
    }
    
    public Cursor fetchOneActive(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
            		KEY_ID_WEB,
        	    	KEY_START_DATE,
        	    	KEY_PILLS,
        	    	KEY_PILLS_TAKEN,
        	    	KEY_FREQUENCY_ID,
        	    	KEY_MEDICAMENT_ID,
        	    	KEY_ACTIVE,
        	    	KEY_SCHEDULED}, KEY_ROWID + "=" + rowId + " AND " + KEY_PILLS_TAKEN + " <= " + KEY_PILLS, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
       	return mCursor;
    }

    public Cursor fetchOneByWebId(long webId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
            		KEY_ID_WEB,
        	    	KEY_START_DATE,
        	    	KEY_PILLS,
        	    	KEY_PILLS_TAKEN,
        	    	KEY_FREQUENCY_ID,
        	    	KEY_MEDICAMENT_ID,
        	    	KEY_ACTIVE,
        	    	KEY_SCHEDULED}, KEY_ID_WEB + "=" + webId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
       	return mCursor;
    }

    
    public long insert(Date date, int pills, int pills_taken, int frequency, int medicine, boolean active, boolean scheduled) {
        ContentValues initialValues = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        initialValues.put(KEY_ID_WEB, -1);	//that means it is record created in mobile phone
        initialValues.put(KEY_START_DATE, dateFormat.format(date));
        initialValues.put(KEY_PILLS, pills);
        initialValues.put(KEY_PILLS_TAKEN, pills_taken);
        FrequenciesDbAdapter f = FrequenciesDbAdapter.getInstance(mCtx);
        int count = f.fetchOne(frequency).getCount();
        if (count != 0)
        	initialValues.put(KEY_FREQUENCY_ID, frequency);
        else return 0;
        if (MedicamentsDbAdapter.getInstance(mCtx).fetchOne(medicine).getCount() != 0)
        	initialValues.put(KEY_MEDICAMENT_ID, medicine);
        else return 0;
        initialValues.put(KEY_ACTIVE, active);
        initialValues.put(KEY_SCHEDULED, scheduled);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public int increaseTakenPills(int id) {
    	Cursor c = fetchOne(id);
    	if (c.getCount() != 1) return -1;
    	int pillsTaken = c.getInt(c.getColumnIndex(KEY_PILLS_TAKEN));
    	int allPills = c.getInt(c.getColumnIndex(KEY_PILLS));
    	pillsTaken++;
    	ContentValues args = new ContentValues();
        args.put(KEY_PILLS_TAKEN, pillsTaken);
        mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + id, null);
        if (allPills <= pillsTaken)
        	return 0;
        else return 1;
    }
    
    public void updateActive(boolean active, int id) {
    	ContentValues args = new ContentValues();
        args.put(KEY_ACTIVE, active);
        mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + id, null);
    }
    
    public void updateScheduled(boolean scheduled, int id) {
    	ContentValues args = new ContentValues();
        args.put(KEY_SCHEDULED, scheduled);
        mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + id, null);
    }
    
    public long insert(int id_web, String date, int pills, int pills_taken, int interval, String medicine, boolean active, boolean scheduled) {
    	Cursor cur = fetchOneByWebId(id_web);
    	if (cur.getCount() != 0)
    		return -1;
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ID_WEB, id_web);
        initialValues.put(KEY_START_DATE, date);
        initialValues.put(KEY_PILLS, pills);
        initialValues.put(KEY_PILLS_TAKEN, pills_taken);
        FrequenciesDbAdapter f = FrequenciesDbAdapter.getInstance(mCtx);
        Cursor f_cur = f.fetchOneByInterval(interval);
        if (f_cur.getCount() != 0)
        	initialValues.put(KEY_FREQUENCY_ID, f_cur.getInt(f_cur.getColumnIndex(FrequenciesDbAdapter.KEY_ROWID)));
        else return 0;
        Cursor m_cur = MedicamentsDbAdapter.getInstance(mCtx).fetchOne(medicine); 
        if (m_cur.getCount() != 0)
        	initialValues.put(KEY_MEDICAMENT_ID, m_cur.getInt(m_cur.getColumnIndex(MedicamentsDbAdapter.KEY_ROWID)));
        else return 0;
        initialValues.put(KEY_ACTIVE, active);
        initialValues.put(KEY_SCHEDULED, scheduled);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public Cursor fetchUnscheduled() {

        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {
            		KEY_ROWID,
            		KEY_ID_WEB,
        	    	KEY_START_DATE,
        	    	KEY_PILLS,
        	    	KEY_PILLS_TAKEN,
        	    	KEY_FREQUENCY_ID,
        	    	KEY_MEDICAMENT_ID,
        	    	KEY_ACTIVE,
        	    	KEY_SCHEDULED}, KEY_SCHEDULED + "=" + 0, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return mCursor;    	
    }
    
    public boolean takeAnotherPill(int id) {
        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
            		KEY_ID_WEB,
        	    	KEY_START_DATE,
        	    	KEY_PILLS,
        	    	KEY_PILLS_TAKEN,
        	    	KEY_FREQUENCY_ID,
        	    	KEY_MEDICAMENT_ID,
        	    	KEY_ACTIVE,
        	    	KEY_SCHEDULED}, KEY_ROWID + "=" + id + " AND " + KEY_PILLS_TAKEN + " < " + KEY_PILLS, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        if (mCursor.getCount() == 0)
        	return false;
        
        else
        	return true;
    }
    
    public int getHighestId() {
        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {
            		"max( " + KEY_ROWID + " )"}, null, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        mCursor.moveToFirst();
        return mCursor.getInt(0);
    }
}
