package com.example.gilharap.mybluetooth3.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.gilharap.mybluetooth3.model.ReceiveMessage;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtil {

    private static ArrayList<String> mBinaries;
    private static List<Integer> mLeft = new ArrayList<>(); // left over from previous iteration

    public ConvertUtil() {
        generateTableOfAllBinaries();
    }

    public static Byte intToHexByte(int num) {
        String ans = Integer.toHexString(num);
        return Byte.parseByte(ans);
    }

    private static String intToBinaryString(int num) {
        String ans = Integer.toBinaryString(num);
        for (int i = ans.length(); i < 8; i++) {
            ans = "0" + ans;
        }
        return ans;
    }

    public static String decimalToHexString(List<Integer> lst) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[lst.size() * 3];
        for (int j = 0; j < lst.size(); j++) {
            int v = lst.get(j);
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    public static byte[] ByteListToArray(ArrayList<Byte> list) {
        Byte[] objects = list.toArray(new Byte[list.size()]);
        byte[] bytes = new byte[objects.length];
        int i = 0;
        for (Byte b : objects) {
            bytes[i++] = b.byteValue();
        }
        return bytes;
    }


    private void generateTableOfAllBinaries() {
        mBinaries = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            String binary = intToBinaryString(i);
            mBinaries.add(binary);
        }
    }

   /* public String byteToBinary(byte[] buffer) {
        // payload to binary
        String payload = "";

        for (int i = 5; i < buffer.length - 2; i++) {
            int binaryindex = buffer[i] & 0xFF;
            String binaryStr = mBinaries.get(binaryindex);
            payload = payload + binaryStr;
        }
        return payload;
    }*/

    public static String decimalToBinary(int dec) {
        return mBinaries.get(dec);
    }

    public static List<ReceiveMessage> bufferToPackets(List<Integer> buffer, int numBytes) {
        List<ReceiveMessage> lst = new ArrayList<>();
        int from = 0;
        int to;

        Log.d(ConstantsUtil.GENERAL_TAG + " 2", "current buffer: " + ConvertUtil.decimalToHexString(buffer));
        Log.d(ConstantsUtil.GENERAL_TAG + " 3", "left buffer initial: " + ConvertUtil.decimalToHexString(mLeft));

        numBytes = numBytes + mLeft.size(); // updating to number of bytes for combined list

        mLeft.addAll(buffer); // adding left overs from previous buffer to the start
        buffer = deepCopy(mLeft);
//        buffer = mLeft;
        mLeft.clear();


        for (int i = 0; i < numBytes - 1; i++) {

            if ((buffer.get(i)) == 0xDA && (buffer.get(i + 1)) == 0xDE) {
                int sizeIndex = from + ConstantsUtil.DA_DE_SIZE;
                int messageLength = ConstantsUtil.DA_DE_SIZE + 3 + buffer.get(sizeIndex) + 2;
                to = from + messageLength - 1;

                if (to >= numBytes) { // message is not full
                    Log.d(ConstantsUtil.GENERAL_TAG + " 4", "to index: " + to);
                    Log.d(ConstantsUtil.GENERAL_TAG + " 4", "numBytes - 1 index: " + (numBytes - 1));
                    List<Integer> packet = buffer.subList(from, numBytes);
                    Log.d(ConstantsUtil.GENERAL_TAG + " 5", "too short packet: " + ConvertUtil.decimalToHexString(packet));
                    mLeft = packet;
                } else { // message reached full length

                    List<Integer> packet = buffer.subList(from, to + 1);
                    // TODO may be remove
                    if (validate(packet)) {
                        Log.d(ConstantsUtil.GENERAL_TAG + " 5", "message created: " + ConvertUtil.decimalToHexString(packet));

                        ReceiveMessage message = createMessageByType(packet);
                        lst.add(message);
                    } else {
                        Log.d(ConstantsUtil.GENERAL_TAG + " 5", "wrong packet: " + ConvertUtil.decimalToHexString(packet));
//                        mLeft = packet;
                    }

                    from = to + 1;
                }
            }
        }


        return lst;
    }

    @Nullable
    private static ReceiveMessage createMessageByType(List<Integer> packet) {
        ReceiveMessage message = null;

        switch (packet.get(3)){ // get type of message
            case 0x21:
                message = new ReceiveMessage(packet, ReceiveMessage.MessageType.LOD);
                break;
            case 0x01:
                message = new ReceiveMessage(packet, ReceiveMessage.MessageType.VERSION);
                break;
        }
        return message;
    }

    private static List<Integer> deepCopy(List<Integer> lst) {
        List<Integer> ans = new ArrayList<>();
        for (Integer element : lst) {
            ans.add(element);
        }
        return ans;
    }

    private static boolean validate(List<Integer> packet) {
        if (packet.get(4) == 0) {
            return true;
        }
        int sum = 0;
        for (int i = 3; i < packet.size() - 2; i++) {
            sum += packet.get(i);
        }
        if (sum == packet.get(packet.size() - 2) * Math.pow(16, 2) + packet.get(packet.size() - 1)) {
            return true;
        }
        return false;
    }

    public static List<Integer> bytesToPositive(byte[] bytes) {
        List<Integer> positive = new ArrayList<>();
        for (int i = 0; i < bytes.length - 1; i++) {
            positive.add(bytes[i] & 0xFF);
        }
        return positive;
    }
}
