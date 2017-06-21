package com.example.shubham_v.TrigentServer;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by shubham_v on 30-05-2017.
 */

public class WifiHotspot {
    private static final String SSID = "WTBFTP_wifi";
    public static boolean setWifiApState(Context context, boolean enabled) {
        //config = Preconditions.checkNotNull(config);
        try {
            WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (enabled) {
                mWifiManager.setWifiEnabled(false);
            }
            WifiConfiguration conf = getWifiApConfiguration();
            mWifiManager.addNetwork(conf);
            return (Boolean) mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class).invoke(mWifiManager, conf, enabled);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static WifiConfiguration getWifiApConfiguration() {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID =  SSID;
        //here change the wifipassword
        conf.preSharedKey="shubhamv";

        //here change the wifi securit protocol
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        return conf;
    }


    public static ArrayList<String> getConnectedDevicesMac()
    {
        ArrayList<String> res = new ArrayList<String>();
        //NetManager.updateArpFile();

        BufferedReader br;
        try
        {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            line = br.readLine();
            while ((line = br.readLine()) != null)
            {
                String[] sp = line.split(" +");
                if (sp[3].matches("..:..:..:..:..:.."))
                    res.add(sp[3]);
            }

            br.close();
        }
        catch (Exception e)
        {}

        return res;
    }
}