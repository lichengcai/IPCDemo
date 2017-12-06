package cc.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * User: chenzheng
 * Date: 2017/1/6 0006
 * Time: 11:41
 */
public class IRemoteService extends Service {
    private ArrayList<Person> persons;

    /**
     * 当客户端绑定到该服务时调用
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        persons = new ArrayList<>();
        return iBinder;
    }

    private IBinder iBinder = new IMyAidl.Stub(){

        @Override
        public int add(int num1, int num2) throws RemoteException {
            Log.e("tag", "收到的两个参数为："+num1+"和"+num2);
            return num1+num2;
        }

        @Override
        public List<Person> addPerson(Person person) throws RemoteException {
            persons.add(person);
            return persons;
        }
    };
}
