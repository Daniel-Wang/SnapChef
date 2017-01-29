package danielwang.com.visionchef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] ingredients = {"tomatoes", "chicken", "potatoes"};

        Intent intent = new Intent(this, SelectionActivity.class);
        intent.putExtra("String", ingredients);
        startActivity(intent);

    }

}
