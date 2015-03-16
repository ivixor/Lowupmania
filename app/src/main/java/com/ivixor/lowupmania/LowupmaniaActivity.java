package com.ivixor.lowupmania;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class LowupmaniaActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lowupmania);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        String result = getIntent().getExtras().getString("result");
        //((TextView) findViewById(R.id.textView)).setText(result.substring(0, 20));
        Log.d("vk_res", result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 42) {
            String result = data.getStringExtra("result");
            ((TextView) findViewById(R.id.textView)).setText(result);
        }
    }
}
