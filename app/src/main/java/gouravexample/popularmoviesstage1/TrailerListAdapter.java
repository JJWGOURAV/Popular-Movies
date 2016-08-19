package gouravexample.popularmoviesstage1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by GOURAV on 19-08-2016.
 */
public class TrailerListAdapter extends ArrayAdapter<Trailer> {

    private static final String LOG_TAG = TrailerListAdapter.class.getSimpleName();

    public TrailerListAdapter(Context context, int resource, List<Trailer> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item,null);
        }

        TextView trailerText = (TextView) convertView.findViewById(R.id.trailer_text);
        Trailer item = (Trailer) getItem(position);
        trailerText.setText(item.getName());

        return convertView;
    }
}
