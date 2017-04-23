package com.example.gilharap.mybluetooth3.model;


import com.example.gilharap.mybluetooth3.utils.ConvertUtil;

import java.util.List;



public class ReceiveMessage {
    protected List<Integer> mLst;
    protected int payLoadPosition = 4;

    public ReceiveMessage(List<Integer> lst) {
        mLst = lst;
    }



    public String getPayloadInBinary() {
        String payload = "";

        for (int i = 5; i < mLst.size() - 2; i++) {
            int binaryIndex = mLst.get(i) & 0xFF;
            String binaryStr = ConvertUtil.decimalToBinary(binaryIndex);
            payload = payload + binaryStr;
        }
        return payload;
    }

    public String toHexa() {
        return ConvertUtil.decimalToHexString(mLst);
    }
}
