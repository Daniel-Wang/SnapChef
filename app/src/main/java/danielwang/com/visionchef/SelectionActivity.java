package danielwang.com.visionchef;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONException;


public class SelectionActivity extends AppCompatActivity {

    public ImageAdapter mImageAdapter;
    private final String LOG_TAG = SelectionActivity.class.getSimpleName();
    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Intent intent = getIntent();
        final String [] test = intent.getStringArrayExtra("String");

        final GridView foodGridView = (GridView) findViewById(R.id.food_gridview);
        mImageAdapter = new ImageAdapter(this);
        foodGridView.setAdapter(mImageAdapter);

        // Array containing ingredients (should grab from intent)  <---- FIX ME
        //final String[] test = {"potatoes", "steak", "wine"};

        updateRecipes(test, mCount, foodGridView);

        try {
            //while (!FetchRecipesTask.flag) {
                Thread.sleep(1000);
            //}
        } catch (InterruptedException e) {

        }

        foodGridView.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if(firstVisibleItem + visibleItemCount >= totalItemCount) {
                    mCount += 3;
                    updateRecipes(test, mCount, foodGridView);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){

            }
        });

        //On Click
        foodGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(SelectionActivity.this, RecipeDetailsActivity.class);
                try {
                    intent.putExtra(intent.EXTRA_TEXT, mImageAdapter.getRecipes().get(position).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
            }
        });

        Log.v(LOG_TAG, "ImageAdapter: " + mImageAdapter.getRecipes());
    }

    // checks to see if there is internet access
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void updateRecipes(String[] ingredients, int firstRecipeNum, GridView foodGridView) {
        if (isOnline()) {
            FetchRecipesTask recipesTask = new FetchRecipesTask(firstRecipeNum, mImageAdapter);
            recipesTask.execute(ingredients);
            mImageAdapter = recipesTask.getImageAdapter();
            Log.v(LOG_TAG, "ImageAdapter: " + mImageAdapter.getRecipes());
            foodGridView.setAdapter(mImageAdapter);
            foodGridView.invalidateViews();
        }
    }
}
