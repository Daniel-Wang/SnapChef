package danielwang.com.visionchef;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class RecipeDetailsActivity extends AppCompatActivity {

    private final String LOG_TAG = RecipeDetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Intent intent = getIntent();

        Log.v(LOG_TAG, "String: " + intent.getStringExtra(Intent.EXTRA_TEXT));
    }
}
