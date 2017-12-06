package cc.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import cc.aidlservice.Person;
import cc.aidlservice.IMyAidl;

public class MainActivity extends AppCompatActivity {
    private static final String  TAG = "client";
    EditText edit1,edit2,edit_res;
    Button btn_res,btn_person;
    private IMyAidl iImoocAidl;

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
//            iImoocAidl = (IImoocAidl) service;
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

        //软件启动就绑定
        Intent bindIntent = new Intent();
        //新版本必须显示Intent 启动绑定服务
        bindIntent.setComponent(new ComponentName("cc.aidlservice","cc.aidlservice.IRemoteService"));
        bindService(bindIntent,conn, Context.BIND_AUTO_CREATE);

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


    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
