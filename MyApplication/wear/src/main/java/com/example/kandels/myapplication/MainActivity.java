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

                Intent intent_direction = new Intent(MainActivity.this, WearService.class);
                intent_direction.setAction(WearService.ACTION_SEND.DIRECTION.name());


                //NOT sure if will get the right value or not
                String var_dir = intent_direction.getStringExtra("DIRECTION");


                //Convert start button id into string
                startButtonView = findViewById(R.id.button1);
                String buttonText = startButtonView.getText().toString();


                //TODO: switch with de direction variable depending on which button pulsed

                switch (var_dir){
                    case "START":
                        intent_direction.putExtra("START", 0);
                        break;
                    case "UP":
                        intent_direction.putExtra("UP", 1);
                        break;
                    case "DOWN":
                        intent_direction.putExtra("DOWN", 2);
                        break;
                    case "LEFT":
                        intent_direction.putExtra("LEFT", 3);
                        break;
                    case "RIGHT":
                        intent_direction.putExtra("RIGHT", 4);
                        break;
                }
                intent_direction.putExtra(WearService.MESSAGE, var_dir);

                startService(intent_direction);


            }
        }, new IntentFilter(DIRECTION)); //NOT SURE


    }

    //TODO call these functions when we want to start activity, send a message etc...
    

    public void sendMessage(View view) {
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.MESSAGE.name());
        intent.putExtra(WearService.MESSAGE, "Messaging other device!");
        intent.putExtra(WearService.PATH, BuildConfig.W_example_path_text);
        startService(intent);
    }

        /*public void sendStart(View view) {
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
        intent.putExtra(WearService.ACTIVITY_TO_START, BuildConfig.W_mainactivity);
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
