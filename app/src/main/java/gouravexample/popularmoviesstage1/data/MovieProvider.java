package gouravexample.popularmoviesstage1.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import gouravexample.popularmoviesstage1.R;
import gouravexample.popularmoviesstage1.data.MoviesContract.MovieEntry;
import gouravexample.popularmoviesstage1.data.MoviesContract.MovieFavoriteEntry;
import gouravexample.popularmoviesstage1.data.MoviesContract.TrailerEntry;

/**
 * Created by GOURAV on 28-08-2016.
 */
public class MovieProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    static final int MOVIES = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int TRAILER = 102;
    static final int FAVORITES = 103;

    static SQLiteQueryBuilder queryBuilder;
    static SQLiteQueryBuilder favoriteQueryBuilder;

    static{
        queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieEntry.TABLE_NAME + " LEFT JOIN " +
                TrailerEntry.TABLE_NAME + " ON " +
                TrailerEntry.TABLE_NAME + "." + TrailerEntry.COLUMN_MOVIE_KEY + " = " +
                MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_API_ID + " LEFT JOIN " +
                MovieFavoriteEntry.TABLE_NAME + " ON " +
                MovieFavoriteEntry.TABLE_NAME + "." + MovieFavoriteEntry.COLUMN_API_ID + " = " +
                MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_API_ID);

        favoriteQueryBuilder = new SQLiteQueryBuilder();
        favoriteQueryBuilder.setTables(MovieEntry.TABLE_NAME + " INNER JOIN " +
                MovieFavoriteEntry.TABLE_NAME + " ON " +
                MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_API_ID + " = " +
                MovieFavoriteEntry.TABLE_NAME + "." + MovieFavoriteEntry.COLUMN_API_ID);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)){
            case MOVIES:{
                if(sortOrder != null && sortOrder.equals(getContext().getResources().getString(R.string.favorites)))
                {
                    retCursor = favoriteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                            projection,
                            MovieFavoriteEntry.TABLE_NAME + "." + MovieFavoriteEntry.COLUMN_IS_FAVORITE + " = ?",
                            new String[] {String.valueOf(true)},
                            null,null,null);
                }
                else {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            MovieEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                }
                break;
            }
            case MOVIE_WITH_ID:{

                retCursor = queryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        MovieEntry.COLUMN_API_ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)},
                        null,null,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(retCursor != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {

        int match = sUriMatcher.match(uri);
        switch(match){
            case MOVIES:
                return MovieEntry.CONTENT_DIR_TYPE;
            case MOVIE_WITH_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return TrailerEntry.CONTENT_DIR_TYPE;

        }

        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                normalizeDate(values);
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                normalizeDate(values);
                long _id = db.insert(TrailerEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrailerEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITES: {
                long _id = db.insert(MoviesContract.MovieFavoriteEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.MovieFavoriteEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(
                        TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                normalizeDate(values);
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRAILER:
                normalizeDate(values);
                rowsUpdated = db.update(TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/*", MOVIE_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority,MoviesContract.PATH_MOVIE_FAVORITE,FAVORITES);
//        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);

//        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        return matcher;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(MovieEntry.COLUMN_RELEASE_DATE)) {
            long dateValue = values.getAsLong(MovieEntry.COLUMN_RELEASE_DATE);
            values.put(MovieEntry.COLUMN_RELEASE_DATE, MoviesContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            case MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TRAILER:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case FAVORITES:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
//                        normalizeDate(value);
                        long _id = db.insert(MovieFavoriteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}