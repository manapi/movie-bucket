package ift2905.moviebucket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MyHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);

        // TODO: Read history from data base, display items
        // TODO: On click on item, show buttons
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        // TODO: Save history to local data base
    }
}
