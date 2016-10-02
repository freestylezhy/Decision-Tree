import java.util.Arrays;

/**
 * Created by Gary on 9/24/16.
 */
public class Instance {

    public int[] attributes;
    public int label;

    public Instance(int[] attributes, int label) {

        this.attributes = attributes;
        this.label = label;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "attributes=" + Arrays.toString(attributes) +
                ", label=" + label +
                '}';
    }
}
