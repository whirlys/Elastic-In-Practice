package Strings;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: guava
 * @description: 将list转换为特定规则的字符串
 * @author: 赖键锋
 * @create: 2018-08-30 17:27
 **/
public class Test3 {

    public static void main(String[] args) {
        java();
        guava();
    }

    /**
     * java
     */
    public static void java() {
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("bb");
        list.add("cc");
        String str = "";
        for (int i = 0; i < list.size(); i++) {
            str = str + "-" + list.get(i);
        }
        System.out.println(str);
        // -aa-bb-cc
    }

    /**
     * guava
     */
    public static void guava() {
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("bb");
        list.add("cc");
        String result = Joiner.on("-").join(list);
        System.out.println(result);
        // aa-bb-cc
    }
}
