package gouravexample.popularmoviesstage1;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GridActivity extends AppCompatActivity {

    public static final String MOVIE_ITEM = "movieItem";
    private static final String LOG_TAG = GridActivity.class.getSimpleName();

//    ProgressBar progressBar;
    GridView gridView;
    List<MovieItem> movieItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        gridView = (GridView) findViewById(R.id.grid_movies);

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
//        animation.setDuration (5000); //in milliseconds
//        animation.setInterpolator (new DecelerateInterpolator());
//        animation.start ();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(GridActivity.this,DetailActivity.class);
                intent.putExtra(MOVIE_ITEM,movieItems.get(position));
                startActivity(intent);
            }
        });

    }

    private List<MovieItem> getMovies(){

        List<MovieItem> list = new ArrayList<>();

        MovieItem item = new MovieItem();
        item.setPosterPath("/e1mjopzAS2KNsvpbpahQ1a6SkSn.jpg");
        item.setAdult(false);
        item.setOverview("From DC Comics comes the Suicide Squad, an antihero team of incarcerated supervillains who act as deniable assets for the United States government, undertaking high-risk black ops missions in exchange for commuted prison sentences.");
        item.setReleaseDate("2016-08-03");
        item.setGenreIds(new int[]{28, 80, 878});
        item.setId(297761);
        item.setOriginalTitle("Suicide Squad");
        item.setOriginalLanguage("en");
        item.setTitle("Suicide Squad");
        item.setBackDropPath("\\/ndlQ2Cuc3cjTL7lTynw6I4boP4S.jpg");
        item.setPopularity(61.563586);
        item.setVideo(false);
        item.setVoteCount(924);
        item.setVoteAverage(5.97);

        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);

        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
           return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart(){
        super.onStart();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = sharedPrefs.getString(getString(R.string.sort_order), getString(R.string.popular));

        new FetchMovies().execute(sortOrder);
    }

    private class FetchMovies extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                URL url=null;
                if(params[0].equals(getResources().getString(R.string.popular))){
                    url = new URL("http://api.themoviedb.org/3/movie/popular?api_key=" + Constants.THEMOVIEDB_API_KEY);
                } else if(params[0].equals(getResources().getString(R.string.top_rated))){
                    url = new URL("http://api.themoviedb.org/3/movie/top_rated?api_key=" + Constants.THEMOVIEDB_API_KEY);
                } else {
                    url = new URL("http://api.themoviedb.org/3/movie/top_rated?api_key=" + Constants.THEMOVIEDB_API_KEY);
                }

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

            JSONParser parser = new JSONParser();
            try {
                movieItems = parser.parseJsonStream(new ByteArrayInputStream(jsonString.getBytes()));

                ViewAdapter adapter = new ViewAdapter(GridActivity.this,0,movieItems);

                gridView.setAdapter(adapter);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    }
}
