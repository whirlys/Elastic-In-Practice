package Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: guava
 * @description: map中包含key为String类型，value为List类型
 * @author: 赖键锋
 * @create: 2018-08-30 17:19
 **/
public class Test2 {

    public static void main(String[] args) {
        java();
        guava();
    }

    /**
     * 旧写法
     */
    public static void java() {
        Map<String, List<Integer>> map = new HashMap<>();
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        map.put("aa", list);
        System.out.println(map.get("aa"));
        //[1, 2]
    }

    /**
     * guava写法
     */
    public static void guava(){
        Multimap<String, Integer> map = ArrayListMultimap.create();
        map.put("aa", 1);
        map.put("aa", 2);
        System.out.println(map.get("aa"));
    }
}
