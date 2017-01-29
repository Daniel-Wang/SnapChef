package danielwang.com.visionchef;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private int GALLERY_KITKAT_INTENT_CALLED = 2;
    byte[] inputData;
    ClarifaiClient client;
    ImageView imageView;
    String key_id = "GtNeEBJPRzpNmoz1Fpp9P2jpWjNRBT9fQy7cFQct";
    String key_secret = "b6ExeI1T50LVQtYXDjl2Gr37tWsKJgzqZZ0SWkAK";
    String realPath;
    private Cursor cursor;
    //private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new ClarifaiBuilder(key_id, key_secret)
                .buildSync();

        imageView = (ImageView) findViewById(R.id.imageView);

        if (Build.VERSION.SDK_INT <19){
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK && data != null){

            // SDK >= API19
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {


                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0x4);
                } else {
                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            0x4);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }

            } else {
                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
            }

            Uri uriFromPath = Uri.fromFile(new File(realPath));

            // you have two ways to display selected image

            // ( 1 ) imageView.setImageURI(uriFromPath);

            // ( 2 ) imageView.setImageBitmap(bitmap);
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriFromPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
        }

        Bitmap src = BitmapFactory.decodeFile(realPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.PNG, 100, baos);
        inputData = baos.toByteArray();

        Log.e("Yo", inputData.toString());
        onImagePicked(inputData);
        Log.e("CALLING MODEL", "PREVIOUS");
//        client.getDefaultModels().foodModel().predict().withInputs(ClarifaiInput.forImage(ClarifaiImage.of(new File(originalUri.toString()))));

    }

    private void onImagePicked(@NonNull final byte[] imageBytes) {
        // Now we will upload our image to the Clarifai API
        setBusy(true);

        // Make sure we don't show a list of old concepts while the image is being uploaded
        //        adapter.setData(Collections.<Concept>emptyList());

        new AsyncTask<Void, String, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
            private ProgressDialog progDailog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progDailog = new ProgressDialog(MainActivity.this);
                progDailog.setMessage("Loading...");
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(true);
                progDailog.show();
            }

            @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
                // The default Clarifai model that identifies concepts in images
                final ConceptModel foodModel = client.getDefaultModels().foodModel();

                Log.e("Yo", imageBytes.toString());
                // Use this model to predict, with the image that the user just selected as the input
                return foodModel.predict()
                        .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                        .executeSync();
            }

            @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                setBusy(false);
                if (!response.isSuccessful()) {
                    showErrorSnackbar(R.string.error_while_contacting_api);
                    return;
                }
                final List<ClarifaiOutput<Concept>> predictions = response.get();
                if (predictions.isEmpty()) {
                    showErrorSnackbar(R.string.no_results_from_api);
                    return;
                }
//                Log.e("TYPE", predictions.get(0).data().getClass().toString());
//                for(int i = 0; i < predictions.size(); i++){
//                    Log.e("JSON? " + i + ": ", predictions.get(i).data().toString());
//                }
                String result = predictions.get(0).data().toString();
                int counter = 0;
                int index = 0;

                String[] ingredients = new String[4];

                while (counter < 4){
                    int name_index = result.indexOf("name=", index)+5;
                    int comma_index = result.indexOf(",", name_index);
                    String ingred = result.substring(name_index, comma_index);

//                    Log.e("Index of Name: ", String.valueOf(name_index));
//                    Log.e("Index of Comma: ", String.valueOf(comma_index));
//                    Log.e("Ingredient: ", ingred);
                    ingredients[counter] = ingred;

                    counter++;
                    index = comma_index+1;
                }
                progDailog.dismiss();
                //imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
                intent.putExtra("String", ingredients);
                startActivity(intent);

            }

            private void showErrorSnackbar(@StringRes int errorString) {
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        errorString,
                        Snackbar.LENGTH_INDEFINITE
                ).show();
            }
        }.execute();
    }

    private void setBusy(final boolean busy) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                imageView.setVisibility(busy ? GONE : VISIBLE);
            }
        });
    }

}
