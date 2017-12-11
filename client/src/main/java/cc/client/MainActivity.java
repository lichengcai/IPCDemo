package cc.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cc.aidlservice.Person;
import cc.aidlservice.IMyAidl;

public class MainActivity extends AppCompatActivity {
    private static final String  TAG = "client";
    EditText edit1,edit2,edit_res;
    Button btn_res,btn_person,btn_messenger,btn_aidl;
    private LinearLayout mLyContainer;
    private IMyAidl iImoocAidl;
    private static final int MSG_SUM = 0x110;
    //显示连接状态
    private TextView mTvState;

    private Messenger mService;
    private boolean isConn;

    private int mA;

    /**
     * DeathRecipient 是一个接口，内部只有一个方法 binderDied,当 Binder 死亡时，系统会回调 binderDied
     * 方法。
     */
    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {

        }
    };

    private ServiceConnection conn = new ServiceConnection() {

        //绑定上服务的时候
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG," onServiceConnected service---" );
            //拿到了远程的服务
            iImoocAidl = IMyAidl.Stub.asInterface(service);
            try {
                service.linkToDeath(deathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        //断开服务的时候
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG," onServiceDisconnected service---" );
            //回收资源
            iImoocAidl = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit1 = (EditText) findViewById(R.id.edit1);
        edit2 = (EditText) findViewById(R.id.edit2);
        edit_res = (EditText) findViewById(R.id.edit_res);
        btn_res = (Button) findViewById(R.id.btn_res);
        btn_person = (Button) findViewById(R.id.btn_person);
        btn_messenger = (Button) findViewById(R.id.btn_messenger);
        btn_aidl = (Button) findViewById(R.id.btn_aidl);
        mTvState = (TextView) findViewById(R.id.text_state);
        mLyContainer = (LinearLayout) findViewById(R.id.activity_main);

        bindServiceInvoked();
        setListener();

    }

    private Messenger mMessenger = new Messenger(new Handler()
    {
        @Override
        public void handleMessage(Message msgFromServer)
        {
            switch (msgFromServer.what)
            {
                case MSG_SUM:
                    TextView tv = (TextView) mLyContainer.findViewById(msgFromServer.arg1);
                    tv.setText(tv.getText() + "=>" + msgFromServer.arg2);
                    break;
            }
            super.handleMessage(msgFromServer);
        }
    });

    private void setListener() {
        btn_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //调用远程的服务
                    int one = Integer.parseInt(edit1.getText().toString());
                    int two = Integer.parseInt(edit2.getText().toString());
                    int res = iImoocAidl.add(one,two);
                    Log.d(TAG," 得到的结果res---" + res);

                    edit_res.setText(res + "");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    edit_res.setText("计算错误");
                }
            }
        });

        btn_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<Person> persons = (ArrayList<Person>) iImoocAidl.addPerson(new Person("ABC",21));

                    Log.d("Persons",persons.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


        btn_messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = mA++;
                int b = (int) (Math.random() * 100);
                //创建一个tv,添加到LinearLayout中
                TextView tv = new TextView(MainActivity.this);
                tv.setText(a + " + " + b + " = caculating ...");
                tv.setId(a);
                mLyContainer.addView(tv);

                Message msgFromClient = Message.obtain(null,MSG_SUM,a,b);
                msgFromClient.replyTo = mMessenger;
                if (isConn) {
                    try {
                        mService.send(msgFromClient);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        btn_aidl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,BookManagerActivity.class));
            }
        });
    }

    private ServiceConnection mConn = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mService = new Messenger(service);
            isConn = true;
            mTvState.setText("connected!");
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mService = null;
            isConn = false;
            mTvState.setText("disconnected!");
        }
    };

    private void bindServiceInvoked() {
        //软件启动就绑定
        Intent bindIntent = new Intent();
        //新版本必须显示Intent 启动绑定服务
        bindIntent.setComponent(new ComponentName("cc.aidlservice","cc.aidlservice.IRemoteService"));
        bindService(bindIntent,conn, Context.BIND_AUTO_CREATE);


        Intent intent = new Intent();
        intent.setComponent(new ComponentName("cc.aidlservice","cc.aidlservice.MessengerService"));
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "bindService invoked !");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
