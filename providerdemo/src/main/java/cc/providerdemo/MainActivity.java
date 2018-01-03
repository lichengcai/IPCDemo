package cc.providerdemo;

import android.database.ContentObserver;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri uri = Uri.parse("content://cc.providerdemo.lcc.provider");
        getContentResolver().query(uri,null,null,null,null);

    }
}
