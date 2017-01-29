package danielwang.com.visionchef;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static danielwang.com.visionchef.R.id.food_name;

public class RecipeDetailsActivity extends AppCompatActivity {

    private final String LOG_TAG = RecipeDetailsActivity.class.getSimpleName();
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Intent intent = getIntent();
        JSONObject mRecipe = null;

        try {
            mRecipe = new JSONObject(intent.getStringExtra(Intent.EXTRA_TEXT));
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        String url = "";
        String name = "";
        String ingredientList = "";
        StringBuilder stringified = new StringBuilder();

        try {
            url = mRecipe.getString("image");
            name = mRecipe.getString("label");
            for (int i = 0; i < mRecipe.getJSONArray("ingredientLines").length(); i++) {
                stringified.append("- " + mRecipe.getJSONArray("ingredientLines").getString(i) + "\n");
            }
            ingredientList = stringified.toString();

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        ImageView foodImageView = (ImageView) findViewById(R.id.food_image);
        Picasso.with(this).load(url).into(foodImageView);
        foodImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        TextView foodNameView = (TextView) findViewById(R.id.food_name);
        foodNameView.setText(name);
        TextView foodIngredientsView = (TextView) findViewById(R.id.food_ingredients);
        foodIngredientsView.setText(ingredientList);
    }

    private String getRecipeURL (JSONObject recipe) {
        try {
            return recipe.getString("url");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            return null;
        }
    }

}