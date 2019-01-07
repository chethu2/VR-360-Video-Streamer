package com.nokia.vr;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class BluetoothConnectionService extends Service {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(enableBluetooth()){
            pairWithController();
        }

        Log.d(TAG, "Bluetooth pairing Started successfully");

        return super.onStartCommand(intent, flags, startId);
    }

    private boolean enableBluetooth(){
        return mBluetoothAdapter.enable();
    }
    private boolean pairWithController(){
        SharedPreferences.Editor editor = getSharedPreferences("VRStreamerPref", MODE_PRIVATE).edit();
        Set<BluetoothDevice> pairedDevices =  mBluetoothAdapter.getBondedDevices();
        Log.i("123456789", "Size is : "+pairedDevices.size());
        for (BluetoothDevice bluetoothDevice: pairedDevices) {
            Log.i("123456789", bluetoothDevice.getName());
            while (bluetoothDevice.getName().contains("BBF5") || bluetoothDevice.getName().contains("08D5" ) || bluetoothDevice.getName().contains("2D69" )) {
                try {
                    Log.d(TAG, "Start Pairing...");
                    Method m = bluetoothDevice.getClass()
                            .getMethod("createBond", (Class[]) null);
                    m.invoke(bluetoothDevice, (Object[]) null);
                    editor.putBoolean("paired", true);
                    editor.apply();
                    Log.d(TAG, "Pairing finished.");
                    return true;

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    editor.putBoolean("paired", false);
                    editor.apply();
                    Log.d(TAG, "Pairing failed");
                    return true;
                }
            }
        }
        return false;
    }
}
