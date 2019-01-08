package com.example.kandels.myapplication;

import android.app.FragmentTransaction;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;



public class MainActivity extends AppCompatActivity implements ManualFragment.OnFragmentInteractionListener, AutomaticFragment.OnFragmentInteractionListener{

    private boolean mode;
    private boolean go_back = false;
    public boolean mRunning;
    private final static String TAG = MainActivity.class.getSimpleName();
    Button button_start;

    //BLE
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private ExpandableListView mGattServicesList;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private TextView mDataField;
    private TextView mConnectionState;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";


    public BluetoothGattCharacteristic JackiModeCharacteristic;   // 0000fff1-0000-1000-8000-00805f9b34fb
    public BluetoothGattCharacteristic JackiSwitchCharacteristic; // 0000fff2-0000-1000-8000-00805f9b34fb
    public BluetoothGattCharacteristic JackiPowerCharacteristic;  // 0000fff3-0000-1000-8000-00805f9b34fb
    public BluetoothGattCharacteristic JackiSensorCharacteristic; // 0000fff6-0000-1000-8000-00805f9b34fb

    public final static UUID UUID_ROBOT_SENSOR =
            UUID.fromString(SampleGattAttributes.ROBOT_SENSOR);


    // MAP
    private FrameLayout map;
    private int width = 750;
    private int height = 800;
    private int size_one_element = 5;
    private int nb_el_width = width/size_one_element;
    private int number_square = height * width / (size_one_element * size_one_element);

    List<Node> node_array = new ArrayList<Node>();

    ArrayList<Integer> path_back = new ArrayList<Integer>();

    boolean go_back_ready = false;


    ImageView robot;
    int orientation_robot = LEFT;
    int position_initial = 12075;
    int position_robot = position_initial;

    static final int STATE_UNKNOWN = 0;
    static final int STATE_OBSTACLE = 1;
    static final int STATE_FREE = 2;

    static final int UP = 0;
    static final int RIGHT = 1;
    static final int DOWN = 2;
    static final int LEFT = 3;

    static final int NORTHERN = 0;
    static final int SOUTHERN = 1;
    static final int EASTERN = 2;
    static final int WESTERN = 3;


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new
            ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName componentName,
                                               IBinder
                                                       service) {
                    mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
                    if (!mBluetoothLeService.initialize()) {
                        Log.e(TAG, "Unable to initialize Bluetooth");
                        finish();
                    }
                    // Automatically connects to the device upon successful
                    // start-up
                    // initialization.
                    mBluetoothLeService.connect(mDeviceAddress);
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    mBluetoothLeService = null;
                }
            };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a
    // result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                            if(groupPosition==3) {
                                if(childPosition == 3) {
                                    JackiSensorCharacteristic = mGattCharacteristics.get(3).get(3);
                                }
                            }
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService
                .ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean bError = true;
        mRunning = false;

        // Start robot control screen in response to button
        // start only if connected to a Jacki RSLK with the correct version of ASEE running
        if(mGattCharacteristics.size() > 3) {
            if (mGattCharacteristics.get(3).size() > 3) {
                JackiSensorCharacteristic = mGattCharacteristics.get(3).get(3);
                if (UUID_ROBOT_SENSOR.equals(JackiSensorCharacteristic.getUuid())) {

                    // BLE
                    final Intent intent = getIntent();
                    mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
                    mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
                    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

                    // Sets up UI references.
                    ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
                    mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
                    mGattServicesList.setOnChildClickListener(servicesListClickListner);
                    mConnectionState = (TextView) findViewById(R.id.connection_state);
                    mDataField = (TextView) findViewById(R.id.data_value);


                    bError = false; // ok to Launch RSLK controller
                }
            }
        }

        // No well connected to the robot
        if(bError){
            Toast.makeText(MainActivity.this, "Must be connected to an RSLK to run this app",
                    Toast.LENGTH_SHORT).show();
        }

        // Bottom Fragment
        final Intent intent = getIntent();

        LinearLayout fragContainer = findViewById(R.id.FragmentLinearLayout);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

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


        map = findViewById(R.id.map);
        robot = findViewById(R.id.robot);
        button_start = findViewById(R.id.button_start);

        initialize_map();
        robot.bringToFront();
        rotate(UP);

        change_state_square(position_robot, STATE_FREE);
        handler.post(runnableCode);




    }

    @Override
    protected void onResume() {
        super.onResume();

        //BLE
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //BLE
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothLeService != null) {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
        node_array = null;
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    // New Robot

    private void VerifyConnection(){
        if(JackiSensorCharacteristic==null) {
            displayGattServices(mBluetoothLeService.getSupportedGattServices());
            JackiModeCharacteristic = mGattCharacteristics.get(3).get(0);   // 0000fff1-0000-1000-8000-00805f9b34fb
            JackiSwitchCharacteristic = mGattCharacteristics.get(3).get(1); // 0000fff2-0000-1000-8000-00805f9b34fb
            JackiPowerCharacteristic = mGattCharacteristics.get(3).get(2);  // 0000fff3-0000-1000-8000-00805f9b34fb
            JackiSensorCharacteristic = mGattCharacteristics.get(3).get(3); // 0000fff6-0000-1000-8000-00805f9b34fb
        }
    }

    private void ReadSensors(){
        VerifyConnection();  // back sure Jacki is connected
        //       mBluetoothLeService.readCharacteristic(JackiModeCharacteristic);
        //       mBluetoothLeService.readCharacteristic(JackiSwitchCharacteristic);
        mBluetoothLeService.readCharacteristic(JackiSensorCharacteristic);
    }



    // MAP

    void initialize_map(){
        View square;
        FrameLayout.LayoutParams params;
        int left_margin = - size_one_element;
        int top_margin = 0;
        for(int i=0; i < number_square; i++){
            square = new View(this);
            params = new FrameLayout.LayoutParams(size_one_element, size_one_element);
            square.setLayoutParams(new FrameLayout.LayoutParams(size_one_element, size_one_element));
            square.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            if(left_margin >= width - size_one_element){
                top_margin += size_one_element;
                left_margin = 0;
            }
            else{
                left_margin += size_one_element;
            }
            params.topMargin = top_margin;
            params.leftMargin = left_margin;
            Log.i("index square", Integer.toString(i));
            Node node = new Node(i, get_x_from_index(i), get_y_from_index(i), square);
            node_array.add(i, node);
            map.addView(square, params);
        }

    }

    // 0 = don't know -> gray
    // 1 = obstacle -> red
    // 2 = free -> green
    void change_state_square(int index, int situation){
        Node node = node_array.get(index);

        if(situation == STATE_UNKNOWN){
           node.square.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
           node.State_robot = STATE_UNKNOWN;

        }
        else if(situation == STATE_OBSTACLE){
            node.square.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            node.State_robot = STATE_OBSTACLE;
        }
        else if(situation == STATE_FREE){
            node.square.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            node.setState(Node.NOT_TESTED);
            node.State_robot = STATE_FREE;
        }
    }

    private void rotate(int direction) {
        int degree = (direction - LEFT) * 90;
        /*final RotateAnimation rotateAnim = new RotateAnimation(90 * (orientation_robot - LEFT), degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);*/
        robot.setRotation(degree);

        /*rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        robot.startAnimation(rotateAnim);*/
        orientation_robot = direction;

        set_arrow();



    }

    void set_arrow(){
        int margin_left = get_marginLeft_from_index(position_robot);
        int margin_top = get_marginTop_from_index(position_robot);
        switch(orientation_robot){
            case UP : margin_left -= 8;
                margin_top -= 0;
                break;
            case RIGHT : margin_left -= 14;
                margin_top -= 7;
                break;
            case DOWN : margin_left -= 7;
                margin_top -= 13;
                break;
            case LEFT : margin_left -= 1;
                margin_top -= 6;
                break;
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin_left, margin_top, 0, 0);
        robot.setLayoutParams(params);
    }

    int get_marginLeft_from_index(int index){
        int marginLeft;
        if(index != 0){
            marginLeft = index%nb_el_width * size_one_element;
        }else{
            marginLeft = 0;
        }

        return marginLeft;
    }

    int get_x_from_index(int index){
        int index_x;
        if(index != 0){
            index_x = index%nb_el_width;
        }else{
            index_x = 0;
        }

        return index_x;
    }

    int get_marginTop_from_index(int index){
        int marginTop;
        if(index != 0){
            marginTop = (int) (index / nb_el_width) * size_one_element;
        }else{
            marginTop = 0;
        }
        return marginTop;
    }

    int get_y_from_index(int index){
        int index_y;
        if(index != 0){
            index_y = (int) (index / nb_el_width);
        }else{
            index_y = 0;
        }
        return index_y;
    }




    // A* Algorithm

    int search_next_node(int index_ini, int index_fin, int index_cur){
        Node node_ini = node_array.get(index_ini);
        Node node_fin = node_array.get(index_fin);
        Node node_cur = node_array.get(index_cur);
        node_cur.State = Node.CLOSED;
        int index_new_cur;

        // get 4 neighbors
        int[] index_neighbor = new int[4];
        float smallest_F = number_square;
        float new_F = 0;
        int index_smalest_F = -1;
        for(int i=0; i<4; i++){
           index_neighbor[i] = get_neighbor(index_cur, i);
           if(index_neighbor[i] != -1){
               Node node = node_array.get(index_neighbor[i]);
               if(node.State != Node.CLOSED){
                   boolean change_parent = node.getG_H_F(node_ini, node_fin);
                   if(change_parent){
                       node.ParentNode = node_cur;
                   }
                   new_F = node.F;
                   if(new_F < smallest_F){
                       smallest_F = new_F;
                       index_smalest_F = i;
                   }
               }
           }
        }
        if(index_smalest_F == -1){
            index_new_cur = node_cur.get_index_parent();
        }else{
            index_new_cur = index_neighbor[index_smalest_F];
        }



        return index_new_cur;
    }

    void find_path(int index_ini, int index_fin){
        int index_cur = index_ini;
        while(index_cur != index_fin){
            index_cur = search_next_node(index_ini, index_fin, index_cur);
        }
        Node node;
        path_back.add(index_fin);
        while(index_cur != index_ini){
            node = node_array.get(index_cur).ParentNode;
            index_cur = node.Index[0];
            path_back.add(index_cur);
        }
        for(int i = 0; i < node_array.size(); i++ ){
            node_array.get(i).reinitialize();
        }
        Collections.reverse(path_back);
        path_back.remove(0);
    }


    // if neighbor doesn't exist = -1
    int get_neighbor(int index, int direction){
        int index_neighbor = 0;
        switch(direction){
            case UP : index_neighbor = index - nb_el_width;
                if(index_neighbor < 0){
                    index_neighbor = -1;
                }
                break;
            case RIGHT : index_neighbor = index + 1;
                if(get_y_from_index(index) != get_y_from_index(index_neighbor)){
                    index_neighbor = -1;
                }
                break;
            case DOWN : index_neighbor = index + nb_el_width;
                if(index_neighbor >= number_square){
                    index_neighbor = -1;
                }
                break;
            case LEFT : index_neighbor = index - 1;
                if(get_y_from_index(index) != get_y_from_index(index_neighbor)){
                    index_neighbor = -1;
                }
                break;
        }
        return index_neighbor;
    }

    public void GoBack(View view) {
        Button button_go_back = findViewById(R.id.button_go_back);
        button_start.performClick();
        if(go_back){
            button_go_back.setText(getString(R.string.Go_Back));
            go_back = false;
            path_back.clear();
            for(int i = 0; i < node_array.size(); i++ ){
                node_array.get(i).reinitialize();
            }
        }else{
            button_go_back.setText(getString(R.string.Cancel));
            go_back_ready = false;
            go_back = true;
            find_path(position_robot, position_initial);
            go_back_ready = true;
        }

    }

    int get_orientation(int index_cur, int index_next){
        int difference = index_next - index_cur;
        if(difference == 1){
            return RIGHT;
        }else if(difference == -1){
            return LEFT;
        }else if(difference > 0){
            return DOWN;
        }else if(difference < 0){
            return UP;
        }
        return orientation_robot;
    }




    // MANUAL

    //TODO: put these functions in the fragments automatic and manual

    public void UpMovement(View view) {
        Button button_up = findViewById(R.id.button_up);
        view.setBackgroundColor(Color.YELLOW);
        button_up.setBackgroundColor(getResources().getColor(R.color.OrangeDark));
        byte data[] ={1}; // back command
        rotate(UP);


        /*Button button_down = findViewById(R.id.button_up);
        view.setBackgroundColor(Color.YELLOW);
        button_down.setBackgroundColor(getResources().getColor(R.color.OrangeDark));
        TextView textView = findViewById(R.id.button_start);

        if (!mRunning) {
            textView.setText("Halt");

            //     statusView.setText("Running");
            mRunning = true;
            byte data[] ={1}; // go command
            JackiModeCharacteristic.setValue(data);
            // JackiModeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mBluetoothLeService.writeCharacteristic(JackiModeCharacteristic);
        }else {
            textView.setText("Go");
            //    statusView.setText("Halted");
            mRunning = false;
            byte data[] ={0}; // stop command
            JackiModeCharacteristic.setValue(data);
            // JackiModeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mBluetoothLeService.writeCharacteristic(JackiModeCharacteristic);
        }
        //UpdateSensorStatus();
        //ReadSensors();
        */



        //add movement to firebase, NOT SURE WHERE TO PUT IT
        //addMovementToFirebaseDB();
    }

    public void DownMovement(View view) {
        Button button_down = findViewById(R.id.button_down);
        view.setBackgroundColor(Color.YELLOW);
        button_down.setBackgroundColor(getResources().getColor(R.color.OrangeDark));
        byte data[] ={2}; // back command

        rotate(DOWN);
        /*
        VerifyConnection();  // back sure Jacki is connected
        JackiModeCharacteristic.setValue(data);
        //       JackiModeCharacteristic.setWriteType(JackiModeCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBluetoothLeService.writeCharacteristic(JackiModeCharacteristic);
        //UpdateSensorStatus();
        */
    }

    public void RightMovement(View view) {
        Button button_left = findViewById(R.id.button_right);
        view.setBackgroundColor(Color.YELLOW);
        button_left.setBackgroundColor(getResources().getColor(R.color.OrangeDark));
        //   TextView statusView = findViewById(R.id.status);
        //   statusView.setText("Right");
        byte data[] ={3}; // hard right

        rotate(RIGHT);
        /*
        VerifyConnection();  // back sure Jacki is connected
        JackiModeCharacteristic.setValue(data);
        //  JackiModeCharacteristic.setWriteType(JackiModeCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBluetoothLeService.writeCharacteristic(JackiModeCharacteristic);
        //UpdateSensorStatus();
        // ReadSensors();
        */
    }

    public void LeftMovement(View view) {
        Button button_right = findViewById(R.id.button_left);
        view.setBackgroundColor(Color.YELLOW);
        button_right.setBackgroundColor(getResources().getColor(R.color.OrangeDark));
        //  TextView statusView = findViewById(R.id.status);
        //  statusView.setText("Left");
        byte data[] ={4}; // left command

        rotate(LEFT);
        /*
        VerifyConnection();  // back sure Jacki is connected
        JackiModeCharacteristic.setValue(data);
        // JackiModeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBluetoothLeService.writeCharacteristic(JackiModeCharacteristic);
        //UpdateSensorStatus();
        //     ReadSensors();
        */
    }

    public void move_one_square(){
        if(go_back && go_back_ready){
            if(position_robot != position_initial){
                int index_next = path_back.get(0);
                rotate(get_orientation(position_robot, index_next));
                path_back.remove(0);
                position_robot = index_next;
                set_arrow();
            }else{
                go_back = false;
                button_start.performClick();
            }

        }else if(go_back == false){
            int index_neighbor = get_neighbor(position_robot, orientation_robot);
            if(index_neighbor != -1){
                Node node = node_array.get(index_neighbor);
                if(node.State_robot != STATE_OBSTACLE){
                    position_robot = index_neighbor;
                    change_state_square(index_neighbor, STATE_FREE);

                    set_arrow();

                    float obs = new Random().nextInt(10);
                    if(obs>=9){   //10 percent chance of an obstacle
                        index_neighbor = get_neighbor(index_neighbor, orientation_robot);
                        if(index_neighbor != -1){
                            if(node_array.get(index_neighbor).State_robot == STATE_UNKNOWN){
                                change_state_square(index_neighbor,STATE_OBSTACLE); //create an obstacle
                            }

                        }
                    }
                }

            }


        }
    }


    public void StartMovement(View view) {
        button_start = findViewById(R.id.button_start);
        if(button_start.getText()==getString(R.string.Start)){
            mRunning = true;
            view.setBackgroundColor(Color.RED);
            button_start.setText(getString(R.string.Stop));
        }
        else{
            mRunning = false;

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

   /* private void addProfileToFirebaseDB() {
        profileRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData
                                                            mutableData) {
                mutableData.child("username").setValue(userProfile.username);
                mutableData.child("password").setValue(userProfile.password);
                mutableData.child("height").setValue(userProfile.height_cm);
                mutableData.child("weight").setValue(userProfile.weight_kg);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   boolean b, @Nullable DataSnapshot
                                           dataSnapshot) {
            }
        });
    } */

    // AUTOMATIC

    public void AutomaticMovement(View view) {
        Button button_auto = findViewById(R.id.button_automatic);


        if(button_auto.getText()==getString(R.string.Start)){
            view.setBackgroundColor(Color.RED);
            button_auto.setText(getString(R.string.Stop));
            automatic_exploration(position_robot); //start the automatic exploration mode with the position of the
                                                    // robot: maybe change the position
        }
        else {
            view.setBackgroundColor(getResources().getColor(R.color.Orange));
            button_auto.setText(getString(R.string.Start));
        }
    }



    //TODO: try to find how to go from square to square (once each time)



    public void automatic_exploration(int start) {
        int position = start;

        //Begin at a state free square
        change_state_square(position, STATE_FREE);

        //Solver

       while(mRunning){ //if mrunning goes to false, robot stops

            //Random obstacles: when we arrive to a square, we got a probability that the front square is and obstacle
            float obs = new Random().nextInt(1);

            if(obs>=0.9){   //10 percent chance of an obstacle

                //TODO: Stop the movement and turn:
                change_state_square(position+750/5,STATE_OBSTACLE); //create an obstacle down


                //turn the robot
                RightMovement(findViewById(R.id.button_right)); //turn right for example
                rotate(RIGHT); //rotate arrow in the map
            }

            change_state_square(position, STATE_FREE); //color the square to green
           // TODO: update the position of the robot for the exploration

       }
    }


                /*if(findPath(positionx,positiony, NORTHERN)[2]==true) {
                positiony = NORTH;   //go north with the robot movement function
            }
            else {
                if (findPath(positionx, positiony, SOUTHERN)[2] == true) {
                    positiony = SOUTH;  //rotate the robot and go south ---> TO DO
                }
                if(findPath(positionx,positiony, EASTERN)[2]==true){
                    positionx=EAST;
                }
                if(findPath(positionx,positiony, WESTERN)[2]==true){
                    positionx=WEST;
                }
            }*/

    /*public static boolean[] findPath(int positionx, int positiony, int CASE){

        int pos_NORTH = positiony+1;  //+ one cell from the map
        int pos_SOUTH = positiony-1;
        int pos_EAST = positionx+1;
        int pos_WEST = positiony-1;
        boolean result = false;             //return 1 if it went nort, south etc...

        //int[] position_update = new int[]{positionx, positiony, result};
        boolean[] position_update = new boolean[]{false, false, result};

        switch (CASE){
            case NORTHERN:
                if (pos_NORTH != 0){        //If the position in the north is different than an obstacle
                      position_update[1]=true;                  //NEED TO CHECK THE OBSTACLE POSITIONS!!!!!
                      result=true;
                      break;
                }
            case SOUTHERN:{
                if(pos_SOUTH != 0){
                    position_update[1]=true;
                    result=true;
                    break;
                }
            }
            case WESTERN:{
                if(pos_WEST != 0){
                    position_update[0]=true;
                    result=true;
                    break;
                }
            }
            case EASTERN:{
                if(pos_SOUTH != 0){
                    position_update[0]=true;
                    result=true;
                    break;
                }
            }
        }
        return new boolean[] {position_update[0], position_update[1], result};
    } */

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /// Timer

    Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {

            if(mRunning){
                move_one_square();
            }


            handler.postDelayed(runnableCode, 1000);
        }
    };



}
