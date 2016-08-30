package gouravexample.popularmoviesstage1;


import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import gouravexample.popularmoviesstage1.data.MoviesContract;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int LOADER_ID = 0;

    private static final String[] DISPLAY_COLUMNS = new String[]{

            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_RELEASE_YEAR,
            MoviesContract.MovieEntry.COLUMN_RUNTIME,
            MoviesContract.MovieEntry.COLUMN_VOTE_COUNT,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.TrailerEntry.COLUMN_NAME,
            MoviesContract.TrailerEntry.COLUMN_SIZE,
            MoviesContract.TrailerEntry.COLUMN_SOURCE,
            MoviesContract.TrailerEntry.COLUMN_TYPE
    };

    //Indices tied to Display columns.
    static final int COL_MOVIE_ID = 0;
    static final int COL_POSTER_PATH = 1;
    static final int COL_RELEASE_YEAR = 2;
    static final int COL_RUNTIME = 3;
    static final int COL_VOTE_COUNT = 4;
    static final int COL_VOTE_AVERAGE = 5;
    static final int COL_TITLE = 6;
    static final int COL_OVERVIEW = 7;
    static final int COL_NAME = 8;
    static final int COL_SIZE = 9;
    static final int COL_SOURCE = 10;
    static final int COL_TYPE = 11;


    private String movieId;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private ListView trailerList;
    private boolean isFavorite = false;
    private Button markFavorite;


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
    public static DetailFragment newInstance(String param1, String param2,String movieId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString("movieId",movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            movieId = getArguments().getString("movieId");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("movieItem") != null)
                movieId = savedInstanceState.getParcelable("movieId");
        }

        markFavorite = (Button) rootView.findViewById(R.id.markFavorite);
        markFavorite.setOnClickListener(btnListener);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        if(NetworkUtils.isNetworkAvailable(getContext()))
            new FetchMovieDetails(getContext()).execute(movieId);
        else
            Snackbar.make(getView().findViewById(R.id.rootLayout), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void setData(Cursor movieItem){

        Log.d(LOG_TAG,"Setting Data Item");

        if(movieItem.getCount() > 0){
            movieItem.moveToLast();
        }else{
            return;
        }

        ((TextView)getView().findViewById(R.id.title)).setText(movieItem.getString(COL_TITLE));
        trailerList = (ListView) getView().findViewById(R.id.trailer_list);
        if(movieItem.getString(COL_TITLE).length() > 20){
            ((TextView)getView().findViewById(R.id.title)).setTextSize(28);
        } else if(movieItem.getString(COL_TITLE).length() > 40){
            ((TextView)getView().findViewById(R.id.title)).setTextSize(20);
        }

        ImageView v = (ImageView)getView().findViewById(R.id.image);
        Picasso.with(getActivity())
                .load(movieItem.getString(COL_POSTER_PATH))
                .placeholder(R.drawable.circular)
                .into(v);

        ((TextView)getView().findViewById(R.id.yearOfRelease)).setText(String.valueOf((int)movieItem.getFloat(COL_RELEASE_YEAR)));
        ((TextView)getView().findViewById(R.id.runningTime)).setText(String.valueOf(movieItem.getFloat(COL_RUNTIME)) + " min");
        ((TextView)getView().findViewById(R.id.rating)).setText(movieItem.getFloat(COL_VOTE_AVERAGE) + "/10");
        ((TextView)getView().findViewById(R.id.overview)).setText(movieItem.getString(COL_OVERVIEW));

        movieItem.moveToFirst();
        List<Trailer> trailerItems = new ArrayList<>();

        while(movieItem.moveToNext()){
            Trailer trailer = new Trailer();
            trailer.setName(movieItem.getString(COL_NAME));
            trailer.setName(movieItem.getString(COL_SIZE));
            trailer.setName(movieItem.getString(COL_SOURCE));
            trailer.setName(movieItem.getString(COL_TYPE));

            trailerItems.add(trailer);
        }

        TrailerListAdapter adapter = new TrailerListAdapter(getContext(),0,trailerItems);
        trailerList.setAdapter(adapter);
    }

    private View.OnClickListener btnListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(NetworkUtils.isNetworkAvailable(getContext())) {
                isFavorite = !isFavorite;
                markFavorite.setText(getResources().getString(R.string.unmark_favorite));
                new MarkFavorite(getContext()).execute(movieId, String.valueOf(isFavorite));
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("movieId",movieId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.d(LOG_TAG,"Create Loader:" + movieId);

        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.buildWeatherUri(movieId),
                DISPLAY_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"Count:" + data.getCount());
        setData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
