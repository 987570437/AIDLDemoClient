package com.itzb.aidldemoclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.itzb.aidldemoserver.ILoginInterface;

public class MainActivity extends AppCompatActivity {

    private boolean isBindServer = false;
    private ILoginInterface iLoginInterface;//aidl定义接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        bindRemoteService();//开启远程服务

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iLoginInterface != null) {
                    try {
                        iLoginInterface.login();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "服务端app未安装", Toast.LENGTH_SHORT).show();
                }
            }
        });

        IntentFilter filter = new IntentFilter("result.login");
        registerReceiver(loginReceiver, filter);
    }

    //绑定服务端service
    private void bindRemoteService() {
        Intent intent = new Intent();
        intent.setAction("server_ation");//设置服务端应用action(服务唯一标识)
        intent.setPackage("com.itzb.aidldemoserver");//设置Server服务端包名
        bindService(intent, conn, BIND_AUTO_CREATE);//启动服务
        isBindServer = true;//改变标志位
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iLoginInterface = ILoginInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        if (isBindServer) {
            unbindService(conn);
        }

        if (loginReceiver != null) {
            unregisterReceiver(loginReceiver);
        }
        super.onDestroy();
    }

    private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("name");
            Boolean loginStatus = intent.getBooleanExtra("loginStatus", false);
            if (loginStatus) {
                Toast.makeText(MainActivity.this, "登录成功, name: " + name, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "登录失败, name: " + name, Toast.LENGTH_SHORT).show();

            }

        }
    };
}
