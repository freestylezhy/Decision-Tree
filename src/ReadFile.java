import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by septem on 9/27/16.
 */
public class ReadFile {

    public static List<Instance> readFile(String file) {

        List<Instance> instances = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean isFirstRow = true;
            while ((line = br.readLine()) != null) {

                if (isFirstRow) {
                    isFirstRow = false;
                } else {
                    String[] ss = line.split("\t");
                    int[] array = new int[ss.length - 1];
                    for (int i = 0; i < array.length; i++) {
                        array[i] = Integer.parseInt(ss[i]);
                    }
                    int label = Integer.parseInt(ss[ss.length - 1]);

                    instances.add(new Instance(array, label));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instances;
    }
}
