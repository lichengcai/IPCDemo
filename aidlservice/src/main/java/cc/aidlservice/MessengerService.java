package cc.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * Created by lichengcai on 2017/12/7.
 */

public class MessengerService extends Service {

    private static final int MSG_SUM = 0x110;

    private Messenger messenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Message msgToClient = Message.obtain(msg);
            switch (msg.what) {
                case MSG_SUM:
                    msgToClient.what = MSG_SUM;
                    try {
                        Thread.sleep(2000);
                        msgToClient.arg2 = msg.arg1 + msg.arg2;
                        msg.replyTo.send(msgToClient);

                    }catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
