package com.example.gilharap.mybluetooth3.utils;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static String bytesToHexString(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
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

    public String byteToBinary(byte[] buffer) {
        // payload to binary
        String payload = "";

        for (int i = 5; i < buffer.length - 2; i++) {
            int binaryindex = buffer[i] & 0xFF;
            String binaryStr = mBinaries.get(binaryindex);
            payload = payload + binaryStr;
        }
        return payload;
    }

    public static List<byte[]> bufferToPackets(byte[] buffer) {
        List<byte[]> lst = new ArrayList<>();
        int from = 0;
        int to;

        for (int i = 0; i < buffer.length - 1; i++) {
            /*if((buffer[i] & 0xFF) == 0xDA && (buffer[i+1] & 0xFF) == 0xDE){
                to = i - 1;
                if(to > 0){ // not first DA DE
                    lst.add(Arrays.copyOfRange(buffer, from, to));
                }
                from = i;
            }*/

            if ((buffer[i] & 0xFF) == 0xDA && (buffer[i + 1] & 0xFF) == 0xDE) {
                int sizeIndex = from + ConstantsUtil.DA_DE_SIZE;
                int messageLength = ConstantsUtil.DA_DE_SIZE + 3 + (int)(buffer[sizeIndex] & 0xFF) + 2;
                to = from + messageLength;

                lst.add(Arrays.copyOfRange(buffer, from, to));

                from = to + 1;
            }
        }

        return lst;
    }

}
