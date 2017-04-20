package com.example.gilharap.mybluetooth3.viewmodel;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.example.gilharap.mybluetooth3.model.BTConnector;
import com.example.gilharap.mybluetooth3.model.LODMessage;
import com.example.gilharap.mybluetooth3.utils.ConstantsUtil;
import com.example.gilharap.mybluetooth3.utils.ConvertUtil;

import java.util.List;


public class MainViewModel extends BaseObservable implements ViewModel{



    private Activity mActivity;
    private viewModelListener mListener;
    private BTConnector mConnector;
    private  ConvertUtil mConvertUtil;


    public MainViewModel(Activity activity, viewModelListener listener) {
        mActivity = activity;
        mListener = listener;

        mConvertUtil = new ConvertUtil();
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
        mConnector.connect(new BTConnector.SocketConnectedListener() {
            @Override
            public void onConnectionSuccess() {
                mListener.onConnectionSuccess();
            }

            @Override
            public void onConnectionError() {
                mListener.onConnectionError();
            }
        });

    }

    public void disConnect() {
        mConnector.disconnect(new BTConnector.SocketDisConnectListener() {
            @Override
            public void onDisconnectError() {
                mListener.onDisconnectFailed();
            }
        });
    }

    public void start(int level, int current) {
        LODMessage message = new LODMessage();
        message.setStart();
        message.addPayload(level);
        message.addPayload(current);

        mConnector.send(message);
        mConnector.listenToIncomingMessages(message.getSize(), buffer -> {

            List<byte[]> packets = ConvertUtil.bufferToPackets(buffer);


            //////////////////////////////////////////////////////////////////////
            for (byte[] packet: packets) {
                final String hex = ConvertUtil.bytesToHexString(packet); // just for test TODO remove later
                String payload =  mConvertUtil.byteToBinary(packet);

                Log.d(ConstantsUtil.MY_TAG, " packet: " + ConvertUtil.bytesToHexString(packet));

                mListener.onUpdateUIFromMessage(hex, payload);
            }


        });
    }



    public void stop() {
        LODMessage message = new LODMessage();
        message.setStop();
        mConnector.send(message);
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

    public interface viewModelListener{
        void onShowPairedDevices(List<String> deviceNames);
        void showDeviceDetails(String deviceName);

        void onConnectionSuccess();
        void onConnectionError();

        void onDisconnectFailed();

        void onUpdateUIFromMessage(String hex, String binary);
    }


}
