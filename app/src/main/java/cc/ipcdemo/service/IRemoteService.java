package cc.ipcdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import cc.ipcdemo.IImoocAidl;

/**
 * Created by lichengcai on 2017/11/30.
 */

public class IRemoteService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }


    private IBinder iBinder = new IImoocAidl.Stub() {


        @Override
        public int add(int num1, int num2) throws RemoteException {
            Log.d("TAG","num1---" + num1 + "   num2---" + num2);
            return num1 + num2;
        }
    };
}
