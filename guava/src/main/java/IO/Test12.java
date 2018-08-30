package IO;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.util.List;

/**
 * @program: guava
 * @description: 文件操作
 * @author: 赖键锋
 * @create: 2018-08-30 21:29
 **/
public class Test12 {
    public static void main(String[] args) {
        File file = new File("test.txt");

        List<String> list = null;
        try {
            list = Files.readLines(file, Charsets.UTF_8);

            File to = new File("to.txt");
            // 复制文件
            Files.copy(file, to);

            // Files.deleteDirectoryContents(File directory); //删除文件夹下的内容(包括文件与子文件夹)
            // Files.deleteRecursively(File file); //删除文件或者文件夹
            // Files.move(File from, File to); //移动文件
            // URL url = Resources.getResource("abc.xml"); //获取classpath根下的abc.xml文件url
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
