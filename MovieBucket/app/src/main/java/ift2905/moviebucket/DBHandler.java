package ift2905.moviebucket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movie_bucket.db";

    private static final String TABLE_HEAD = "entries";
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_FAV = "favorite";
    private static final String KEY_VIEWED = "viewed";

    private static SQLiteDatabase db = null;

    static final String SQL_CREATE_DB =
            "CREATE TABLE " + TABLE_HEAD + " ( " +
                    KEY_ID + " INTEGER NOT NULL PRIMARY KEY, " +
                    KEY_TITLE + " TEXT NOT NULL, " +
                    KEY_FAV + " INTEGER NOT NULL, " +
                    KEY_VIEWED + " INTEGER NOT NULL)";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if(DBHandler.db == null) {
            DBHandler.db = getWritableDatabase();
        }
    }

    public SQLiteDatabase getDb() {
        return DBHandler.db;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS `" + TABLE_HEAD + "`");
        onCreate(db);
    }

    public void addToDB(int id, String title, int viewed){
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, id);
        cv.put(KEY_TITLE,title);
        cv.put(KEY_FAV, 0);
        cv.put(KEY_VIEWED, viewed);
        try {
            db.insertOrThrow(TABLE_HEAD, null, cv);
        } catch(SQLException e) {}
    }

    public void removeFromDB(long id){
        db.delete(TABLE_HEAD, KEY_ID + "= " + id, null);
    }

    public void markAsViewed(long id){
        ContentValues cv = new ContentValues();
        cv.put(KEY_VIEWED, 1);
        db.update(TABLE_HEAD, cv,KEY_ID + "= " + id, null);
    }

    public Cursor movieLister(String pageName){
        String[] columns = {KEY_ID, KEY_TITLE, KEY_FAV};
        String criteria = KEY_VIEWED + " = ?";
        String viewed = "0";
        if(pageName.equals("History")) {
            viewed = "1";
        }
        String[] criteriaArgs = {viewed};
        Cursor cursor = db.query(TABLE_HEAD, columns, criteria, criteriaArgs , null, null, null);
        return cursor;
    };

    public int inWhichList(int id){
        String[] columns = {KEY_ID, KEY_VIEWED};
        String criteria = KEY_ID + " = ?";
        String[] criteriaArgs = {Integer.toString(id)};
        Cursor cursor = db.query(TABLE_HEAD, columns, criteria, criteriaArgs , null, null, null);

        if(cursor.getCount() != 0){
            cursor.moveToNext();
            int viewed = cursor.getInt(cursor.getColumnIndexOrThrow("viewed"));
            cursor.close();
            if( viewed == 0) {
                return 1;
            } else {
                return 2;
            }
        }
        cursor.close();
        return 0;
    }
}