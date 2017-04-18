package com.example.gilharap.mybluetooth3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import com.example.gilharap.mybluetooth3.viewmodel.MainViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Gil Harap on 09/04/2017.
 */

public class BTConnector {

    private BluetoothAdapter mBtAdapter;
    private ArrayList<BluetoothDevice> mPairedDevicesList;
    private BluetoothDevice mSelectedDevice;

    public BTConnector() {
                mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void connect(ConnectingThread.SocketConnectedListener listener){

    }

    public List<String> showPairedDevices(MainViewModel.viewModelListener listener) {
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        mPairedDevicesList = new ArrayList<>(pairedDevices);

        List<String> deviceNames = devicesToNames(mPairedDevicesList);
        // TODO do it with RXjava

       return  deviceNames;
    }

    private List<String> devicesToNames(List<BluetoothDevice> pairedDevicesList) {
        List<String> names = new ArrayList<>();

        // to add on min api 24
//                List<String> names = mPairedDevicesList.stream().map(BluetoothDevice::getName).collect(Collectors.toList());

        for (BluetoothDevice device : pairedDevicesList) {
            names.add(device.getName());
        }
        return names;
    }

    public void connect() {

    }

    // if disabled ask the user to enable it
    public void showPairedDevicesIfBTEnabled(Activity activity, MainViewModel.viewModelListener listener){
        // check ig BT enabled on device
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 1);
        } else {
            List<String> deviceNames = showPairedDevices(listener);
            listener.onShowPairedDevices(deviceNames);
        }
    }

    public void setSelectedDevice(int position) {
        mSelectedDevice = mPairedDevicesList.get(position);
    }

    public String getSelectedName() {
        return mSelectedDevice.getName();
    }

    private static class ConnectingThread extends Thread {

        public interface SocketConnectedListener{
            void onConnectionSucess();
            void onConnectionError();
        }

        private static final String TAG = "MY_APP_DEBUG_TAG";

        private SocketConnectedListener listener;
        private BluetoothSocket socket;


        public ConnectingThread(BluetoothSocket socket, SocketConnectedListener listener) {
            this.socket = socket;
            this.listener = listener;
        }

        @Override
        public void run() {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            try {
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                listener.onConnectionError();
            }
            listener.onConnectionSucess();
        }
    }
}
