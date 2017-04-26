package com.example.gilharap.mybluetooth3.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gilharap.mybluetooth3.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.gilharap.mybluetooth3.view.ConnectFragment.State.DISCONNECTED;
import static com.example.gilharap.mybluetooth3.view.ConnectFragment.State.STARTED;
import static com.example.gilharap.mybluetooth3.view.ConnectFragment.State.STOPPED;


public class ConnectFragment extends Fragment {


    public static final String NAME1 = "name";

    private OnFragmentInteractionListener mListener;
    private String mName;
    private State mState;

    @BindView(R.id.connectName)
    TextView mNameView;

    @BindView(R.id.connectVersion)
    TextView mVersionView;

    @BindView(R.id.select)
    View mConnect;

    @BindView(R.id.dissconect)
    View mDissconect;

    @BindView(R.id.start)
    View mStart;

    @BindView(R.id.stop)
    View mStop;

    @BindView(R.id.level)
    Spinner mSpinnerLevel;

    @BindView(R.id.current)
    Spinner mSpinnerCurrent;

    @BindView(R.id.indicatorsLayoutPositive)
    LinearLayout mIndicatorsLayoutPositive;

    @BindView(R.id.indicatorsLayoutNegative)
    LinearLayout mIndicatorsLayoutNegative;




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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_connect, container, false);

        ViewDataBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_connect, container, false);

        View view = binding.getRoot();


        ButterKnife.bind(this, view);

        mNameView.setText(mName);

        setSpinners();

        mConnect.setOnClickListener((View v) -> {
            mListener.onConnect();
        });

        mDissconect.setOnClickListener((View v) -> {
            mListener.onDisConnect();
            setUIState(DISCONNECTED);
        });

        mStart.setOnClickListener((View v) -> {
            mListener.onStartRequest(mSpinnerLevel.getSelectedItemPosition(), mSpinnerCurrent.getSelectedItemPosition());
            setUIState(STARTED);
        });

        mStop.setOnClickListener((View v) -> {
            mListener.onStopRequest();
            setUIState(STOPPED);
        });

        setUIState(DISCONNECTED);

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

    public void updateUILOD(String hex, String binary) {
        for (int i = 0; i < binary.length() / 2; i++) {
            if (binary.charAt(i) == '0') { // attached

                mIndicatorsLayoutPositive.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_green));

//                Log.d(ConstantsUtil.GENERAL_TAG, "positive 0, green");
            } else {

                mIndicatorsLayoutPositive.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_red));

//                Log.d(ConstantsUtil.GENERAL_TAG, "positive 1, red");
            }
//            Log.d(ConstantsUtil.GENERAL_TAG, "index to circle positive: " + i);
        }

        int layoutIndex = 0;
        for (int i = binary.length() / 2; i < binary.length(); i++) {
            if (binary.charAt(i) == '0') { // attached

                mIndicatorsLayoutNegative.getChildAt(layoutIndex).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_green));

//                Log.d(ConstantsUtil.GENERAL_TAG, "negative 0, green");
            } else {

                mIndicatorsLayoutNegative.getChildAt(layoutIndex).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_red));

//                Log.d(ConstantsUtil.GENERAL_TAG, "negative 1, red");
            }

//            Log.d(ConstantsUtil.GENERAL_TAG, "index to circle negative: " + i);

            layoutIndex++;
        }
    }

    public void updateUIVersion(String hex, String payload) {
        mVersionView.setText(hex);
    }

    private void setSpinners() {
        // set spinners
        List<String> levelsLst = new ArrayList(Arrays.asList("P_95_N_5", "P_92_5_N_7_5", "P_90_N_10", "P_87_5_N_12_5", "P_85_N_15", "P_80_N_20", "P_75_N_25", "P_70_N_30"));
        ArrayAdapter<String> levelArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, levelsLst); //selected item will look like a spinner set from XML
        levelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLevel.setAdapter(levelArrayAdapter);

        List currentLst = new ArrayList(Arrays.asList("CURRENT_6_NA", "CURRENT_24_NA", "CURRENT_6_UA", "CURRENT_24_UA"));
        ArrayAdapter<String> currentArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, currentLst); //selected item will look like a spinner set from XML
        currentArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCurrent.setAdapter(currentArrayAdapter);
    }

    public void setUIState(State state) {
        switch (state){
            case DISCONNECTED:
                mConnect.setEnabled(true);
                mStart.setEnabled(false);
                mDissconect.setEnabled(false);
                mStop.setEnabled(false);
                mSpinnerLevel.setEnabled(false);
                mSpinnerCurrent.setEnabled(false);
                setNeutrall(mIndicatorsLayoutPositive);
                setNeutrall(mIndicatorsLayoutNegative);
                break;

            case CONNECTED:
                mConnect.setEnabled(false);
                mStart.setEnabled(true);
                mDissconect.setEnabled(true);
                mStop.setEnabled(false);
                mSpinnerLevel.setEnabled(true);
                mSpinnerCurrent.setEnabled(true);
                break;

            case STARTED:
                mConnect.setEnabled(false);
                mStart.setEnabled(false);
                mDissconect.setEnabled(false);
                mStop.setEnabled(true);
                mSpinnerLevel.setEnabled(false);
                mSpinnerCurrent.setEnabled(false);
                break;

            case STOPPED:
                mConnect.setEnabled(false);
                mStart.setEnabled(true);
                mDissconect.setEnabled(true);
                mStop.setEnabled(false);
                mSpinnerLevel.setEnabled(true);
                mSpinnerCurrent.setEnabled(true);
                setNeutrall(mIndicatorsLayoutPositive);
                setNeutrall(mIndicatorsLayoutNegative);
                break;
        }
    }

    private void setNeutrall(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            layout.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_blue));
        }
    }

    @Override
    public void onStop() {
        mListener.onStopRequest();
        mListener.onDisConnect();
        setUIState(DISCONNECTED);
        super.onStop();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onConnect();

        void onDisConnect();

        void onStartRequest(int selectedItemPosition, int selectedItemPosition1);

        void onStopRequest();
    }

    public enum State{
        DISCONNECTED,
        CONNECTED,
        STARTED,
        STOPPED
    }

}
