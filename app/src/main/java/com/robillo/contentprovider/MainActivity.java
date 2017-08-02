package com.robillo.contentprovider;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.name);
        (findViewById(R.id.add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eName = name.getText().toString();
                if(eName.length()>0){
                    ContentValues values = new ContentValues();
                    values.put(MyProvider.name, eName);
                    //noinspection unused
                    Uri uri = getContentResolver().insert(MyProvider.CONTENT_URI, values);
                    Toast.makeText(getApplicationContext(), "NEW CONTACT ADDED", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "NULL FIELD", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
