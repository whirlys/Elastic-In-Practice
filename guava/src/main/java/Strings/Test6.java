package Strings;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.util.List;

/**
 * @program: guava
 * @description: 多个字符切割，或者特定的正则分隔
 * @author: 赖键锋
 * @create: 2018-08-30 17:42
 **/
public class Test6 {
    public static void main(String[] args) {
        String input = "aa.dd,,ff,,.";

        List<String> list = Splitter.onPattern("[.|,]").omitEmptyStrings().splitToList(input);
        System.out.println(list);

        // 判断匹配结果
        boolean result = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z')).matches('K');
        System.out.println(result);

        // 保留数字文本   CharMatcher.digit() 已过时   retain 保留
        String s1 = CharMatcher.inRange('0', '9').retainFrom("abc 123 efg");
        System.out.println(s1);

        // 删除数字文本   remove 删除
        String s2 = CharMatcher.inRange('0', '9').removeFrom("abc 123 efg");
        System.out.println(s2);
    }
}
