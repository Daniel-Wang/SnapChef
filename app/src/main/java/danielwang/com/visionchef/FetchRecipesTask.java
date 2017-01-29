package danielwang.com.visionchef;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by eric9 on 2017-01-28.
 */

class FetchRecipesTask extends AsyncTask<String[], Void, JSONArray> {

    private final String LOG_TAG = FetchRecipesTask.class.getSimpleName();

    @Override
    protected JSONArray doInBackground(String[]... ingredients) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain raw JSON response as a String.
        String recipesJSONstr = null;

        try {
            // Constructing the URL
            final String RECIPE_BASE_URL = "https://api.edamam.com/search";
            final String APP_ID_PARAM = "app_id";
            final String APP_ID = "13746454";
            final String APP_KEY_PARAM = "app_key";
            final String APP_KEY = "57da5853d1c482fde9312a2ff7537d41";
            final String APP_INGREDIENTS_PARAM = "q";
            final String APP_FROM_PARAM = "from";
            final String APP_TO_PARAM = "to";
            String APP_INGREDIENTS = ingredients[0][0];

            for (int i = 1; i < ingredients[0].length; i++) {
                APP_INGREDIENTS += " " + ingredients[0][i];
            }

            Log.v(LOG_TAG, "ingredients: " + APP_INGREDIENTS);

            Uri builtUri = Uri.parse(RECIPE_BASE_URL).buildUpon()
                    .appendQueryParameter(APP_INGREDIENTS_PARAM, APP_INGREDIENTS)
                    .appendQueryParameter(APP_ID_PARAM, APP_ID)
                    .appendQueryParameter(APP_KEY_PARAM, APP_KEY)
                    .appendQueryParameter(APP_FROM_PARAM, "0")
                    .appendQueryParameter(APP_TO_PARAM, "3")
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Build URI: " + url);

            // Create the request to Edamam, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            final String RECIPE_TAG = "\"recipe\"";
            boolean inRecipe = false;
            String line;
            buffer.append("{ \"data\": [\n");

            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.

                if (line.contains(RECIPE_TAG)) {
                    buffer.append("{\n");
                    inRecipe = true;
                } else if (inRecipe) {
                    buffer.append(filterJSON(line));
                }

                if (line.contains("ingredientLines")) {
                    buffer.setLength(buffer.length() - 1);
                    buffer.append("},\n");
                    inRecipe = false;
                }
            }
            buffer.setLength(buffer.length() - 2);
            buffer.append("\n]\n}");

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            recipesJSONstr = buffer.toString();

            Log.v(LOG_TAG, "JSON String: " + recipesJSONstr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the recipe data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }

        try {
            JSONObject recipesObject = new JSONObject(recipesJSONstr);
            return recipesObject.getJSONArray("data");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONArray recipesArray) {
        super.onPostExecute(recipesArray);
    }

    /*
     * Returns line if it is needed, and returns the empty string otherwise.
     */
    protected String filterJSON(String line) {
        // Necessary fields
        String[] filterTags = {"\"label\"", "\"image\"","\"url\"", "\"ingredientLines\""};

        boolean keep = false;
        for (int i = 0; i < filterTags.length; i++) {
            if (line.contains(filterTags[i])) {
                keep = true;
            }
        }

        if (keep) {
            return line;
        }
        return "";
    }
}
