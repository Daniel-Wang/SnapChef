package danielwang.com.visionchef;

import android.widget.BaseAdapter;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Kevin on 1/28/17.
 */

class ImageAdapter extends BaseAdapter {

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    public int numElements;
    private JSONArray recipes;
    public int pageNum;

    //Constructor
    public ImageAdapter(Context c) {
        mContext = c;
        numElements = 0;
        recipes = new JSONArray();
        pageNum = 1;
    }

    public int getCount() {
        return numElements;
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
            imageView.setLayoutParams(new GridView.
                    LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            imageView = (ImageView) convertView;
        }

        if (recipes != null) {
            try {
                //Extracting the poster path from JSONObject
                final String IMAGE_PATH_KEY = "image";

                String imagePath = recipes.getJSONObject(position).getString(IMAGE_PATH_KEY);

                String url = imagePath;

                //Display the poster
                Picasso.with(mContext).load(url + "/" + imagePath).into(imageView);

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
