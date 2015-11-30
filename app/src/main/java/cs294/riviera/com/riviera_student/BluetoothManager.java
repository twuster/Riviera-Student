package cs294.riviera.com.riviera_student;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by tonywu on 11/28/15.
 */
public class BluetoothManager {

    public static final int REQUEST_ENABLE_BT = 1;

    private ArrayList mDevicesArray = new ArrayList();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket;
    private Context mContext;

    public BluetoothManager(Context context) {
        mContext = context;
    }

    public void start() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mDevicesArray.add(device.getName() + "\n" + device.getAddress());
                Log.d("DEBUG", device.getName() + "\n" + device.getAddress());
            }
            connect();
        }
    }

    public void connect() {
        // One with headers
//        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("98:76:B6:00:74:A6");
        // One without headers
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("98:76:B6:00:64:4D");

        Log.d("", "Connecting to ... " + device);
        mBluetoothAdapter.cancelDiscovery();
        try {
            btSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            btSocket.connect();
            Toast.makeText(mContext, "Bluetooth connected", Toast.LENGTH_SHORT).show();
            Log.d("", "Connection made.");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.d("", "Unable to end the connection");
            }
            Toast.makeText(mContext, "Bluetooth did not connect", Toast.LENGTH_SHORT).show();
            Log.d("", "Socket creation failed");
        }
    }

    public void writeData(String data) {
        Log.d("DEBUG", "Sending: " + data);
        OutputStream outStream = null;
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.d("Debug", "Bug BEFORE Sending stuff", e);
            Toast.makeText(mContext, "Error sending data. Try reconnecting", Toast.LENGTH_SHORT).show();
        }

        String message = data;
        byte[] msgBuffer = message.getBytes();

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            Log.d("Debug", "Bug while sending stuff", e);
            Toast.makeText(mContext, "Error sending data. Try reconnecting", Toast.LENGTH_SHORT).show();
        }
    }
}
