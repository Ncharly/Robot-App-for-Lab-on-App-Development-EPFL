package com.example.kandels.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends WearableActivity {

    public static final String DIRECTION = "DIRECTION";
    public static final String MAP_IMAGE = "MAP_IMAGE";
    public static final String ACTION_MAP_RECEIVED = "ACTION_MAP_RECEIVED";

    private TextView mTextView;
    private TextView startButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);


        // Enables Always-on
        setAmbientEnabled();


        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //GETTING IMAGE OF THE MAP
                //TODO create an imageview with the id wearimageview
                //ImageView imageView = findViewById(R.id.wearImageView);
                byte[] byteArray = intent.getByteArrayExtra(MAP_IMAGE);
                Bitmap bmpMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                //imageView.setImageBitmap(bmpMap);

            }
        }, new IntentFilter(ACTION_MAP_RECEIVED));



    }

    //TODO call these functions when we want to start activity, send a message etc...


    //ON CLICK FUNCTIONS TO SEND DATA TO THE APP

    public void onclick_start_wear(View view) {
        Intent intent_start = new Intent(MainActivity.this, WearService.class);
        intent_start.setAction(WearService.ACTION_SEND.SENDSTART.name());
        intent_start.putExtra(WearService.START, "START");
        startService(intent_start);
    }
    public void onclick_up_wear(View view) {
        Intent intent_start = new Intent(MainActivity.this, WearService.class);
        intent_start.setAction(WearService.ACTION_SEND.SENDUP.name());
        intent_start.putExtra(WearService.UP, "UP");
        startService(intent_start);
    }
    public void onclick_down_wear(View view) {
        Intent intent_start = new Intent(MainActivity.this, WearService.class);
        intent_start.setAction(WearService.ACTION_SEND.SENDDOWN.name());
        intent_start.putExtra(WearService.DOWN, "DOWN");
        startService(intent_start);
    }
    public void onclick_left_wear(View view) {
        Intent intent_start = new Intent(MainActivity.this, WearService.class);
        intent_start.setAction(WearService.ACTION_SEND.SENDLEFT.name());
        intent_start.putExtra(WearService.LEFT, "LEFT");
        startService(intent_start);
    }
    public void onclick_right_wear(View view) {
        Intent intent_start = new Intent(MainActivity.this, WearService.class);
        intent_start.setAction(WearService.ACTION_SEND.SENDRIGHT.name());
        intent_start.putExtra(WearService.RIGHT, "RIGHT");
        startService(intent_start);
    }

}
