package com.example.gilharap.mybluetooth3.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.gilharap.mybluetooth3.FragmentSwapper;
import com.example.gilharap.mybluetooth3.R;
import com.example.gilharap.mybluetooth3.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DevicesFragment.OnFragmentInteractionListener, ConnectFragment.OnFragmentInteractionListener {

    @BindView(R.id.frame)
    FrameLayout frame;

    private FragmentSwapper mFragmentSwapper;
    private MainViewModel mMainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMainViewModel = new MainViewModel(this, new viewModelListen());

        mFragmentSwapper = FragmentSwapper.getInstance(getSupportFragmentManager());


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

    }

    @Override
    public void onStartRequest() {

    }

    @Override
    public void onStopRequest() {

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
            mFragmentSwapper.swapToFragment(ConnectFragment.class, bundle, R.id.frame, true, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == -1 || requestCode == 1){ // allowed
            mMainViewModel.showPairedDevices();
        }
    }
}
