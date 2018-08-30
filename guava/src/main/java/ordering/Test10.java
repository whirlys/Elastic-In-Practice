package ordering;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @program: guava
 * @description: 强大的Ordering排序器
 * @author: 赖键锋
 * @create: 2018-08-30 21:05
 **/
public class Test10 {
    public static void main(String[] args) {
        Person person1 = new Person("aa", 14);
        Person person2 = new Person("bb", 13);
        Ordering<Person> byOrdering = Ordering.natural().nullsFirst().onResultOf(new Function<Person, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Person input) {
                return input.getAge().toString();
            }
        });
        byOrdering.compare(person1, person2);
        System.out.println(byOrdering.compare(person1, person2));
        // 1
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
