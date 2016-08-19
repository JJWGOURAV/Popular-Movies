package gouravexample.popularmoviesstage1;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private MovieItem movieItem;
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private ListView trailerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if((movieItem = getIntent().getParcelableExtra(GridActivity.MOVIE_ITEM))== null){
            finish();
        }

        setData();

        new FetchMovieDetails().execute();

        trailerList = (ListView) findViewById(R.id.trailer_list);

    }

    private void setData(){
        ((TextView)findViewById(R.id.title)).setText(movieItem.getTitle());

        if(movieItem.getTitle().length() > 20){
            ((TextView)findViewById(R.id.title)).setTextSize(28);
        } else if(movieItem.getTitle().length() > 40){
            ((TextView)findViewById(R.id.title)).setTextSize(20);
        }

        Picasso.with(DetailActivity.this).load(movieItem.getPosterPath()).placeholder(R.drawable.circular).into((ImageView)findViewById(R.id.image));
        ((TextView)findViewById(R.id.yearOfRelease)).setText(String.valueOf(movieItem.getReleaseYear()));
        ((TextView)findViewById(R.id.runningTime)).setText(String.valueOf(movieItem.getRuntime()) + " min");
        ((TextView)findViewById(R.id.rating)).setText(movieItem.getVoteAverage() + "/10");
//        findViewById(R.id.markFavorite)
        ((TextView)findViewById(R.id.overview)).setText(movieItem.getOverview());

    }

    private List<String> getTrailerData(){

        List<String> trailers = new ArrayList<>();

        trailers.add("Trailer 1");
        trailers.add("Trailer 2");
        trailers.add("Trailer 3");

        return trailers;
    }

    private class FetchMovieDetails extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                URL url = new URL("http://api.themoviedb.org/3/movie/" + movieItem.getId() + "?api_key=" + Constants.THEMOVIEDB_API_KEY + "&append_to_response=trailers");

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

//            progressBar.clearAnimation();
//            progressBar.setVisibility(View.GONE);


            JSONDetailParser parser = new JSONDetailParser();
            try {
                DetailActivity.this.movieItem = parser.parseJsonStream(new ByteArrayInputStream(jsonString.getBytes()));

                setData();

                TrailerListAdapter adapter = new TrailerListAdapter(DetailActivity.this,0,movieItem.getTrailerList());
                trailerList.setAdapter(adapter);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    }
}
