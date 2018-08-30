package watch;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * @program: guava
 * @description: 计算中间代码的运行时间
 * @author: 赖键锋
 * @create: 2018-08-30 21:26
 **/
public class Test11 {
    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < 100; i++) {
            // do some thing
            try {
                Thread.sleep(30);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long nanos = stopwatch.elapsed(TimeUnit.MICROSECONDS);
        System.out.println(nanos);
        // 3040742
    }
}
