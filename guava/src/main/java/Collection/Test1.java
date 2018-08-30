package Collection;

import com.google.common.collect.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: guava
 * @description:
 * @author: 赖键锋
 * @create: 2018-08-30 17:07
 **/
public class Test1 {
    public static void main(String[] args) {
        guavaCollection();
        guavaImmutable();

        ImmutableList<String> immutableList = ImmutableList.of("1","2","3","4");
        System.out.println(immutableList);
    }

    /**
     * 普通Collection的创建
     */
    public static void guavaCollection() {
        List<String> list = Lists.newArrayList();
        Set<String> set = Sets.newHashSet();
        Map<String, String> map = Maps.newHashMap();
    }

    /**
     * 不变Collection的创建
     */
    public static void guavaImmutable(){
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");
        ImmutableSet<String> set = ImmutableSet.of("e1", "e2");
        ImmutableMap<String, String> map = ImmutableMap.of("k1", "v1", "k2", "v2");
    }
}
