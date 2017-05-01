package ift2905.moviebucket;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbTV;
import info.movito.themoviedbapi.model.Collection;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class DBHandler extends SQLiteOpenHelper {

    static final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "moviebucket1.db";

    private static final String TABLE_HEAD = "entries";
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_FAV = "favorite";
    private static final String KEY_VIEWED = "viewed";
    private static final String KEY_RUNTIME = "runtime";
    private static final String KEY_ISMOVIE = "ismovie";

    private static SQLiteDatabase db = null;
    private Context DBcontext;

    static final String SQL_CREATE_DB =
            "CREATE TABLE " + TABLE_HEAD + " ( " +
                    KEY_ID + " INTEGER NOT NULL PRIMARY KEY, " +
                    KEY_TITLE + " TEXT NOT NULL, " +
                    KEY_FAV + " INTEGER NOT NULL, " +
                    KEY_VIEWED + " INTEGER NOT NULL," +
                    KEY_RUNTIME + " INTEGER NOT NULL," +
                    KEY_ISMOVIE + " INTEGER NOT NULL)";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if(DBHandler.db == null) {
            DBHandler.db = getWritableDatabase();
        }
        DBcontext = context;
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

    public void addToDB(int id, String title, int viewed, int runtime, int ismovie){
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, id);
        cv.put(KEY_TITLE,title);
        cv.put(KEY_FAV, 0);
        cv.put(KEY_VIEWED, viewed);
        cv.put(KEY_RUNTIME, runtime);
        cv.put(KEY_ISMOVIE, ismovie);
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
        db.update(TABLE_HEAD, cv, KEY_ID + "= " + id, null);
    }

    public Cursor movieLister(String pageName){
        String[] columns = {KEY_ID, KEY_TITLE, KEY_FAV, KEY_RUNTIME};
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

    public boolean checkIfMovie(long id){
        String[] columns = {KEY_ID, KEY_ISMOVIE};
        String criteria = KEY_ID + " = ?";
        String[] criteriaArgs = {Long.toString(id)};

        Cursor cursor = db.query(TABLE_HEAD, columns, criteria, criteriaArgs , null, null, null);
        cursor.moveToNext();
        int isMovie = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ISMOVIE));
        cursor.close();
        return isMovie == 1;
    }

    public void swapFavoriteValue(long id){
        String[] columns = {KEY_ID, KEY_FAV};
        String criteria = KEY_ID + " = ?";
        String[] criteriaArgs = {Long.toString(id)};

        Cursor cursor = db.query(TABLE_HEAD, columns, criteria, criteriaArgs , null, null, null);
        cursor.moveToNext();
        int favValue = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FAV));

        ContentValues cv = new ContentValues();
        if(favValue == 0){
            cv.put(KEY_FAV, 1);
        } else {
            cv.put(KEY_FAV, 0);
        }
        db.update(TABLE_HEAD, cv, KEY_ID + "= " + id, null);
        cursor.close();
    }

    public void rebuild(){
        List mIds = this.getIds(1);
        List tvIds = this.getIds(0);
        new mReconstructTask().execute(mIds);
        new tvReconstructTask().execute(tvIds);

    }

    public List getIds(int isMovie) {
        List ids = new ArrayList();
        String[] columns = {KEY_ID, KEY_TITLE};
        String criteria = KEY_ID + " = ?";
        String[] criteriaArgs = {Integer.toString(isMovie)};
        Cursor cursor = db.query(TABLE_HEAD, columns, criteria, criteriaArgs, null, null, null);

        cursor.moveToNext();
        //TODO: Check for a proper limitator.
        while(cursor != null) {
            ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            cursor.moveToNext();
        }
        return ids;
    }


    public class mReconstructTask extends AsyncTask<List, Void, Void>{

        @Override
        protected Void doInBackground(List... ints) {
            List ids = ints[0];
            ContentValues cv = new ContentValues();
            TmdbApi api = new TmdbApi(API_KEY);
            TmdbMovies tmdbm = new TmdbMovies(api);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DBcontext);
            String lang = prefs.getString(SettingsFragment.KEY_LOCALE, "en");
            MovieDb movie;

            for(int i=0; i<ids.size(); i++) {
                cv.clear();
                int id = (int)ids.get(i);
                movie = tmdbm.getMovie(id, lang , TmdbMovies.MovieMethod.credits);
                cv.put(KEY_TITLE, movie.getTitle());
                db.update(TABLE_HEAD, cv, KEY_ID + "= " + id, null);
            }

            return null;
        }
    }

    public class tvReconstructTask extends AsyncTask<List, Void, Void>{

        @Override
        protected Void doInBackground(List... ints) {
            List ids = ints[0];
            ContentValues cv = new ContentValues();
            TmdbApi api = new TmdbApi(API_KEY);
            TmdbTV tmdbtv = api.getTvSeries();
            TvSeries tvSeries;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DBcontext);
            String lang = prefs.getString(SettingsFragment.KEY_LOCALE, "en");

            for(int i=0; i<ids.size(); i++) {
                cv.clear();
                int id = (int)ids.get(i);
                tvSeries = tmdbtv.getSeries(id, lang, TmdbTV.TvMethod.credits);
                cv.put(KEY_TITLE, tvSeries.getName());
                db.update(TABLE_HEAD, cv, KEY_ID + "= " + id, null);
            }
            return null;
        }
    }
}
