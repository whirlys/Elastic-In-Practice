package Strings;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @program: guava
 * @description: 把map集合转换为特定规则的字符串
 * @author: 赖键锋
 * @create: 2018-08-30 17:32
 **/
public class Test4 {

    public static void main(String[] args) {
        guava();
    }

    public static void guava(){
        Map<String, Integer> map = Maps.newHashMap();
        map.put("xiaoming", 11);
        map.put("xiaodong", 22);
        String result = Joiner.on(",").withKeyValueSeparator("=").join(map);
        System.out.println(result);
        // xiaoming=11,xiaodong=22
    }
}
