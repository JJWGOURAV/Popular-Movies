package gouravexample.popularmoviesstage1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import gouravexample.popularmoviesstage1.data.MoviesContract.MovieEntry;

/**
 * Created by GOURAV on 27-08-2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    public MovieDbHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "(" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_BACKDROP_PATH  + " TEXT , " +
                MovieEntry.COLUMN_BUDGET  + " CHARACTER(20) , " +
                MovieEntry.COLUMN_GENRE_IDS + " TEXT , " +
                MovieEntry.COLUMN_HOMEPAGE + " TEXT  , " +
                MovieEntry.COLUMN_API_ID + " TEXT , " +
                MovieEntry.COLUMN_IMDB_ID + " TEXT  , " +
                MovieEntry.COLUMN_ORIG_LANGUAGE + " TEXT  , " +
                MovieEntry.COLUMN_ORIG_TITLE + " TEXT  , " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT  , " +
                MovieEntry.COLUMN_POPULARITY + " TEXT  , " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT  , " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER  , " +
                MovieEntry.COLUMN_RELEASE_YEAR + " TINYINT  , " +
                MovieEntry.COLUMN_REVENUE + " TEXT  , " +
                MovieEntry.COLUMN_RUNTIME + " TEXT  , " +
                MovieEntry.COLUMN_STATUS + " TEXT  , " +
                MovieEntry.COLUMN_TAGLINE + " TEXT  , " +
                MovieEntry.COLUMN_TITLE + " TEXT  , " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " FLOAT  , " +
                MovieEntry.COLUMN_VOTE_COUNT + " MEDIUMINT  , " +
                MovieEntry.COLUMN_IS_ADULT + " BOOLEAN  , " +
                MovieEntry.COLUMN_IS_VIDEO + " BOOLEAN , " +
                " UNIQUE (" + MovieEntry.COLUMN_API_ID + ", " +
                MovieEntry.COLUMN_IMDB_ID + ") ON CONFLICT REPLACE);";

        Log.d(LOG_TAG,SQL_CREATE_MOVIE_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
