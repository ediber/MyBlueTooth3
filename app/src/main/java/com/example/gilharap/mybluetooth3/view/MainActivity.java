package com.example.gilharap.mybluetooth3.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.gilharap.mybluetooth3.R;
import com.example.gilharap.mybluetooth3.utils.FragmentSwapper;
import com.example.gilharap.mybluetooth3.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DevicesFragment.OnFragmentInteractionListener, ConnectFragment.OnFragmentInteractionListener {

    @BindView(R.id.frame)
    FrameLayout mFrame;
    @BindView(R.id.progressBar)
    View mProgress;

    private FragmentSwapper mFragmentSwapper;
    private MainViewModel mMainViewModel;
    private ConnectFragment mConnectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMainViewModel = MainViewModel.getInstance(this, new viewModelListen());
        // MainViewModel is singelton and getInstance may not create ne instance with current params
        mMainViewModel.setActivity(this);
        mMainViewModel.setListener(new viewModelListen());

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
        mProgress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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
        public void onShowPairedDevices(List<String> deviceNames, List<String> deviceMacs) {
            Bundle bundle = new Bundle();
            bundle.putStringArray(DevicesFragment.NAMES, deviceNames.toArray(new String[0]));
            bundle.putStringArray(DevicesFragment.MACS, deviceMacs.toArray(new String[0]));
            mFragmentSwapper.swapToFragment(DevicesFragment.class, bundle, R.id.frame, false, true);
        }

        @Override
        public void showDeviceDetails(String deviceName) {
            Bundle bundle = new Bundle();
            bundle.putString(ConnectFragment.NAME1, deviceName);
           mConnectFragment = (ConnectFragment) mFragmentSwapper.swapToFragment(ConnectFragment.class, bundle, R.id.frame, true, true);
//            mFragmentSwapper.addInitialFragment(mConnectFragment, bundle, R.id.mFrame, true, CONNECT_FRAGMENT);
        }

        @Override
        public void onConnectionSuccess() {
            String message = "connection sucess";
            ToastOnUIThread(message);
            mMainViewModel.showVersion();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    mConnectFragment.setUIState(ConnectFragment.State.CONNECTED);
                }
            });

        }

        @Override
        public void onConnectionError() {
            String message = "connection failed";
            ToastOnUIThread(message);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });

        }

        @Override
        public void onDisconnectFailed() {
            ToastOnUIThread("diconnection failed");
        }

        @Override
        public void onUpdateUIFromLOD(String hex, String binary) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mConnectFragment.updateUILOD(hex, binary);
                }
            });

        }

        @Override
        public void onUpdateUIFromVersion(String hex, String payload) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mConnectFragment.updateUIVersion(hex, payload);
                }
            });
        }

        @Override
        public void onStopError(String error) {
            MainActivity.this.runOnUiThread(() -> {
                mConnectFragment.setUIState(ConnectFragment.State.STARTED);
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            });
        }

        @Override
        public void onStartError(String error) {
            MainActivity.this.runOnUiThread(() -> {
                mConnectFragment.setUIState(ConnectFragment.State.CONNECTED);
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
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
