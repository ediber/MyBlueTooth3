package com.example.gilharap.mybluetooth3.utils;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtil {

    private ArrayList<String> mBinaries;

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

        char[] hexChars = new char[lst.size() * 2];
        for (int j = 0; j < lst.size(); j++) {
            int v = lst.get(j);
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
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

    public String decimalToBinary(int dec) {
        String binaryStr = mBinaries.get(dec);
        return binaryStr;
    }

    public static List<List<Integer>> bufferToPackets(List<Integer> buffer) {
        List<List<Integer>> lst = new ArrayList<>();
        int from = 0;
        int to;

        for (int i = 0; i < buffer.size() - 1; i++) {

            if ((buffer.get(i)) == 0xDA && (buffer.get(i+1)) == 0xDE) {
                int sizeIndex = from + ConstantsUtil.DA_DE_SIZE;
                int messageLength = ConstantsUtil.DA_DE_SIZE + 3 + buffer.get(sizeIndex) + 2;
                to = from + messageLength;

                List<Integer> unCheckedPacket = buffer.subList(from, to);
                if(validate(unCheckedPacket)){
                    lst.add(unCheckedPacket);
                }

                from = to + 1;
            }
        }

        return lst;
    }

    private static boolean validate(List<Integer> packet) {
        if(packet.get(4) == 0){
            return true;
        }
        int sum = 0;
        for(int i=3; i<packet.size()-2; i++){
            sum += packet.get(i);
        }
        if (sum == packet.get(packet.size()-2) * Math.pow(16, 2) + packet.get(packet.size()-1)){
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
