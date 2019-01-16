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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends WearableActivity {

    public static final String DIRECTION = "DIRECTION";
    public static final String MAP_IMAGE = "MAP_IMAGE";
    public static final String ACTION_MAP_RECEIVED = "ACTION_MAP_RECEIVED";
    public static final String START_RECEIVED = "START_RECEIVED";
    public static final String ACTION_START = "ACTION_START";
    public static boolean decision = true;

    public static Boolean start=false;
    private TextView mTextView;
    private TextView startButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);



        // Enables Always-on
        setAmbientEnabled();


        /*LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //GETTING IMAGE OF THE MAP
                //TODO create an imageview with the id wearimageview
                //ImageView imageView = findViewById(R.id.wearImageView);
                byte[] byteArray = intent.getByteArrayExtra(MAP_IMAGE);
                Bitmap bmpMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                //imageView.setImageBitmap(bmpMap);

            }
        }, new IntentFilter(ACTION_MAP_RECEIVED));*/
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //depending on direction_received value, determine where to go
                String start_received = intent.getStringExtra(START_RECEIVED);
                switch (start_received){
                    case "START":
                        decision=false;
                        start=true;
                        //Toast.makeText(MainActivity.this, "sdsav", Toast.LENGTH_LONG).show();

                        onclick_start_wear(new Button(context));

                        break;

                    case "STOP":
                        decision=false;
                        start=false;
                        //Toast.makeText(MainActivity.this, "Loading MAP", Toast.LENGTH_LONG).show();

                        onclick_start_wear(new Button(context));

                    default:
                        //Log.v(TAG, "Start/Stop not transmitted! ");
                        break;
                }
            }


        }, new IntentFilter(ACTION_START));



    }

    //TODO call these functions when we want to start activity, send a message etc...


    //ON CLICK FUNCTIONS TO SEND DATA TO THE APP

    public void onclick_start_wear(View view) {

        Button button1 = findViewById(R.id.button1);

        if(start==false){
            start=true;
            button1.setText(getString(R.string.Stop));
            //button1.setBackgroundColor(getResources().getColor(R.color.DBLUE));

        }
        else{
            start=false;
            button1.setText(getString(R.string.Start));
            //button1.setBackgroundColor(getResources().getColor(R.color.LBLUE));
        }

        if(decision) {
            Intent intent_start = new Intent(MainActivity.this, WearService.class);
            //intent_start.setAction(WearService.ACTION_SEND.SENDSTART.name());
            intent_start.setAction(WearService.ACTION_SEND.DIRECTION.name());
            //intent_start.putExtra(WearService.START, "START");
            intent_start.putExtra(WearService.MESSAGE, "START");
            startService(intent_start);
        }
        decision=true;
    }
    public void onclick_up_wear(View view) {

        if(start==true) {
            Intent intent_up = new Intent(MainActivity.this, WearService.class);
            //intent_start.setAction(WearService.ACTION_SEND.SENDUP.name());
            intent_up.setAction(WearService.ACTION_SEND.DIRECTION.name());
            intent_up.putExtra(WearService.MESSAGE, "UP");
            //intent_up.putExtra(WearService.UP, "UP");
            startService(intent_up);
        }
    }
    public void onclick_down_wear(View view) {
        if(start==true) {
            Intent intent_down = new Intent(MainActivity.this, WearService.class);
            //intent_start.setAction(WearService.ACTION_SEND.SENDDOWN.name());
            //intent_start.putExtra(WearService.DOWN, "DOWN");
            intent_down.setAction(WearService.ACTION_SEND.DIRECTION.name());
            intent_down.putExtra(WearService.MESSAGE, "DOWN");
            startService(intent_down);
        }
    }
    public void onclick_left_wear(View view) {
        if(start==true) {
            Intent intent_left = new Intent(MainActivity.this, WearService.class);
            //intent_start.setAction(WearService.ACTION_SEND.SENDLEFT.name());
            //intent_start.putExtra(WearService.LEFT, "LEFT");
            intent_left.setAction(WearService.ACTION_SEND.DIRECTION.name());
            intent_left.putExtra(WearService.MESSAGE, "LEFT");
            startService(intent_left);
        }
    }
    public void onclick_right_wear(View view) {
        if(start==true) {
            Intent intent_right = new Intent(MainActivity.this, WearService.class);
            //intent_start.setAction(WearService.ACTION_SEND.SENDRIGHT.name());
            //intent_start.putExtra(WearService.RIGHT, "RIGHT");
            intent_right.setAction(WearService.ACTION_SEND.DIRECTION.name());
            intent_right.putExtra(WearService.MESSAGE, "RIGHT");
            startService(intent_right);
        }
    }
    public void onclick_back_wear(View view) {
        if(start==true) {
            Intent intent_back = new Intent(MainActivity.this, WearService.class);
            //intent_start.setAction(WearService.ACTION_SEND.SENDRIGHT.name());
            //intent_start.putExtra(WearService.RIGHT, "RIGHT");
            intent_back.setAction(WearService.ACTION_SEND.DIRECTION.name());
            intent_back.putExtra(WearService.MESSAGE, "BACK");
            startService(intent_back);
        }
    }

}
