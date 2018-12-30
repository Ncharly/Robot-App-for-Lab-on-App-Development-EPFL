package com.example.kandels.myapplication;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements ManualFragment.OnFragmentInteractionListener, AutomaticFragment.OnFragmentInteractionListener{

    private boolean mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout fragContainer = findViewById(R.id.FragmentLinearLayout);
        FragmentTransaction ft = getFragmentManager().beginTransaction();


        Intent intent = getIntent();
        mode = intent.getBooleanExtra(ManualFragment.MANUAL, true);

        if(mode) {
            ManualFragment myManualFragment;
            myManualFragment = new ManualFragment();
            ft.add(fragContainer.getId(), myManualFragment, null);
        }

        else {
            AutomaticFragment myAutomaticFragment;
            myAutomaticFragment = new AutomaticFragment();
            ft.add(fragContainer.getId(), myAutomaticFragment, null);
        }

        ft.commit();
    }

    public void UpMovement(View view) {
        Button button_down = findViewById(R.id.button_down);
        view.setBackgroundColor(Color.YELLOW);
        button_down.setBackgroundColor(getResources().getColor(R.color.OrangeDark));
    }

    public void DownMovement(View view) {
        Button button_up = findViewById(R.id.button_up);
        view.setBackgroundColor(Color.YELLOW);
        button_up.setBackgroundColor(getResources().getColor(R.color.OrangeDark));
    }

    public void RightMovement(View view) {
        Button button_left = findViewById(R.id.button_left);
        view.setBackgroundColor(Color.YELLOW);
        button_left.setBackgroundColor(getResources().getColor(R.color.OrangeDark));
    }

    public void LeftMovement(View view) {
        Button button_right = findViewById(R.id.button_right);
        view.setBackgroundColor(Color.YELLOW);
        button_right.setBackgroundColor(getResources().getColor(R.color.OrangeDark));
    }

    public void StartMovement(View view) {
        Button button_start = findViewById(R.id.button_start);
        if(button_start.getText()==getString(R.string.Start)){
            view.setBackgroundColor(Color.RED);
            button_start.setText(getString(R.string.Stop));
        }
        else{
            Button button_right = findViewById(R.id.button_right);
            button_right.setBackgroundColor(getResources().getColor(R.color.OrangeDark));

            Button button_left = findViewById(R.id.button_left);
            button_left.setBackgroundColor(getResources().getColor(R.color.OrangeDark));

            Button button_up = findViewById(R.id.button_up);
            button_up.setBackgroundColor(getResources().getColor(R.color.OrangeDark));

            Button button_down = findViewById(R.id.button_down);
            button_down.setBackgroundColor(getResources().getColor(R.color.OrangeDark));

            view.setBackgroundColor(getResources().getColor(R.color.Orange));
            button_start.setText(getString(R.string.Start));
        }

    }

    public void AutomaticMovement(View view) {
        Button button_auto = findViewById(R.id.button_auto);

        if(button_auto.getText()==getString(R.string.Start)){
            view.setBackgroundColor(Color.RED);
            button_auto.setText(getString(R.string.Stop));
        }
        else {
            view.setBackgroundColor(getResources().getColor(R.color.Orange));
            button_auto.setText(getString(R.string.Start));
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
