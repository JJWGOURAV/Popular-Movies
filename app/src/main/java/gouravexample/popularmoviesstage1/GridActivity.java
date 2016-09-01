package gouravexample.popularmoviesstage1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class GridActivity extends AppCompatActivity implements GridFragment.Callback{

    public static final String MOVIE_ITEM = "movieItem";
    private static final String LOG_TAG = GridActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String sortOrder="";

    private final int requestCode = 1;
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }

        if(findViewById(R.id.fragment_detail_container) != null){
            mTwoPane = true;
        }else{
            mTwoPane = false;
        }

        if(mTwoPane && savedInstanceState == null){
            //Two pane activity started for the first time.
            Bundle bundle = new Bundle();
            bundle.putString("movieId","278");
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_detail_container,fragment, DETAILFRAGMENT_TAG);
            ft.commit();
        }

        Log.d(LOG_TAG,"Two Pane is:" + mTwoPane);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == this.requestCode){
            if(resultCode == RESULT_OK){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.rootLayout,GridFragment.newInstance("",""));
                    fragmentTransaction.commit();
                }
            }else{
                Snackbar.make(findViewById(R.id.rootLayout),"App will not work without this permission", Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    }

    @Override
    public void onItemSelected(String movieId) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putString("movieId", movieId);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("movieId",movieId);
            Log.d(LOG_TAG,movieId);
            startActivity(intent);
        }
    }
}