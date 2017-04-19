package com.example.gilharap.mybluetooth3.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.gilharap.mybluetooth3.utils.FragmentSwapper;
import com.example.gilharap.mybluetooth3.R;
import com.example.gilharap.mybluetooth3.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.gilharap.mybluetooth3.utils.ConstantsUtil.CONNECT_FRAGMENT;

public class MainActivity extends AppCompatActivity implements DevicesFragment.OnFragmentInteractionListener, ConnectFragment.OnFragmentInteractionListener {

    @BindView(R.id.frame)
    FrameLayout frame;

    private FragmentSwapper mFragmentSwapper;
    private MainViewModel mMainViewModel;
    private ConnectFragment mConnectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMainViewModel = new MainViewModel(this, new viewModelListen());

        mFragmentSwapper = FragmentSwapper.getInstance(getSupportFragmentManager());
//        mConnectFragment = ConnectFragment.newInstance();


        mMainViewModel.onCreate();
    }

    @Override
    public void onDeviceSelected(int position) {
        mMainViewModel.onDeviceSelected(position);
    }

    @Override
    public void onConnect() {
        mMainViewModel.connect();
    }

    @Override
    public void onDisConnect() {
        mMainViewModel.disConnect();
    }

    @Override
    public void onStartRequest(int level, int current) {
        mMainViewModel.start(level, current);
    }

    @Override
    public void onStopRequest() {
        mMainViewModel.stop();
    }



    private class viewModelListen implements MainViewModel.viewModelListener {

        @Override
        public void onShowPairedDevices(List<String> deviceNames) {
            Bundle bundle = new Bundle();
            bundle.putStringArray(DevicesFragment.NAMES, deviceNames.toArray(new String[0]));
            mFragmentSwapper.swapToFragment(DevicesFragment.class, bundle, R.id.frame, false, true);
        }

        @Override
        public void showDeviceDetails(String deviceName) {
            Bundle bundle = new Bundle();
            bundle.putString(ConnectFragment.NAME1, deviceName);
           mConnectFragment = (ConnectFragment) mFragmentSwapper.swapToFragment(ConnectFragment.class, bundle, R.id.frame, true, true);
//            mFragmentSwapper.addInitialFragment(mConnectFragment, bundle, R.id.frame, true, CONNECT_FRAGMENT);
        }

        @Override
        public void onConnectionSuccess() {
            String message = "connection sucess";
            ToastOnUIThread(message);
        }

        @Override
        public void onConnectionError() {
            ToastOnUIThread("connection failed");
        }

        @Override
        public void onDisconnectFailed() {
            ToastOnUIThread("diconnection failed");
        }

        @Override
        public void onUpdateUIFromMessage(String hex, String binary) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mConnectFragment.updateUI(hex, binary);
                }
            });

        }
    }

    private void ToastOnUIThread(String message) {
        MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == -1 || requestCode == 1){ // allowed
            mMainViewModel.showPairedDevices();
        }
    }
}
