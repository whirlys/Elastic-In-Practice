package Collection;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * @program: guava
 * @description: 集合的交并差
 * @author: 赖键锋
 * @create: 2018-08-30 20:25
 **/
public class Test8 {
    public static void main(String[] args) {
        set();
        System.out.println("===================");
        map();
    }

    /**
     * Set 的交并差
     */
    public static void set() {
        HashSet setA = newHashSet(1, 2, 3, 4, 5);
        HashSet setB = newHashSet(4, 5, 6, 7, 8);

        // 并集
        Sets.SetView union = Sets.union(setA, setB);
        System.out.println("union: " + union);

        // 差集
        Sets.SetView difference = Sets.difference(setA, setB);
        System.out.println("difference: " + difference);

        // 交集
        Sets.SetView intersection = Sets.intersection(setA, setB);
        System.out.println("intersection: " + intersection);
    }

    /**
     * map的交集，并集，差集
     */
    public static void map() {
        HashMap<String, Integer> mapA = Maps.newHashMap();
        mapA.put("a", 1);
        mapA.put("b", 2);
        mapA.put("c", 3);

        HashMap<String, Integer> mapB = Maps.newHashMap();
        mapB.put("b", 20);
        mapB.put("c", 3);
        mapB.put("d", 4);

        MapDifference differenceMap = Maps.difference(mapA, mapB);
        differenceMap.areEqual();
        Map entriesDiffering = differenceMap.entriesDiffering();
        Map entriesOnlyLeft = differenceMap.entriesOnlyOnLeft();
        Map entriesOnlyRight = differenceMap.entriesOnlyOnRight();
        Map entriesInCommon = differenceMap.entriesInCommon();

        System.out.println(entriesDiffering);
        // {b=(2, 20)}
        System.out.println(entriesOnlyLeft);
        // {a=1}
        System.out.println(entriesOnlyRight);
        // {d=4}
        System.out.println(entriesInCommon);
        // {c=3}
    }
}
