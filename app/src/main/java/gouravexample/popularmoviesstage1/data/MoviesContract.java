package gouravexample.popularmoviesstage1.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;
import android.util.Log;

/**
 * Created by GOURAV on 27-08-2016.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "gouravexample.popularmoviesstage1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String LOG_TAG = MoviesContract.class.getSimpleName();

    public static final String PATH_MOVIE = "movies";
    public static final String PATH_TRAILER = "trailer";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_BACKDROP_PATH="backdrop_path";
        public static final String COLUMN_BUDGET="budget";
        public static final String COLUMN_GENRE_IDS="genre_ids";
        public static final String COLUMN_HOMEPAGE="homepage";
        public static final String COLUMN_API_ID="api_id";
        public static final String COLUMN_IMDB_ID="imdb_id";
        public static final String COLUMN_ORIG_LANGUAGE="orig_language";
        public static final String COLUMN_ORIG_TITLE="orig_title";
        public static final String COLUMN_OVERVIEW="overview";
        public static final String COLUMN_POPULARITY="popularity";
        public static final String COLUMN_POSTER_PATH="poster_path";
        public static final String COLUMN_RELEASE_DATE="release_date";
        public static final String COLUMN_RELEASE_YEAR="release_year";
        public static final String COLUMN_REVENUE="revenue";
        public static final String COLUMN_RUNTIME="runtime";
        public static final String COLUMN_STATUS="status";
        public static final String COLUMN_TAGLINE="tagline";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_VOTE_AVERAGE="vote_average";
        public static final String COLUMN_VOTE_COUNT="vote_count";
        public static final String COLUMN_IS_ADULT="is_adult";
        public static final String COLUMN_IS_VIDEO="is_video";
        public static final String COLUMN_IS_FAVORITE="is_favorite";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherUri(String id) {
            Uri uri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(id).build();
            Log.d(LOG_TAG,uri.toString());
            return uri;
        }
    }

    public static final class TrailerEntry implements BaseColumns{

        public static final String TABLE_NAME = "trailer";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_MOVIE_KEY = "movie_key";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
}
