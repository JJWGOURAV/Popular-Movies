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

        Picasso.with(context).setIndicatorsEnabled(true);

        Picasso.with(context)
                .load(cursor.getString(GridFragment.COL_POSTER_PATH))
                .into(getTarget(cursor.getString(GridFragment.COL_API_ID),iv));
    }

    private static Target getTarget(final String url,final ImageView iv){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {

                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + url);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();

                            iv.setImageBitmap(bitmap);

                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
//                    }
//                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        return target;
    }
}
