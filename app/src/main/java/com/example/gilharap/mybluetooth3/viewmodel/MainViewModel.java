package com.example.gilharap.mybluetooth3.viewmodel;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.gilharap.mybluetooth3.utils.ConvertUtil;
import com.example.gilharap.mybluetooth3.model.BTConnector;
import com.example.gilharap.mybluetooth3.model.Message;

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
        Message message = new Message();
        message.setStart();
        message.setPayloadCount(2);
        message.addPayload(level);
        message.addPayload(current);

        mConnector.send(message);
        mConnector.listenToIncomingMessages(message.getSize(), buffer -> {
            String payload = "";
            final String hex = ConvertUtil.bytesToHexString(buffer);
            String binary =  mConvertUtil.byteToBinary(buffer, payload);

            mListener.onUpdateUIFromMessage(hex, binary);
        });
    }



    public void stop() {
        Message message = new Message();
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
