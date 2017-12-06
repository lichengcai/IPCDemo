// IMyAidl.aidl
package cc.aidlservice;

// Declare any non-default types here with import statements
import cc.aidlservice.Person;
interface IMyAidl {
    int add(int a ,int b);
    List<Person> addPerson(in Person person);
}
