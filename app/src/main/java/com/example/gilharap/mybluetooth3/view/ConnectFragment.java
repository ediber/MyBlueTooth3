package com.example.gilharap.mybluetooth3.view;

import android.content.Context;
import android.os.Bundle;
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


public class ConnectFragment extends Fragment {


    public static final String NAME1 = "name";

    private OnFragmentInteractionListener mListener;
    private String mName;

    @BindView(R.id.connectName)
    TextView mNameView;

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
    LinearLayout mIndicatorsLayout1;

    @BindView(R.id.indicatorsLayoutNegative)
    LinearLayout mIndicatorsLayout2;


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

        mNameView.setText(mName);

        setSpinners();

        mConnect.setOnClickListener((View v) -> {
            mListener.onConnect();
        });

        mDissconect.setOnClickListener((View v) -> {
            mListener.onDisConnect();
        });

        mStart.setOnClickListener((View v) -> {
            mListener.onStartRequest(mSpinnerLevel.getSelectedItemPosition(), mSpinnerCurrent.getSelectedItemPosition());
        });

        mStop.setOnClickListener((View v) -> {
            mListener.onStopRequest();
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

    public void updateUI(String hex, String binary) {
        for(int i=0; i < binary.length()/2; i++){
            if(binary.charAt(i) == '0'){ // attached
                mIndicatorsLayout1.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_green));
//                Log.d(TAG, "0, green");
            } else {
                mIndicatorsLayout2.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_red));
//                Log.d(TAG, "1, red");
            }
        }

        int layoutIndex = 0;
        for(int i = binary.length()/2; i < binary.length(); i++){
            if(binary.charAt(i) == '0'){ // attached
                mIndicatorsLayout2.getChildAt(layoutIndex).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_green));
//                Log.d(TAG, "0, green");
            } else {
                mIndicatorsLayout2.getChildAt(layoutIndex).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_red));
//                Log.d(TAG, "1, red");
            }

            layoutIndex++;
        }
    }

    private void setSpinners() {
        // set spinners
        List<String> levelsLst = new ArrayList(Arrays.asList("P_95_N_5", "P_92_5_N_7_5", "P_90_N_10" ,"P_87_5_N_12_5", "P_85_N_15", "P_80_N_20", "P_75_N_25", "P_70_N_30"));
        ArrayAdapter<String> levelArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, levelsLst); //selected item will look like a spinner set from XML
        levelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLevel.setAdapter(levelArrayAdapter);

        List currentLst = new ArrayList(Arrays.asList("CURRENT_6_NA", "CURRENT_24_NA", "CURRENT_6_UA", "CURRENT_24_UA"));
        ArrayAdapter<String> currentArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, currentLst); //selected item will look like a spinner set from XML
        currentArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCurrent.setAdapter(currentArrayAdapter);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onConnect();
        void onDisConnect();
        void onStartRequest(int selectedItemPosition, int selectedItemPosition1);
        void onStopRequest();
    }

}
