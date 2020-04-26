package com.example.demoaccident;

import android.Manifest;



import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationManager;
;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.gms.location.DetectedActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DeviceCallback;


public class MainActivity extends AppCompatActivity implements Listener {
    int l;
    Button addc, send;
    StringBuilder a = new StringBuilder();
    LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    ArrayList<String>  contacts= new ArrayList<>()  ;
    Bluetooth bluetooth;
    BluetoothCallback bluetoothCallback;
    private BluetoothDevice device;
    TextView textView;
    Intent scanintent;
    EasyWayLocation easyWayLocation;
    private TextView location, latLong, diff;
    private Double lati, longi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Blue
        l =1;

        send = (Button)findViewById(R.id.send);

//            try {
//                FileOutputStream fileOutputStream = openFileOutput("contacts", Context.MODE_PRIVATE);
//                ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
//
//                out.close();
//                fileOutputStream.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        bluetooth = new Bluetooth(this);
        bluetooth.setBluetoothCallback(bluetoothCallback);
        textView = (TextView) findViewById(R.id.textView);
        scanintent= new Intent(MainActivity.this,scan.class);
        device = getIntent().getParcelableExtra("device");
        bluetooth = new Bluetooth(this);
        bluetooth.setCallbackOnUI(this);

            easyWayLocation = new EasyWayLocation(this);
            easyWayLocation.setListener(this);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);

        }


        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);

        }


        View.OnClickListener newlistner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent();

            }
        };
        addc = (Button) findViewById(R.id.addcontact);
        addc.setOnClickListener(newlistner);
        Button conbuton = (Button)findViewById(R.id.onoff);
        conbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(scanintent);



            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn();
            }
        });

        bluetooth.setDeviceCallback(new DeviceCallback() {
            @Override
            public void onDeviceConnected(BluetoothDevice device) {
                Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceDisconnected(BluetoothDevice device, String message) {
                textView.setText("disconnected");
            }

            @Override
            public void onMessage(String message) {
                a.append(message);
                textView.setText("    "+String.valueOf(l)+"  "+String.valueOf(a));
                if (a.length()>100){a = new StringBuilder();}
                l++;
                if(l ==50){
                    btn();


                }


            }

            @Override
            public void onError(int errorCode) {

            }



            @Override
            public void onConnectError(BluetoothDevice device, String message) {
            }
        });
    }
    @Override
    protected void onStart() {



        super.onStart();
        easyWayLocation.beginUpdates();
        bluetooth.onStart();
        bluetooth.connectToDevice(device);
        if(bluetooth.isEnabled()){

        } else {
            bluetooth.enable();
        }

    }
    public void intent() {
        Intent intent = new Intent(this, Main2Activity.class);


        startActivity(intent);
    }


    public void btn () {
        try {
            FileInputStream inputStream = openFileInput("contacts");
            ObjectInputStream in = new ObjectInputStream(inputStream);
            contacts = (ArrayList<String>) in.readObject();
            in.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();

        }


        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            OnGPS();
        }
        else {
           locationOn();
        }
        if (contacts.size()!=0){
        for (int i =0;i< contacts.size();i++){
        mymsg(contacts.get(i));
        } }
        else{
            Toast.makeText(MainActivity.this, "no contacts ", Toast.LENGTH_SHORT).show();}
    }



    private void OnGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();




    }

    private void mymsg(String a) {

            String mssg = "The rider has met an accident please contact him or get to this location ASAP : " +
                    "http://maps.google.com/?q=" + String.valueOf(lati) + "," + String.valueOf(longi) ;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(a, null, mssg, null, null);
            Toast.makeText(MainActivity.this, "MESSAGE SENT TO " + a, Toast.LENGTH_SHORT).show();







    }
    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }


    @Override
    public void locationOn() {
        Toast.makeText(this, "Location ON", Toast.LENGTH_SHORT).show();
        easyWayLocation.beginUpdates();
        lati = easyWayLocation.getLatitude();
        longi = easyWayLocation.getLongitude();
        textView.setText(lati + "   "+ longi);
    }

    @Override
    public void onPositionChanged() {
        Toast.makeText(this, String.valueOf(easyWayLocation.getLongitude()) + "," + String.valueOf(easyWayLocation.getLatitude()), Toast.LENGTH_SHORT).show();
        textView.setText(lati + "   "+ longi);
    }

    @Override
    public void locationCancelled() {


    }






}
