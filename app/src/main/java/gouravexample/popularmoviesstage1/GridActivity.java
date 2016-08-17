package gouravexample.popularmoviesstage1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        GridView gridView = (GridView) findViewById(R.id.grid_movies);

        List<MovieItem> moviesList = getMovies();

        ViewAdapter adapter = new ViewAdapter(GridActivity.this,0,moviesList);

        gridView.setAdapter(adapter);
    }

    private List<MovieItem> getMovies(){

        List<MovieItem> list = new ArrayList<>();

        MovieItem item = new MovieItem();
        item.setPosterPath("/e1mjopzAS2KNsvpbpahQ1a6SkSn.jpg");
        item.setAdult(false);
        item.setOverview("From DC Comics comes the Suicide Squad, an antihero team of incarcerated supervillains who act as deniable assets for the United States government, undertaking high-risk black ops missions in exchange for commuted prison sentences.");
//        item.setReleaseDate(new Date("2016-08-03"));

        String dateStr = "2016-08-03";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            item.setReleaseDate((Date)formatter.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        item.setGenreIds(new int[]{28, 80, 878});
        item.setId(297761);
        item.setOriginalTitle("Suicide Squad");
        item.setOriginalLanguage("en");
        item.setTitle("Suicide Squad");
        item.setBackDropPath("\\/ndlQ2Cuc3cjTL7lTynw6I4boP4S.jpg");
        item.setPopularity(61.563586);
        item.setVideo(false);
        item.setVoteCount(924);
        item.setVoteAverage(5.97);

        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);

        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
           return true;
    }
}
