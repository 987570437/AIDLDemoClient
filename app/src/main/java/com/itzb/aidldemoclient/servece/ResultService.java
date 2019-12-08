package com.itzb.aidldemoclient.servece;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.itzb.aidldemoserver.ILoginInterface;

public class ResultService extends Service {
    public ResultService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ILoginInterface.Stub() {
            @Override
            public void login() throws RemoteException {

            }

            @Override
            public void loginCallback(boolean loginStatus, String loginName) throws RemoteException {
                Log.d("zb", "loginCallback, loginName: " + loginName);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("result.login");
                broadcastIntent.putExtra("loginStatus",loginStatus);
                broadcastIntent.putExtra("name", loginName);
                sendBroadcast(broadcastIntent);
            }
        };
    }
}
