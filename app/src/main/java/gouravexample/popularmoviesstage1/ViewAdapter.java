package gouravexample.popularmoviesstage1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by GOURAV on 17-08-2016.
 */
public class ViewAdapter extends ArrayAdapter<MovieItem>{

    private static final String LOG_TAG = ViewAdapter.class.getSimpleName();

    public ViewAdapter(Context context, int resource, List<MovieItem> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie,null);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        MovieItem item = (MovieItem)getItem(position);

        Log.d(LOG_TAG,item.getPosterPath());
//        imageView.setImageResource(getContext().getResources().getIdentifier((String)getItem(position), "drawable", getContext().getPackageName()));
        Picasso.with(getContext())
                .load(item.getPosterPath())
                .placeholder(R.drawable.circular)
//                .resize(185,278)
//                .centerCrop()
                .into(imageView);

        return convertView;
    }
}
