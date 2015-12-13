package com.way.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * Created by Fan on 2014-12-12.
 */
public class HardwareUtil {

    /**
     * 获取唯一标识
     * @param context Context
     * @return 唯一标识
     */
    public static String getDeviceUniqueCode(Context context){
        Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        String tmDevice, tmSerial, androidId, uniqueId = null;
        try {
            tmDevice = "" + tm.getDeviceId();

            tmSerial = "" + tm.getSimSerialNumber();

            androidId = ""
                    + Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(),
                    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

            uniqueId = deviceUuid.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uniqueId;
    }
//    /**
//     * 获取唯一标识
//     * @param context Context
//     * @return 唯一标识
//     */
//    public static String getDeviceUniqueCode(Context context){
//
//        String result = getIMEICode(context) + getDeviceShortID() + getAndriodId(context)
//                + getWlanMACAddress(context) + getBluetoothMAC();
//
//        MessageDigest m = null;
//        try {  m = MessageDigest.getInstance("MD5"); } catch (NoSuchAlgorithmException e) {  e.printStackTrace();   }
//        m.update(result.getBytes(), 0, result.length());
//
//        byte md5Data[] = m.digest();
//
//        String uniqueId = new String();
//
//        for (int i = 0; i < md5Data.length; i++) {
//            int b = (0xff & md5Data[i]);
//            if (b <= 0xf){
//                uniqueId += "0";
//            }
//            uniqueId += Integer.toHexString(b);
//        }
//
//        uniqueId = uniqueId.toUpperCase();
//        return uniqueId;
//    }

    /**
     * 获取IMEI码， 有些平板或特殊手机不能获取
     * @param context
     * @return
     */
    private static String getIMEICode(Context context){
        TelephonyManager telephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyMgr.getDeviceId();
    }

    /**
     * 通过版本号、CPU、制造商、ROM版本、以及其它硬件信息，取长度信息
     * @return
     */
    private static String getDeviceShortID(){

        String mDeviceShortID = "35" + //we make this look like a valid IMEI

                Build.BOARD.length()        % 10 +
                Build.BRAND.length()        % 10 +
                Build.CPU_ABI.length()      % 10 +
                Build.DEVICE.length()       % 10 +
                Build.DISPLAY.length()      % 10 +
                Build.HOST.length()         % 10 +
                Build.ID.length()           % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length()        % 10 +
                Build.PRODUCT.length()      % 10 +
                Build.TAGS.length()         % 10 +
                Build.TYPE.length()         % 10 +
                Build.USER.length()         % 10 ; //13 digits
        return mDeviceShortID;
    }

    /**
     * 获取Android ID
     * 这个ID会改变如果进行了出厂设置。并且，如果某个Andorid手机被Root过的话，这个ID也可以被任意改变
     * @param context
     * @return
     */
    private static String getAndriodId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取MAC地址
     * @return
     */
    private static String getWlanMACAddress(Context context){
        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wm.getConnectionInfo().getMacAddress();
    }

    /**
     * 获取蓝牙MAC地址
     * @return
     */
    private static String getBluetoothMAC(){
        BluetoothAdapter mBluetoothAdapter;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.getAddress();
    }


}
