package ift2905.moviebucket;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movie_bucket.db";

    /*
    private static final String TABLE_HEAD = "entries";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_FAV = "favorite";
    private static final String KEY_VIEWED = "viewed";
    */

    private static SQLiteDatabase db = null;

    static final String[] TABLES = new String[]{
            "entries",
    };

    static final String[] SQL_CREATE_DB = new String[]{
            "CREATE TABLE `entries` (" +
                    " `_id` INTEGER NOT NULL PRIMARY KEY," +
                    " `title` TEXT NOT NULL," +
                    " `fav` INTEGER NOT NULL" +
                    " `viewed` INTEGER NOT NULL" +
                    ")"
    };

    public DBHandler(Context context, String name,
                     SQLiteDatabase.CursorFactory factory, int version) {
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
        for (String statement : DBHandler.SQL_CREATE_DB) {
            db.execSQL(statement);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        for (String table : DBHandler.TABLES) {
            db.execSQL("DROP TABLE IF EXISTS `" + table + "`");
        }
        onCreate(db);
    }
}
