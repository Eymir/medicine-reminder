package siwos.dr.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppDbAdapter {
	

    private static final String TAG = "AppDbAdapter";
    private DatabaseHelper mDbHelper;
    protected SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String FREQUENCIES_DATABASE_CREATE =
        "create table frequencies (_id integer primary key autoincrement, "
        + "name text not null, interval integer not null);";

    private static final String TREATMENTS_DATABASE_CREATE =
        "create table treatments (_id integer primary key autoincrement, "
    	+ "id_web not null,"
        + "start_date text not null,"
        + "pills integer not null, "
        + "pills_taken integer not null, "
        + "frequency_id integer not null, "
        + "medicament_id integer not null, "
        + "active integer not null, "
        + "scheduled integer not null, "
        + "foreign key(frequency_id) references frequencies(_id), "
        + "foreign key(medicament_id) references medicaments(_id));";
    
    private static final String USER_DATABASE_CREATE =
    	"create table user (_id String primary key);";
    
    private static final String MEDICAMENTS_DATABASE_CREATE =
        "create table medicaments (_id integer primary key autoincrement, " 
    	+ "name text not null, description text);";
    
    private static final String DATABASE_NAME = "drug_reminder";
    private static final String FREQUENCIES_DATABASE_TABLE = "frequencies";
    private static final String TREATMENTS_DATABASE_TABLE = "treatments";
    private static final String MEDICAMENTS_DATABASE_TABLE = "medicaments";
    private static final String USER_DATABASE_TABLE = "user";
    private static final int DATABASE_VERSION = 1;

    protected final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FREQUENCIES_DATABASE_CREATE);
            db.execSQL(TREATMENTS_DATABASE_CREATE);
            db.execSQL(MEDICAMENTS_DATABASE_CREATE);
            db.execSQL(USER_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FREQUENCIES_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TREATMENTS_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MEDICAMENTS_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + USER_DATABASE_TABLE);
            onCreate(db);
        }
    }

    public static class PlacesDbHelper{
    	
    }
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public AppDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public AppDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

}
