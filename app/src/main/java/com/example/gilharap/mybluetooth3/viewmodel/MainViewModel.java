package com.example.gilharap.mybluetooth3.viewmodel;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.gilharap.mybluetooth3.BTConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Gil Harap on 06/04/2017.
 */

public class MainViewModel extends BaseObservable implements ViewModel{



    public interface viewModelListener{
        void onShowPairedDevices(List<String> deviceNames);
        void showDeviceDetails(String deviceName);
    }

    private Activity mActivity;
//    private BluetoothAdapter mBtAdapter;
    private viewModelListener mListener;
//    private ArrayList<BluetoothDevice> mPairedDevicesList;
//    private BluetoothDevice mSelectedDevice;
//    private BluetoothSocket mSocket;
    private BTConnector mConnector;


    public MainViewModel(Activity activity, viewModelListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    @Override
    public void onCreate() {
        // Get the local Bluetooth adapter
//        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnector = new BTConnector();
        showPairedDevices();

        // TODO on activity result
    }

    public void showPairedDevices() {
        mConnector.showPairedDevicesIfBTEnabled(mActivity, mListener);
    }

    @Bindable
    public String getRecievedPackets(){
        return "5";
    }





    public void onDeviceSelected(int position) {
        mConnector.setSelectedDevice(position);
        mListener.showDeviceDetails(mConnector.getSelectedName());
    }


    public void connect() {
        mConnector.connect();
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
