package gouravexample.popularmoviesstage1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class GridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        GridView gridView = (GridView) findViewById(R.id.grid_movies);

        List<String> moviesList = getMovies();

        ViewAdapter adapter = new ViewAdapter(GridActivity.this,R.layout.grid_item_movie,moviesList);

        gridView.setAdapter(adapter);
    }

    private List<String> getMovies(){

        List<String> list = new ArrayList<>();
        list.add("demo_image");
        list.add("demo_image");
        list.add("demo_image");
        list.add("demo_image");
        list.add("demo_image");
        list.add("demo_image");
        list.add("demo_image");
        list.add("demo_image");
        list.add("demo_image");

        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
           return true;
    }
}
