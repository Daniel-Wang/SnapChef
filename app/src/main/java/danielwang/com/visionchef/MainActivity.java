package danielwang.com.visionchef;

import android.content.Intent;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    // ID and Secret can be obtained from https://clarifai.com/
    String key_id = "Insert Client ID here.";
    String key_secret = "Insert Client Secret here.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new ClarifaiBuilder(key_id, key_secret)
                .buildSync();

        imageView = (ImageView) findViewById(R.id.imageView);

//        Intent intent = new Intent();
//        // Show only images, no videos or anything else
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        // Always show the chooser (if there are multiple options available)
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (null == data) return;
        Uri originalUri = null;
        if (requestCode == PICK_IMAGE_REQUEST) {
            originalUri = data.getData();
            Log.e("Data: ", data.getData().toString());
        } else if (requestCode == GALLERY_KITKAT_INTENT_CALLED) {
            originalUri = data.getData();
            Log.e("Data: ", data.getData().toString());
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // Check for the freshest data.
            getContentResolver().takePersistableUriPermission(originalUri, takeFlags);
        }

        Log.e("URI: ", originalUri.toString());
        InputStream iStream = null;
        try {
            iStream = getContentResolver().openInputStream(originalUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            byte[] inputData = getBytes(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        onImagePicked(inputData);
        Log.e("CALLING MODEL", "PREVIOUS");
//        client.getDefaultModels().foodModel().predict().withInputs(ClarifaiInput.forImage(ClarifaiImage.of(new File(originalUri.toString()))));

    }


    private void onImagePicked(@NonNull final byte[] imageBytes) {
        // Now we will upload our image to the Clarifai API
        setBusy(true);

        // Make sure we don't show a list of old concepts while the image is being uploaded
        //        adapter.setData(Collections.<Concept>emptyList());

        new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
            @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
                // The default Clarifai model that identifies concepts in images
                final ConceptModel foodModel = client.getDefaultModels().foodModel();

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
                Log.e("TYPE", predictions.get(0).data().getClass().toString());
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
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

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void setBusy(final boolean busy) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                imageView.setVisibility(busy ? GONE : VISIBLE);
            }
        });
    }

}
