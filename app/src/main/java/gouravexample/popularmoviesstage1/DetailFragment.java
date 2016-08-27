package gouravexample.popularmoviesstage1;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MovieItem movieItem;
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private ListView trailerList;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2,MovieItem movieItem) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putParcelable(GridActivity.MOVIE_ITEM,movieItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            movieItem = getArguments().getParcelable(GridActivity.MOVIE_ITEM);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("movieItem") != null)
                movieItem = savedInstanceState.getParcelable("movieItem");
        }

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        if(NetworkUtils.isNetworkAvailable(getContext()))
            new FetchMovieDetails().execute();
        else
            Snackbar.make(getView().findViewById(R.id.rootLayout), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
    }

    private void setData(){
        ((TextView)getView().findViewById(R.id.title)).setText(movieItem.getTitle());
        trailerList = (ListView) getView().findViewById(R.id.trailer_list);
        if(movieItem.getTitle().length() > 20){
            ((TextView)getView().findViewById(R.id.title)).setTextSize(28);
        } else if(movieItem.getTitle().length() > 40){
            ((TextView)getView().findViewById(R.id.title)).setTextSize(20);
        }

        ImageView v = (ImageView)getView().findViewById(R.id.image);
        Picasso.with(getActivity())
                .load(movieItem.getPosterPath())
                .placeholder(R.drawable.circular)
                .into(v);

        ((TextView)getView().findViewById(R.id.yearOfRelease)).setText(String.valueOf(movieItem.getReleaseYear()));
        ((TextView)getView().findViewById(R.id.runningTime)).setText(String.valueOf(movieItem.getRuntime()) + " min");
        ((TextView)getView().findViewById(R.id.rating)).setText(movieItem.getVoteAverage() + "/10");
        ((TextView)getView().findViewById(R.id.overview)).setText(movieItem.getOverview());

        if(movieItem.getTrailerList() != null){
            TrailerListAdapter adapter = new TrailerListAdapter(getContext(),0,movieItem.getTrailerList());
            trailerList.setAdapter(adapter);
        }
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
                if(jsonString == null){
                    Snackbar.make(getView().findViewById(R.id.rootLayout), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                DetailFragment.this.movieItem = parser.parseJsonStream(new ByteArrayInputStream(jsonString.getBytes()));

                setData();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelable("movieItem",movieItem);
        super.onSaveInstanceState(savedInstanceState);
    }

}
