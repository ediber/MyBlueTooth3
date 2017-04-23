package com.example.gilharap.mybluetooth3.viewmodel;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.example.gilharap.mybluetooth3.model.BTConnector;
import com.example.gilharap.mybluetooth3.model.LODSendMessage;
import com.example.gilharap.mybluetooth3.model.ReceiveMessage;
import com.example.gilharap.mybluetooth3.utils.ConstantsUtil;
import com.example.gilharap.mybluetooth3.utils.ConvertUtil;

import java.util.List;


public class MainViewModel extends BaseObservable implements ViewModel{



    private Activity mActivity;
    private viewModelListener mListener;
    private BTConnector mConnector;
    private  ConvertUtil mConvertUtil;
    private int mRecievedPackets = 0;


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
        return mRecievedPackets + "";
    }

    public void setRecievedPackets(String packets) {
        this.mRecievedPackets = Integer.parseInt(packets);
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
        LODSendMessage sendMessage = new LODSendMessage();
        sendMessage.setStart();
        sendMessage.addPayload(level);
        sendMessage.addPayload(current);

        mConnector.send(sendMessage);
        mConnector.listenToIncomingMessages(sendMessage.getSize(), (buffer, numBytes) -> {


                List<Integer> positiveBuffer;
                positiveBuffer = ConvertUtil.bytesToPositive(buffer);

                List<ReceiveMessage> messages = ConvertUtil.bufferToPackets(positiveBuffer, numBytes);

                Log.d(ConstantsUtil.MY_TAG, "  ");


                for (ReceiveMessage message: messages) {
//                final String hex = ConvertUtil.decimalToHexString(message); // just for test TODO remove later
                    final String hex = message.toHexa(); // just for test TODO remove later

//                ReceiveMessage receiveMessage = new ReceiveMessage(message);
                    String payload = message.getPayloadInBinary();

//                Log.d(ConstantsUtil.MY_TAG, " message: " + ConvertUtil.decimalToHexString(message));

                    mListener.onUpdateUIFromMessage(hex, payload);
                }


        });
    }



    public void stop() {
        LODSendMessage message = new LODSendMessage();
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
