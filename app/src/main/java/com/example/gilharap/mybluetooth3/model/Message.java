package com.example.gilharap.mybluetooth3.model;

import com.example.gilharap.mybluetooth3.utils.ConvertUtil;

import java.util.ArrayList;
import java.util.Arrays;


public class Message {

    private ArrayList<Byte> send;
    private int payLoadPosition = 4;


    public void setStart() {
        send = new ArrayList<>(Arrays.asList(
                (byte) 0xDA,
                (byte) 0xDE,
                (byte) 0x00,
                (byte) 0x21,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00
        ));
    }

    public void setStop() {
        send = new ArrayList<>(Arrays.asList(
                (byte) 0xDA,
                (byte) 0xDE,
                (byte) 0x00,
                (byte) 0x22,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00
        ));
    }

    public void setPayloadCount(int count) {
        Byte countByte = ConvertUtil.intToHexByte(count);
        send.set(2, countByte);
    }

    public void addPayload(int num) {
        send.add(payLoadPosition, ConvertUtil.intToHexByte(num));
        payLoadPosition ++;
    }

    public byte[] toBytes() {
        return ConvertUtil.ByteListToArray(send);
    }


    public int getSize() {
        return 9;
    }
}
