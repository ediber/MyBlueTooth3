package com.example.gilharap.mybluetooth3.viewmodel;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;

import static android.R.attr.x;

/**
 * Created by Gil Harap on 06/04/2017.
 */

public class MainViewModel implements ViewModel {


    public interface viewModelListener{
        void showPairedDevices(List<String> deviceNames);
    }

    private Activity mActivity;
    private BluetoothAdapter mBtAdapter;
    private viewModelListener mListener;


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

    private void showPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        List<BluetoothDevice> pairedDevicesList = new ArrayList<>(pairedDevices);

        List<String> deviceNames = devicesToNames(pairedDevicesList);
        // TODO do it with RXjava

        mListener.showPairedDevices(deviceNames);

    }

    private List<String> devicesToNames(List<BluetoothDevice> pairedDevicesList) {
        List<String> names = new ArrayList<>();
        for (BluetoothDevice device : pairedDevicesList) {
            names.add(device.getName());
        }
        return names;
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
