package danielwang.com.visionchef;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchSelection(View view) {
        Intent intent = new Intent(this, SelectionActivity.class);

        startActivity(intent);
//        Intent intent = new Intent(this, SelectionActivity.class);
//        startActivity(intent);
    }

}
