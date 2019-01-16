package com.example.kandels.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowMapActivity extends AppCompatActivity {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference mapsGetRef = database.getReference("maps");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        //get ID



    }

    /*private void readMap(){
        mapsGetRef.child(mapID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_db = dataSnapshot.child("username").getValue(String.class); String password_db = dataSnapshot.child("password").getValue(String.class); int height_db = dataSnapshot.child("height").getValue(int.class);
                float weight_db = dataSnapshot.child("weight").getValue(float.class); String photo = dataSnapshot.child("photo").getValue(String.class);
                userProfile = new Profile(user_db, password_db); userProfile.password = password_db; userProfile.height_cm = height_db; userProfile.weight_kg = weight_db; userProfile.photoPath = photo;
                setUserImageAndProfileInfo();
            }
    }*/
}
