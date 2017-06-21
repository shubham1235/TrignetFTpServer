package com.example.shubham_v.TrigentServer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;

import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    private FtpServer mFtpServer;
    private int port = 2221; // The port number private String
    Boolean Wifi_Hot_Spot_State, Server_Running_Status = false;
    TextView tv,Start_stop_text;

    FtpServerFactory serverFactory;
    PropertiesUserManagerFactory userManagerFactory;
    String info = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Wifi_Hot_Spot_State = WifiHotspot.setWifiApState(MainActivity.this,true);


        tv = (TextView) findViewById(R.id.tvText);
        Start_stop_text =(TextView) findViewById(R.id.server_start_stop_text_id);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                info = "Please visit the following address through a browser or my computer\n" + "ftp://" + getLocalIpAddress() + ":" + port + "\n";

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(info);
                    }
                });

            }
        }, 4000);

            Button btnStart = (Button) findViewById(R.id.btnStart);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    WifiHotspot.getConnectedDevicesMac();


                    if (!Server_Running_Status) {
                        if (Wifi_Hot_Spot_State) {
                            Config1();
                            Server_Running_Status = true;
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "FTP Server Already Running", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            Button btnStop = (Button) findViewById(R.id.btnStop);
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Server_Running_Status) {
                        mFtpServer.suspend();
                        mFtpServer.stop();
                        mFtpServer.isSuspended();
                        Start_stop_text.setText("FTP Server is stoped");
                        Server_Running_Status = false;
                    } else {
                        Toast.makeText(MainActivity.this, "FTP Server Already Stoped", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    void Config1() {
         serverFactory = new FtpServerFactory();
         ListenerFactory factory = new ListenerFactory();
         userManagerFactory = new PropertiesUserManagerFactory();

    /*    ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(true);

        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
*/
        factory.setPort(port);

        try {
            serverFactory.addListener("default", factory.createListener());
            FtpServer server = serverFactory.createServer();

            this.mFtpServer = server;
            userAuthentication();

            server.start();
            Start_stop_text.setText("FTP server is start");
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public String getLocalIpAddress() {
        String strIP = null;
        try {
            for (Enumeration < NetworkInterface > en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration< InetAddress > enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        strIP = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("msg", ex.toString());
        }
        return strIP;
    }


    void userAuthentication()
    {
        BaseUser user1 = new BaseUser();
        user1.setName("shubham");
        user1.setPassword("shubham");
        //user1.setHomeDirectory code use for set sdcard drive path. user never open another
       // user1.setHomeDirectory("/storage/sdcard0/Download/zxz.msi");


        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new WritePermission());
        user1.setAuthorities(authorities);
        UserManager um = userManagerFactory.createUserManager();
        AnonymousAuthentication anonymousAuthentication = new AnonymousAuthentication();
        try {
            um.authenticate(anonymousAuthentication);
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
        }
        try
        {
            um.save(user1);//Save the user to the user list on the filesystem
        }
        catch (FtpException e1)
        {
            e1.printStackTrace();
        }

// this is anonymous connection so please uncommenct this
      /*  BaseUser user = new BaseUser();
        user.setName("anonymous");
        try {
            serverFactory.getUserManager().save(user);

        } catch (FtpException e) {
            e.printStackTrace();
        }*/
//
        serverFactory.setUserManager(um);  // if you want to use anonymos connection here so please comment this line

    }
    @Override protected void onDestroy() {
        super.onDestroy();
        if (null != mFtpServer) {
            mFtpServer.stop();
            mFtpServer = null;
            WifiHotspot.setWifiApState(MainActivity.this,false);

        }
    }

}
