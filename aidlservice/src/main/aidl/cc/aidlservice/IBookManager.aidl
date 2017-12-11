// IBookManager.aidl
package cc.aidlservice;
import cc.aidlservice.Book;
import cc.aidlservice.IOnNewBookArriedListener;

interface IBookManager {
   List<Book> getBookList();
   void addBook(in Book book);
   void registerListener(IOnNewBookArriedListener listener);
   void unregisterListener(IOnNewBookArriedListener listener);
}
