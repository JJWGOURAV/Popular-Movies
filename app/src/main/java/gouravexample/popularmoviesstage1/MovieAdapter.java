package gouravexample.popularmoviesstage1;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import gouravexample.popularmoviesstage1.data.MoviesContract;

/**
 * Created by GOURAV on 28-08-2016.
 */
public class MovieAdapter extends CursorAdapter{

    private Picasso mBuilder;



    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mBuilder = new Picasso.Builder(context)
                .loggingEnabled(BuildConfig.DEBUG)
                .indicatorsEnabled(BuildConfig.DEBUG)
                .downloader(new OkHttpDownloader(context))
                .build();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        mBuilder.load(cursor.getString(GridFragment.COL_POSTER_PATH)).into(viewHolder.iv);
    }

    public static class ViewHolder{
        ImageView iv;

        public ViewHolder(View view){
            iv = (ImageView) view.findViewById(R.id.image);
        }
    }
}
