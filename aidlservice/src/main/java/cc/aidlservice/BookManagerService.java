package cc.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lichengcai on 2017/12/7.
 */

public class BookManagerService extends Service {
    private static final String TAG = "BMS";
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArriedListener> mListenerList = new RemoteCallbackList<>();
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArriedListener listener) throws RemoteException {
//            if (!mListenerList.contains(listener)) {
//                mListenerList.add(listener);
//            }else {
//                Log.d(TAG,"already exists");
//            }
            mListenerList.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArriedListener listener) throws RemoteException {
//            if (mListenerList.contains(listener)) {
//                mListenerList.remove(listener);
//                Log.d(TAG,"unregister listener succeed");
//            }else {
//                Log.d(TAG,"unregister listener failed");
//            }
//
//            Log.d(TAG,"unregisterListener, current size: " + mListenerList.size());
            mListenerList.unregister(listener);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book("Android",1));
        mBookList.add(new Book("IOS",2));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceDestoryed.set(true);
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
//        Log.d(TAG,"onNewBookArrived, notify listeners:" + mListenerList.size());
//        for (int i=0; i< mListenerList.size(); i++) {
//            IOnNewBookArriedListener listener = mListenerList.get(i);
//            Log.d(TAG,"onNewBookArrived, notify listener:" + listener);
//            listener.onNewBookArried(book);
//        }

        int n = mListenerList.beginBroadcast();
        for (int i=0; i<n; i++) {
            IOnNewBookArriedListener listener = mListenerList.getBroadcastItem(i);
            if (listener != null) {
                listener.onNewBookArried(book);
            }
        }

        mListenerList.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);
                    int bookId = mBookList.size() + 1;
                    Book newBook = new Book("new Book#",bookId);

                    onNewBookArrived(newBook);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
