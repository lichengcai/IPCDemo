package cc.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import cc.aidlservice.Book;
import cc.aidlservice.IBookManager;
import cc.aidlservice.IOnNewBookArriedListener;

/**
 * Created by lichengcai on 2017/12/7.
 */

public class BookManagerActivity extends Activity {
    private static final String TAG= "BookManagerActivity";
    private static final int MSG_NEW_BOOK_ARRIVED = 1;
    private TextView text_book,text_book_new;

    private IBookManager mRemoteBookManager;
    private IOnNewBookArriedListener mOnNewBookArrivedListener = new IOnNewBookArriedListener.Stub() {
        @Override
        public void onNewBookArried(Book newBook) throws RemoteException {
            handler.obtainMessage(MSG_NEW_BOOK_ARRIVED,newBook).sendToTarget();
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_NEW_BOOK_ARRIVED:
                    Log.d(TAG,"receive new Book: " + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"the BookManagerService is connected");
            IBookManager iBookManager = IBookManager.Stub.asInterface(service);
            mRemoteBookManager = iBookManager;
            try {
                //调用查询方法查询所有书籍
                List<Book> list = iBookManager.getBookList();
                Log.d(TAG,"query book list:  " + list.toString());
                text_book.setText(list.toString());
                //调用添加书籍方法，添加新书
                Book book = new Book("Android开发艺术探索",3);
                iBookManager.addBook(book);
                //再次调用查询方法查询所有书籍
                List<Book> newList = iBookManager.getBookList();
                text_book_new.setText(newList.toString());

                iBookManager.registerListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"the BookManagerService is connected fail");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);
        text_book = (TextView) findViewById(R.id.text_book);
        text_book_new = (TextView) findViewById(R.id.text_book_new);

        //软件启动就绑定
        Intent bindIntent = new Intent();
        //新版本必须显示Intent 启动绑定服务
        bindIntent.setComponent(new ComponentName("cc.aidlservice","cc.aidlservice.BookManagerService"));
        bindService(bindIntent,mConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
        unbindService(mConn);
    }
}
