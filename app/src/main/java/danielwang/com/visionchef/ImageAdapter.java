package danielwang.com.visionchef;

import android.widget.BaseAdapter;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Kevin on 1/28/17.
 */

class ImageAdapter extends BaseAdapter {

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    private JSONArray mRecipes;

    //Constructor
    public ImageAdapter(Context c) {
        mContext = c;
        mRecipes = new JSONArray();
    }

    public JSONArray getRecipes() {
        return mRecipes;
    }

    public void addRecipes(JSONArray recipes) {
        try {
            for (int i = 0; i < recipes.length(); i++) {
                mRecipes.put(recipes.get(i));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    public int getCount() {
        return mRecipes.length();
    }

    public Object getItem (int position) {
        return null;
    }

    public long getItemId (int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.
//                    LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setLayoutParams(new GridView.LayoutParams(650, 650));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }

        if (mRecipes != null) {
            try {
                //Extracting the poster path from JSONObject
                final String IMAGE_PATH_KEY = "image";

                String url = mRecipes.getJSONObject(position).getString(IMAGE_PATH_KEY);

                //Display the poster
                Picasso.with(mContext).load(url).into(imageView);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        } else {
            Picasso.with(mContext).load("http://i.imgur.com/DvpvklR.png").into(imageView);
        }

        return imageView;
    }
}
