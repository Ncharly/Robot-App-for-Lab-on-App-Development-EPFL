package com.example.kandels.myapplication;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();


    }

    //TODO call these functions when we want to start activity, send a message etc...
    
    /*public void sendStart(View view) {
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
        intent.putExtra(WearService.ACTIVITY_TO_START, BuildConfig.W_mainactivity);
        startService(intent);
    }
    public void sendMessage(View view) {
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.MESSAGE.name());
        intent.putExtra(WearService.MESSAGE, "Messaging other device!");
        intent.putExtra(WearService.PATH, BuildConfig.W_example_path_text);
        startService(intent);
    }
    public void sendDatamap(View view) {
        int some_value = 420;
        ArrayList<Integer> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, 105, 107, 109, 1010);
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.EXAMPLE_DATAMAP.name());
        intent.putExtra(WearService.DATAMAP_INT, some_value);
        intent.putExtra(WearService.DATAMAP_INT_ARRAYLIST, arrayList);
        startService(intent);
    }
    public void sendBitmap(View view) {
// Get bitmap data (can come from elsewhere) and
// convert it to a rescaled asset
        Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.wikipedia_logo);
        Asset asset = WearService.createAssetFromBitmap(bmp);
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.EXAMPLE_ASSET.name());
        intent.putExtra(WearService.IMAGE, asset);
        startService(intent);
    }*/
}
