package Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

/**
 * @program: guava
 * @description: 集合的过滤
 * @author: 赖键锋
 * @create: 2018-08-30 20:15
 **/
public class Test7 {
    public static void main(String[] args) {
        filterByCondition();
        filterByCustomize();
    }

    /**
     * 按照条件过滤
     */
    public static void filterByCondition() {
        ImmutableList<String> names = ImmutableList.of("begin", "code", "Guava", "Java");
        // 过滤出 Guava 或 Java
        Iterable<String> filterd = Iterables.filter(names, Predicates.or(Predicates.equalTo("Guava"),
                Predicates.equalTo("Java")));
        System.out.println(filterd);
        //[Guava, Java]
    }

    /**
     * 自定义过滤条件   使用自定义回调方法对Map的每个Value进行操作
     */
    public static void filterByCustomize() {
        ImmutableMap<String, Integer> map = ImmutableMap.of("begin", 12, "code", 15);
        // Function<F, T> F表示apply()方法input的类型，T表示apply()方法返回类型
        Map<String, Integer> map2 = Maps.transformValues(map, new Function<Integer, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable Integer input) {
                if (input > 12) {
                    return input;
                } else {
                    return input + 1;
                }
            }
        });
        System.out.println(map2);
        //{begin=13, code=15}
    }
}
