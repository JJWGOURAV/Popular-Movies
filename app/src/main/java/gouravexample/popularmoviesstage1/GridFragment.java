package gouravexample.popularmoviesstage1;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String MOVIE_ITEM = "movieItem";
    private static final String LOG_TAG = GridFragment.class.getSimpleName();
    private String sortOrder="";

    private GridView gridView;
    private List<MovieItem> movieItems;

    public GridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridFragment newInstance(String param1, String param2) {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grid, container, false);

        gridView = (GridView) rootView.findViewById(R.id.grid_movies);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra(MOVIE_ITEM,movieItems.get(position));
                startActivity(intent);
            }
        });

        if(savedInstanceState != null){
            sortOrder = savedInstanceState.getString("sortOrder");

            if(savedInstanceState.getParcelableArrayList("movieItems") != null) {
                movieItems = savedInstanceState.getParcelableArrayList("movieItems");

                ViewAdapter adapter = new ViewAdapter(getActivity(), 0, movieItems);

                gridView.setAdapter(adapter);
            }
        }

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String newSortOrder = sharedPrefs.getString(getString(R.string.sort_order), getString(R.string.popular));

        if(!sortOrder.equals(newSortOrder) || movieItems==null || movieItems.isEmpty()){
            sortOrder = newSortOrder;
            if(NetworkUtils.isNetworkAvailable(getContext()))
                new FetchMovies().execute(sortOrder);
            else
                Snackbar.make(getView().findViewById(R.id.rootLayout), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("sortOrder",sortOrder);
        Parcelable[] movieList = new Parcelable[0];
//        savedInstanceState.putParcelableArray("movieItems",movieItems.toArray(movieList));
        savedInstanceState.putParcelableArrayList("movieItems",(ArrayList<? extends Parcelable>) movieItems);
        super.onSaveInstanceState(savedInstanceState);
    }

    private class FetchMovies extends AsyncTask<String,Void,String> {

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

            JSONParser parser = new JSONParser();
            try {

                if(jsonString == null){
                    Snackbar.make(getView().findViewById(R.id.rootLayout), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                movieItems = parser.parseJsonStream(new ByteArrayInputStream(jsonString.getBytes()));

                ViewAdapter adapter = new ViewAdapter(getActivity(),0,movieItems);

                gridView.setAdapter(adapter);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    }


}
