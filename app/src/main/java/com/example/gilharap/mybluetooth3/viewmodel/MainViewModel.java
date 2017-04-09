package com.example.gilharap.mybluetooth3.viewmodel;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Gil Harap on 06/04/2017.
 */

public class MainViewModel extends BaseObservable implements ViewModel{

    public interface viewModelListener{
        void showPairedDevices(List<String> deviceNames);
        void showDeviceDetails(String deviceName);
    }

    private Activity mActivity;
    private BluetoothAdapter mBtAdapter;
    private viewModelListener mListener;
    private ArrayList<BluetoothDevice> mPairedDevicesList;

    public MainViewModel(Activity activity, viewModelListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    @Override
    public void onCreate() {
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // check ig BT enabled on device
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, 1);
        }
        // TODO on activity result

        showPairedDevices();
    }

    @Bindable
    public String getRecievedPackets(){
        return "5";
    }

    private void showPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        mPairedDevicesList = new ArrayList<>(pairedDevices);

        List<String> deviceNames = devicesToNames(mPairedDevicesList);
        // TODO do it with RXjava

        mListener.showPairedDevices(deviceNames);

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

    public void onDeviceSelected(int position) {
        mListener.showDeviceDetails(mPairedDevicesList.get(position).getName());
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

}
