package com.example.gilharap.mybluetooth3.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gilharap.mybluetooth3.PairedAdapter;
import com.example.gilharap.mybluetooth3.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DevicesFragment extends Fragment {

    public static final String NAMES = "names";
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.devicesRecycler)
    RecyclerView recycler;

    @BindView(R.id.text)
    TextView text;

    private PairedAdapter adapter;

    public DevicesFragment() {
        // Required empty public constructor
    }

    public static DevicesFragment newInstance(String param1, String param2) {
        DevicesFragment fragment = new DevicesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, view);

        String[] names = getArguments().getStringArray(NAMES);
        ArrayList<String> namesList = new ArrayList<>(Arrays.asList(names));

        text.setText("aaa");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(linearLayoutManager);
        adapter = new PairedAdapter(namesList, new SelectListen());
        recycler.setAdapter(adapter);

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
        void onDeviceSelected(int position);
    }

    private class SelectListen implements PairedAdapter.SelectListener {
        @Override
        public void onSelect(int position) {

        }
    }
}
