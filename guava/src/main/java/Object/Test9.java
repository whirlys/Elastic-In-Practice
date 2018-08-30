package Object;

import com.google.common.base.MoreObjects;

/**
 * @program: guava
 * @description: MoreObjects
 * @author: 赖键锋
 * @create: 2018-08-30 20:55
 **/
public class Test9 {
    public static void main(String[] args) {
        Person person = new Person("whirly", 11);

        String string = MoreObjects.toStringHelper("person").add("age", person.getAge()).toString();
        System.out.println(string);
        // person{age=11}
    }

    static class Person {
        private String name;
        private Integer age;

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public Person() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
