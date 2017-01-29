package danielwang.com.visionchef;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class SelectionActivity extends AppCompatActivity {

    //private mImageAdapter = new ImageAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        /*GridView foodGridView = (GridView) findViewById(R.id.food_gridview);
        foodGridView.setAdapter(new ImageAdapter(this));

        foodGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(SelectionActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        }); */

        // Array containing ingredients (should grab from intent)
        String[] test = {"chicken", "tomatoes", "peas"};

        updateRecipes(test, 0);
    }

    // checks to see if there is internet access
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void updateRecipes(String[] ingredients, int firstRecipeNum) {
        if (isOnline()) {
            FetchRecipesTask recipesTask = new FetchRecipesTask(firstRecipeNum);
            recipesTask.execute(ingredients);
        }
    }
}
