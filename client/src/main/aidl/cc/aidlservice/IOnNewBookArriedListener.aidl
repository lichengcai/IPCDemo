// IOnNewBookArriedListener.aidl
package cc.aidlservice;
import cc.aidlservice.Book;

interface IOnNewBookArriedListener {
    void onNewBookArried(in Book newBook);
}
