package com.example.gilharap.mybluetooth3.model;

public class LODMessage extends Message {


    public void setStart() {
        super.setStart();

        send.add((byte) 0x21);
        send.add((byte) 0x00);
        send.add((byte) 0x00);
        send.add((byte) 0x00);
    }

    public void setStop() {
        super.setStart();

        send.add((byte) 0x22);
        send.add((byte) 0x00);
        send.add((byte) 0x00);
        send.add((byte) 0x00);
    }


    @Override
    public int getSize() {
        return 9;
    }
}
