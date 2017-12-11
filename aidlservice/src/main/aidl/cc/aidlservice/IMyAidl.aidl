
package cc.aidlservice;

import cc.aidlservice.Person;

interface IMyAidl {

    int add(int a ,int b);

    List<Person> addPerson(in Person person);
}
