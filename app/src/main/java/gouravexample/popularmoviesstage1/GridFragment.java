package gouravexample.popularmoviesstage1;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import gouravexample.popularmoviesstage1.data.MoviesContract;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
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
            MoviesContract.MovieEntry.COLUMN_API_ID,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH
    };

    //Indices tied to Display columns.
    static final int COL_ID = 0;
    static final int COL_API_ID = 1;
    static final int COL_POSTER_PATH = 2;

    public static final String MOVIE_ITEM = "movieItem";
    private static final String LOG_TAG = GridFragment.class.getSimpleName();
    private String sortOrder="";

    private GridView gridView;
    private MovieAdapter movieAdapter;

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
    public void onActivityCreated(Bundle savedInstanceState) {
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//        sortOrder = sharedPrefs.getString(getString(R.string.sort_order), getString(R.string.popular));
        Log.d(LOG_TAG,"initializing Loader");
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        movieAdapter = new MovieAdapter(getActivity(), null, 0);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grid, container, false);

        gridView = (GridView) rootView.findViewById(R.id.grid_movies);
        gridView.setAdapter(movieAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("movieId",cursor.getString(GridFragment.COL_API_ID));
                    Log.d(LOG_TAG,cursor.getString(GridFragment.COL_API_ID));
                    startActivity(intent);
                }
            }
        });

//        if(savedInstanceState != null){
//            sortOrder = savedInstanceState.getString("sortOrder");
//
//            if(savedInstanceState.getParcelableArrayList("movieItems") != null) {
//                movieItems = savedInstanceState.getParcelableArrayList("movieItems");
//
//                ViewAdapter adapter = new ViewAdapter(getActivity(), 0, movieItems);
//
//                gridView.setAdapter(adapter);
//            }
//        }

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovies();
    }

    private void updateMovies(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String newSortOrder = sharedPrefs.getString(getString(R.string.sort_order), getString(R.string.popular));
        if(!sortOrder.equals(newSortOrder)){
            sortOrder = newSortOrder;
            getLoaderManager().restartLoader(LOADER_ID,null,this);
            if(NetworkUtils.isNetworkAvailable(getContext())) {
                if(sortOrder.equals(getResources().getString(R.string.favorites))){
                    new FetchMovieFavorites(getContext()).execute(sortOrder);
                }else{
                    new FetchMovies(getContext()).execute(sortOrder);
                }
            }
            else
                Snackbar.make(getView().findViewById(R.id.rootLayout), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
//        savedInstanceState.putString("sortOrder",sortOrder);
//        Parcelable[] movieList = new Parcelable[0];
//        savedInstanceState.putParcelableArray("movieItems",movieItems.toArray(movieList));
//        savedInstanceState.putParcelableArrayList("movieItems",(ArrayList<? extends Parcelable>) movieItems);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortString = null;
        String selectionString = null;
        String[] selectionArgs = null;
        Log.d(LOG_TAG,"SortOrder:" + sortOrder);

        if(sortOrder.equals(getResources().getString(R.string.top_rated))){
            sortString = MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC LIMIT 20";
        } else if(sortOrder.equals(getResources().getString(R.string.popular))){
            sortString = MoviesContract.MovieEntry.COLUMN_RELEASE_YEAR + " DESC LIMIT 20";
        } else if(sortOrder.equals(getResources().getString(R.string.favorites))){
            sortString = getResources().getString(R.string.favorites);
        }

        Log.d(LOG_TAG,"SOrtString:" + sortString);

        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                DISPLAY_COLUMNS,
                selectionString,
                selectionArgs,
                sortString);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"Data returned:" + data.getCount());
        movieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        movieAdapter.swapCursor(null);
    }
}
