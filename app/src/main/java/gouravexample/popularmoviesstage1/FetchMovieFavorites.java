package gouravexample.popularmoviesstage1;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import gouravexample.popularmoviesstage1.data.MoviesContract;

/**
 * Created by GOURAV on 30-08-2016.
 */
public class FetchMovieFavorites extends AsyncTask<String,Void,String> {

    private static final String LOG_TAG = FetchMovieDetails.class.getSimpleName();
    private Context context;

    public FetchMovieFavorites(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            URL url = new URL("http://api.themoviedb.org/3/account/id/favorite/movies?api_key=" + Constants.THEMOVIEDB_API_KEY + "&session_id=" + Constants.SESSION_ID);

            Log.d(LOG_TAG,url.toString());
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        Log.d(LOG_TAG,forecastJsonStr);

        return forecastJsonStr;
    }

    @Override
    protected void onPostExecute(String jsonString){

        JSONParser parser = new JSONParser();
        try {

            List<MovieItem> movieItems = parser.parseJsonStream(new ByteArrayInputStream(jsonString.getBytes()));

            //Add these movie Items to DB.
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieItems.size());

            for(MovieItem movieItem: movieItems) {
                // These are the values that will be collected.

                ContentValues movieValues = new ContentValues();

                movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH,movieItem.getPosterPath());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_IS_ADULT,movieItem.isAdult());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW,movieItem.getOverview());
//                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE,MoviesContract.normalizeDate(movieItem.getReleaseDate()));
                movieValues.put(MoviesContract.MovieEntry.COLUMN_GENRE_IDS, Arrays.toString(movieItem.getGenreIds()));
                movieValues.put(MoviesContract.MovieEntry.COLUMN_API_ID,movieItem.getId());
                Log.d(LOG_TAG,movieItem.getId() + " " + movieItem.getReleaseYear());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_IMDB_ID,movieItem.getImdb_id());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIG_TITLE,movieItem.getOriginalTitle());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIG_LANGUAGE,movieItem.getOriginalLanguage());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE,movieItem.getTitle());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH,movieItem.getBackDropPath());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY,movieItem.getPopularity());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT,movieItem.getVoteCount());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,movieItem.getVoteAverage());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_IS_VIDEO, movieItem.isVideo());
                movieValues.put(MoviesContract.MovieEntry.COLUMN_IS_FAVORITE, String.valueOf(true));


                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = context.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
