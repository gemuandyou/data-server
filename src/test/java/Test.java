import java.io.File;

/**
 * Created by gemu on 29/05/2017.
 */
public class Test {

    public static void main(String[] args) {
        File file = new File("D:\\developmenet\\idea_workspace\\dist");
        System.out.println(file.list().length);
    }

}
