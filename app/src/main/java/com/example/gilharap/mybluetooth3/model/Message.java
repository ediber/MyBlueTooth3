package com.example.gilharap.mybluetooth3.model;


import com.example.gilharap.mybluetooth3.utils.ConvertUtil;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Message{
    protected ArrayList<Byte> send;
    protected int payLoadPosition = 4;
    private int payloadCount = 0;


    public abstract int getSize();

    public byte[] toBytes() {
        return ConvertUtil.ByteListToArray(send);
    }

    public void addPayload(int num) {
        send.add(payLoadPosition, ConvertUtil.intToHexByte(num));
        payLoadPosition ++;
        payloadCount ++;
        setPayloadCount(payloadCount);
    }

    private void setPayloadCount(int count) {
        Byte countByte = ConvertUtil.intToHexByte(count);
        send.set(2, countByte);
    }

    public void setStart() {
        send = new ArrayList<>(Arrays.asList(
                (byte) 0xDA,
                (byte) 0xDE,
                (byte) 0x00
        ));
    }

    public void setStop() {
        send = new ArrayList<>(Arrays.asList(
                (byte) 0xDA,
                (byte) 0xDE,
                (byte) 0x00
        ));
    }
}
