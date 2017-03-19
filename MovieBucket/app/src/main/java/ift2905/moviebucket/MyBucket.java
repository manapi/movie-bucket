package ift2905.moviebucket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MyBucket extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bucket);

        // TODO: Read list from data base, display list items
        // TODO: On click on item, show buttons
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        // TODO: Save list to local data base
    }
}
