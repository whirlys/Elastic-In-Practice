package Strings;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: guava
 * @description: 将String转换为特定的集合
 * @author: 赖键锋
 * @create: 2018-08-30 17:35
 **/
public class Test5 {
    public static void main(String[] args) {
        java();
        guava();
        guavaEmptyStr();
    }

    /**
     * 使用 Java
     */
    public static void java() {
        List<String> list = new ArrayList<>();
        String a = "1-2-3-4-5-6";
        String[] strs = a.split("-");
        for (int i = 0; i < strs.length; i++) {
            list.add(strs[i]);
        }
        System.out.println(list);
        // [1, 2, 3, 4, 5, 6]
    }

    /**
     * guava
     */
    public static void guava() {
        String str = "1-2-3-4-5-6";
        List<String> list = Splitter.on("-").splitToList(str);
        System.out.println(list);
        // [1, 2, 3, 4, 5, 6]
    }

    /**
     * guava 空格和空串
     */
    public static void guavaEmptyStr() {
        String str = "1-2-3-4- 5-  6  ";
        List<String> list = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(str);
        System.out.println(list);
        // [1, 2, 3, 4, 5, 6]
    }
}