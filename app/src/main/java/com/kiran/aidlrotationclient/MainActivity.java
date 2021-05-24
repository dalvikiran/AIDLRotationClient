package com.kiran.aidlrotationclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kiran.aidlrotationclient.utils.Utils;
import com.kiran.aidlrotationserver.IAIDLRotationCallback;
import com.kiran.aidlrotationserver.IAIDLRotationInterface;
import com.kiran.aidlrotationserver.OrientationDetails;

public class MainActivity extends AppCompatActivity {

    IAIDLRotationInterface rotationService;
    IAIDLRotationCallback rotationCallback;

    TextView pitchTextView;
    TextView rollTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pitchTextView = (TextView) findViewById(R.id.pitchTextView);
        rollTextView = (TextView) findViewById(R.id.rollTextView);

        bindToALService();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (rotationService != null)
            getRotationDetails(rollTextView);
    }

    void bindToALService(){
        Intent intent = new Intent("connect_to_aidl_rotation_service");
        Intent explicitIntent = Utils.implicitIntentToExplicitIntent(intent,this);
        bindService(explicitIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            rotationService = IAIDLRotationInterface.Stub.asInterface(service);
            getRotationDetails(rollTextView);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            rotationService = null;
        }
    };

    public void getRotationDetails(View view){

        try {

            rotationCallback = new IAIDLRotationCallback.Stub() {
                @Override
                public void getRotationDetails(OrientationDetails orientation) throws RemoteException {
                    Log.e("Coordinates", orientation.getPitch());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pitchTextView.setText(orientation.getPitch());
                            rollTextView.setText(orientation.getRoll());
                        }
                    });

                }
            };

            rotationService.getCoordinates(rotationCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            rotationService.stopCoordinates(rotationCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}