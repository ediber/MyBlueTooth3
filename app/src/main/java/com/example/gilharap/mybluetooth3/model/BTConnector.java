package com.example.gilharap.mybluetooth3.model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import com.example.gilharap.mybluetooth3.utils.ConstantsUtil;
import com.example.gilharap.mybluetooth3.viewmodel.MainViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class BTConnector {

    private BluetoothSocket mSocket;
    private BluetoothAdapter mBtAdapter;
    private ArrayList<BluetoothDevice> mPairedDevicesList;
    private BluetoothDevice mSelectedDevice;

    public BTConnector() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void createSocket() {
        mSocket = null;
        try {
            // TODO make in new thread
            mSocket = mSelectedDevice.createRfcommSocketToServiceRecord(mSelectedDevice.getUuids()[0].getUuid());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(SocketConnectedListener listener) {
        new ConnectingThread(new SocketConnectedListener() {
            @Override
            public void onConnectionSuccess() {
                listener.onConnectionSuccess();
            }

            @Override
            public void onConnectionError() {
                listener.onConnectionError();
            }
        }).start();
    }

    public void disconnect(SocketDisConnectListener listener) {
        new DisconnectingThread(mSocket, listener).start();
    }

    public void send(SendMessage sendMessage) {
        writeToSocket(sendMessage.toBytes());
    }

    public void listenToIncomingMessages(int receivingMessageSize, MessageReceivedListener listener) {
        new AlreadyConnectedThread(mSocket, receivingMessageSize, listener).start();
    }

    private List<String> showPairedDevices(MainViewModel.viewModelListener listener) {
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        mPairedDevicesList = new ArrayList<>(pairedDevices);

        List<String> deviceNames = devicesToNames(mPairedDevicesList);
        // TODO do it with RXjava

        return deviceNames;
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


    // if disabled ask the user to enable it
    public void showPairedDevicesIfBTEnabled(Activity activity, MainViewModel.viewModelListener listener) {
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

    // Call this from the main activity to mLst data to the remote device.
    private void writeToSocket(byte[] bytes) {

        OutputStream outStream = null;
        try {
            outStream = mSocket.getOutputStream();
        } catch (IOException e) {
            // TODO add listener to ui print
            Log.e("tag", "Error occurred when creating output stream", e);
        }

        try {
            if(outStream != null){
                outStream.write(bytes);
            }

        } catch (IOException e) {
            // TODO add listener to ui print
            Log.e("tag", "Error occurred when sending data", e);
        }
    }



    private class ConnectingThread extends Thread {

        private SocketConnectedListener listener;

        public ConnectingThread(SocketConnectedListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            createSocket();

            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            try {
                mSocket.connect();
                listener.onConnectionSuccess();
            } catch (IOException e) {
                e.printStackTrace();
                listener.onConnectionError();
            }
        }
    }

    private class DisconnectingThread extends Thread {
        private SocketDisConnectListener mListener;

        public DisconnectingThread(BluetoothSocket mSocket, SocketDisConnectListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void run() {
            try {
                mSocket.close();
            } catch (IOException e) {
                mListener.onDisconnectError();
            }
        }
    }

    public class AlreadyConnectedThread extends Thread {

        private int mBufferSize;
        private InputStream mInnput;
//    private Handler mHandler; // handler that gets info from Bluetooth service


        private MessageReceivedListener mListener;

        public AlreadyConnectedThread(BluetoothSocket socket, int bufferSize,  MessageReceivedListener listener) {
            mListener = listener;
            mInnput = null;
            mBufferSize = bufferSize;

            // Get the input and output streams; using temp objects because
            // member streams are final.

            try {
                mInnput = socket.getInputStream();
            } catch (IOException e) {
                Log.e(ConstantsUtil.MY_TAG, "Error occurred when creating input stream", e);
            }

        }

        public void run() {
//            buffer = new byte[mBufferSize];
            byte[] buffer = new byte[ConstantsUtil.BUFFER_SIZE];


            int numBytes; // mLst returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {

//                Arrays.fill(buffer, (byte)0);

                try {
                    // Read from the InputStream.
                    numBytes = mInnput.read(buffer);

                    Log.d(ConstantsUtil.MY_TAG + " 1", "numBytes: " + numBytes);


                    mListener.onReceived(buffer, numBytes);

                } catch (IOException e) {
                    Log.d(ConstantsUtil.MY_TAG, "Input stream was disconnected", e);
                    break;
                }


                /*try {
                    byte[] initBuffer = new byte[3];

                    // Read from the InputStream.
                    numBytes = mInnput.read(initBuffer);
                    Log.d(ConstantsUtil.MY_TAG, "num first Bytes: " + numBytes);
                    Log.d(ConstantsUtil.MY_TAG, " first buffer: " + ConvertUtil.decimalToHexString(initBuffer));

                    int bytesLeftSize = (int)(initBuffer[2] & 0xFF) + 4;


                    byte[] leftBuffer = new byte[bytesLeftSize];
                    numBytes = mInnput.read(leftBuffer);

                    Log.d(ConstantsUtil.MY_TAG, "num left Bytes: " + numBytes);
                    Log.d(ConstantsUtil.MY_TAG, "second buffer: " + ConvertUtil.decimalToHexString(leftBuffer));

                    byte[] both = new byte[initBuffer.length + leftBuffer.length];
                    System.arraycopy(initBuffer, 0, both, 0, initBuffer.length);
                    System.arraycopy(leftBuffer, 0, both, initBuffer.length, leftBuffer.length);

                    mListener.onReceived(both);

                } catch (IOException e) {
                        Log.d(ConstantsUtil.MY_TAG, "Input stream was disconnected", e);
                    break;
                }*/
            }
        }
    }

    public interface SocketConnectedListener {
        void onConnectionSuccess();

        void onConnectionError();
    }

    public interface SocketDisConnectListener {
        void onDisconnectError();
    }

    public interface MessageSentListener {
        void onSent();
    }

    public interface MessageReceivedListener {
        void onReceived(byte[] mBuffer, int numBytes);
    }
}
