import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gary on 9/24/16.
 */
public class Node {

    public List<Instance> instances;
    public int positive;
    public int negative;

    public Node left;
    public Node right;
    public Node parent;

    public Map<String, Integer> attributeIndex;
    public String attributeSelected;
    public int classBelonged;

    public int id;

    /**
     * For root node.
     * @param file
     */
    public Node(String file) {

        instances = new ArrayList<>();
        attributeIndex = new HashMap<>();
        parent = null;
        left = null;
        right = null;
        attributeSelected = null;
        classBelonged = -1;
        id = -1;
        ReadFile(file);
    }

    /**
     * For sub-tree.
     * @param instances
     * @param parent
     * @param attributeIndex
     */
    public Node(List<Instance> instances, Node parent, Map<String, Integer> attributeIndex) {

        this.instances = instances;
        this.parent = parent;
        this.attributeIndex = attributeIndex;
        left = null;
        right = null;
        classBelonged = -1;
        attributeSelected = null;
        positive = 0;
        negative = 0;
        id = -1;
        for (Instance instance : instances) {
            if (instance.label == 0) {
                positive++;
            } else {
                negative++;
            }
        }
    }

    public void ReadFile(String file) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean isFirstRow = true;
            while ((line = br.readLine()) != null) {
                if (isFirstRow) {
                    String[] names = line.split("\t");
                    for (int i = 0; i < names.length - 1; i++) {
                        attributeIndex.put(names[i], i);
                    }
                    //System.out.println(attributeIndex);
                    isFirstRow = false;
                } else {
                    String[] ss = line.split("\t");
                    int[] array = new int[ss.length - 1];
                    for (int i = 0; i < array.length; i++) {
                        array[i] = Integer.parseInt(ss[i]);
                    }
                    int label = Integer.parseInt(ss[ss.length - 1]);
                    if (label == 1) {
                        negative++;
                    } else {
                        positive++;
                    }
                    instances.add(new Instance(array, label));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calEntropy(int negative, int positive) {

        if (negative == 0 || positive == 0) {
            return 0;
        }
        double total = negative + positive;
        double negativePercent = (double) negative / total;
        double positivePercent = (double) positive / total;
        return -1.0 * ((negativePercent * Math.log(negativePercent) + positivePercent * Math.log(positivePercent)) / Math.log(2));
    }

    private double calInformationGain(int index) {

        int left = 0;
        int right = 0;
        int leftNegative = 0;
        int leftPositive = 0;
        int rightNegative = 0;
        int rightPositive = 0;
        for (Instance instance : instances) {
            if (instance.attributes[index] == 0) {
                left++;
                if (instance.label == 0) {
                    leftPositive++;
                } else {
                    leftNegative++;
                }
            } else {
                right++;
                if (instance.label == 0) {
                    rightPositive++;
                } else {
                    rightNegative++;
                }
            }
        }
        double total = negative + positive;
        double entropy = calEntropy(negative, positive);
        double conditionalEntropy = left / total * calEntropy(leftNegative, leftPositive) + right / total * calEntropy(rightNegative, rightPositive);
        return entropy - conditionalEntropy;
    }

    public int findMaxIG() {

        double max = -1;
        int maxIndex = -1;
        for (Map.Entry<String, Integer> entry : attributeIndex.entrySet()) {
            double informationGain = calInformationGain(entry.getValue());
            //System.out.println(informationGain + " " + entry.getKey());
            if (max < informationGain) {
                max = informationGain;
                maxIndex = entry.getValue();
                attributeSelected = entry.getKey();
            }
        }
        if (max == 0) {
            return -1;
        }
        return maxIndex;
    }

    public int randomSelectIG() {

        String[] attributeindice = new String[attributeIndex.size()];
        int i = 0;
        for (String attribute : attributeIndex.keySet()) {
            attributeindice[i++] = attribute;
        }
        int index = (int)(Math.random() * attributeindice.length);
        attributeSelected = attributeindice[index];
        if (calInformationGain(attributeIndex.get(attributeSelected)) == 0) {
            return -1;
        }
        return attributeIndex.get(attributeSelected);
    }

    @Override
    public String toString() {
        return "Node{" +
                "instances=" + instances +
                ", positive=" + positive +
                ", negative=" + negative +
                ", left=" + left +
                ", right=" + right +
                ", parent=" + parent +
                ", attributeIndex=" + attributeIndex +
                ", attributeSelected='" + attributeSelected + '\'' +
                '}';
    }
}

















