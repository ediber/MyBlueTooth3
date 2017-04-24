package com.example.gilharap.mybluetooth3.viewmodel;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.example.gilharap.mybluetooth3.model.BTConnector;
import com.example.gilharap.mybluetooth3.model.LODSendMessage;
import com.example.gilharap.mybluetooth3.model.ReceiveMessage;
import com.example.gilharap.mybluetooth3.model.VersionSendMessage;
import com.example.gilharap.mybluetooth3.utils.ConstantsUtil;
import com.example.gilharap.mybluetooth3.utils.ConvertUtil;

import java.util.List;


public class MainViewModel extends BaseObservable implements ViewModel {

    private Activity mActivity;
    private viewModelListener mListener;
    private BTConnector mConnector;
    private int mRecievedPackets = 0;


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
    public String getRecievedPackets() {
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
        mConnector.listenToIncomingMessages((buffer, numBytes) -> {
            parseMessage(buffer, numBytes);
        });
    }

    private void parseMessage(byte[] buffer, int numBytes) {
        List<Integer> positiveBuffer;
        positiveBuffer = ConvertUtil.bytesToPositive(buffer);

        List<ReceiveMessage> messages = ConvertUtil.bufferToPackets(positiveBuffer, numBytes);

        Log.d(ConstantsUtil.GENERAL_TAG, "  ");


        for (ReceiveMessage message : messages) {
            final String hex = message.toHexa(); // just for test TODO remove later
            String payload = message.getPayloadInBinary();

            switch (message.getmType()) {
                case LOD:
//                Log.d(ConstantsUtil.GENERAL_TAG, " message: " + ConvertUtil.decimalToHexString(message));
                    mListener.onUpdateUIFromLOD(hex, payload);
                    break;

                case VERSION:
                    mListener.onUpdateUIFromVersion(hex, payload);
                    break;
            }

        }
    }


    public void stop() {
        LODSendMessage message = new LODSendMessage();
        message.setStop();
        mConnector.send(message);
    }

    public void showVersion() {
        VersionSendMessage message = new VersionSendMessage();
        message.setStart();
        mConnector.send(message);

        mConnector.listenToIncomingMessages((buffer, numBytes) -> {
            parseMessage(buffer, numBytes);
        });
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


    public interface viewModelListener {
        void onShowPairedDevices(List<String> deviceNames);

        void showDeviceDetails(String deviceName);

        void onConnectionSuccess();

        void onConnectionError();

        void onDisconnectFailed();

        void onUpdateUIFromLOD(String hex, String binary);

        void onUpdateUIFromVersion(String hex, String payload);
    }


}
