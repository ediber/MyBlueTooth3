package com.example.gilharap.mybluetooth3.viewmodel;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.example.gilharap.mybluetooth3.BR;
import com.example.gilharap.mybluetooth3.model.BTConnector;
import com.example.gilharap.mybluetooth3.model.LODSendMessage;
import com.example.gilharap.mybluetooth3.model.ReceiveMessage;
import com.example.gilharap.mybluetooth3.model.VersionSendMessage;
import com.example.gilharap.mybluetooth3.utils.ConstantsUtil;
import com.example.gilharap.mybluetooth3.utils.ConvertUtil;
import com.example.gilharap.mybluetooth3.utils.DateUtil;

import java.util.Date;
import java.util.List;

import static com.example.gilharap.mybluetooth3.utils.DateUtil.dateToStr;
import static com.example.gilharap.mybluetooth3.utils.DateUtil.getCurrentDate;

/*import com.example.gilharap.mybluetooth3.utils.ConstantsUtil;
import com.example.gilharap.mybluetooth3.utils.ConvertUtil;
import com.example.gilharap.mybluetooth3.utils.DateUtil;*/


public class MainViewModel extends BaseObservable implements ViewModel {

    private static MainViewModel mInstance = null;

    private Activity mActivity;
    private viewModelListener mListener;
    private BTConnector mConnector;
    private int mPacketCounter;
    private String mStartTime;
    private String mElapsedTime;
    private Date mDate;
    private String mTransferRate;


    private MainViewModel(Activity activity, viewModelListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    public static MainViewModel getInstance(Activity activity, viewModelListener listener) {
        if(mInstance == null) {
            mInstance = new MainViewModel(activity, listener);
        }
        return mInstance;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setListener(viewModelListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onCreate() {
        // Get the local Bluetooth adapter
//        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnector = new BTConnector();
        showPairedDevices();
        new ConvertUtil(); // call constructor to initialize ConvertUtil inner list of binaries
    }

    public void showPairedDevices() {
        mConnector.showPairedDevicesIfBTEnabled(mActivity, mListener);
    }

    @Bindable
    public String getPacketCounter() {
        return mPacketCounter + "";
    }

    public void setPacketCounter(String packetCounter) {
        this.mPacketCounter = Integer.parseInt(packetCounter);
        notifyPropertyChanged(BR.packetCounter);
    }

    @Bindable
    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
        notifyPropertyChanged(BR.startTime);
    }

    @Bindable
    public String getElapsedTime() {
        return mElapsedTime;
    }

    public void setElapsedTime(String mElapsedTime) {
        this.mElapsedTime = mElapsedTime;
        notifyPropertyChanged(BR.elapsedTime);
    }

    @Bindable
    public String getTransferRate() {
        return mTransferRate;
    }

    public void setTransferRate(String mTransferRate) {
        this.mTransferRate = mTransferRate;
        notifyPropertyChanged(BR.transferRate);
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
        setPacketCounter("0");
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

        mConnector.send(sendMessage, new BTConnector.MessageSentListener() {
            @Override
            public void onError(String error) {
                mListener.onStartError(error);
            }
        });

        mConnector.listenToIncomingMessages((buffer, numBytes, packetsCounter) -> {
            parseMessage(buffer, numBytes, packetsCounter);
        });

        initializeStartTime();
    }

    private void initializeStartTime() {
        mDate = DateUtil.getCurrentDate();
        setStartTime(dateToStr(mDate, "HH:mm:ss"));
    }

    private void parseMessage(byte[] buffer, int numBytes, int packetsCounter) {
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
                    setPacketCounter(packetsCounter + "");
                    long diffLong = DateUtil.differenceBetweenDatesLong(mDate, getCurrentDate());
                    String diffStr = DateUtil.longToStr(diffLong, "mm:ss");
                    setElapsedTime(diffStr);
                    double transferRate = (double)mPacketCounter / diffLong * 1000;
                    setTransferRate(String.format("%.1f", transferRate));
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
        mConnector.send(message, new BTConnector.MessageSentListener() {
            @Override
            public void onError(String error) {
                mListener.onStopError(error);
            }
        });
        mConnector.clearPacketsCounter();
    }

    public void showVersion() {
        VersionSendMessage message = new VersionSendMessage();
        message.setStart();
        mConnector.send(message, new BTConnector.MessageSentListener() {
            @Override
            public void onError(String error) {

            }
        });

        mConnector.listenToIncomingMessages((buffer, numBytes, packetsCounter) -> {
            parseMessage(buffer, numBytes, packetsCounter);
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

        void onStopError(String error);

        void onStartError(String error);
    }


}
