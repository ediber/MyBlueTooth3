package com.example.gilharap.mybluetooth3.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gilharap.mybluetooth3.R;
import com.example.gilharap.mybluetooth3.databinding.FragmentConnectBinding;
import com.example.gilharap.mybluetooth3.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ConnectFragment extends Fragment {


    public static final String NAME1 = "name";

    private OnFragmentInteractionListener mListener;
    private String mName;

    @BindView(R.id.connectName)
    TextView mNameView;

    @BindView(R.id.connect)
    View mConnect;

    @BindView(R.id.dissconect)
    View mDissconect;

    @BindView(R.id.start)
    View mStart;

    @BindView(R.id.stop)
    View mStop;

//    MainViewModel viewModel = new MainViewModel(getActivity());

    public ConnectFragment() {
        // Required empty public constructor
    }

    public static ConnectFragment newInstance() {
        ConnectFragment fragment = new ConnectFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(NAME1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_connect, container, false);
        ButterKnife.bind(this, view);

//        MainViewModel viewModel = new MainViewModel(getActivity(), new ViewModelListener());
//        FragmentConnectBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connect, container, false);

        mNameView.setText(mName);

        mConnect.setOnClickListener((View v) -> {
            mListener.onConnect();
        });

        mDissconect.setOnClickListener((View v) -> {
            mListener.onDisConnect();
        });

        mStart.setOnClickListener((View v) -> {
            mListener.onConnect();
        });

        mStop.setOnClickListener((View v) -> {
            mListener.onConnect();
        });

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onConnect();
        void onDisConnect();
        void onStartRequest();
        void onStopRequest();
    }


/*    private class ViewModelListener implements MainViewModel.viewModelListener {
        @Override
        public void onShowPairedDevices(List<String> deviceNames) {

        }

        @Override
        public void showDeviceDetails(String deviceName) {

        }
    }*/
}
