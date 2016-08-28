package gouravexample.popularmoviesstage1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import gouravexample.popularmoviesstage1.data.MoviesContract;

/**
 * Created by GOURAV on 28-08-2016.
 */
public class MovieAdapter extends CursorAdapter{

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        LinearLayout ll = (LinearLayout)view;
        ImageView iv = (ImageView) ll.findViewById(R.id.image);

        Picasso.with(context)
                .load(cursor.getString(GridFragment.COL_POSTER_PATH))
                .into(iv);
    }
}
