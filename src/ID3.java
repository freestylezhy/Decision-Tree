import java.text.NumberFormat;
import java.util.*;

/**
 * Created by septem on 9/26/16.
 */
public class ID3 {

    private static Node root;
    private static int range;
    private static int nodeCount;
    private static int leafNodeCount;
    private static int sumOfDepthOfLeafNode;

    private ID3(String file) {

        root = new Node(file);
        range = root.attributeIndex.size();
        nodeCount = 1;
        leafNodeCount = 0;
        sumOfDepthOfLeafNode = 0;
    }

    private void expand(Node root, Map<String, Integer> attributeIndex) {

        if (root.positive == 0) {
            //System.out.println("positive == 0");
            root.classBelonged = 1;
            return;
        }
        if (root.negative == 0) {
            //System.out.println("negative == 0");
            root.classBelonged = 0;
            return;
        }
        if (attributeIndex.isEmpty()) {
            //System.out.println("leftNumber= " + root.positive + " rightNumber= " + root.negative);
            //System.out.println("currnet map is Empty!");
            root.classBelonged = root.positive > root.negative ? 0 : 1;
            return;
        }
        System.out.println(root.findMaxIG() + " " + root.randomSelectIG());
        int index = root.randomSelectIG();     //root.findMaxIG();
        if (index == -1) {
            root.classBelonged = root.positive > root.negative ? 0 : 1;
            return;
        }
        //System.out.println("leftNumber= " + root.positive + " rightNumber= " + root.negative);
        Map<String, Integer> attributeChildenIndex = new HashMap<>(attributeIndex);
        attributeChildenIndex.remove(root.attributeSelected);
        //System.out.println("childen is empty? " + attributeChildenIndex.isEmpty() + "\n");

        List<Instance> leftInstances = new ArrayList<>();
        List<Instance> rightInstances = new ArrayList<>();
        for (Instance instance : root.instances) {
            if (instance.attributes[index] == 0) {
                leftInstances.add(instance);
            } else {
                rightInstances.add(instance);
            }
        }
        root.left = new Node(leftInstances, root, attributeChildenIndex);
        expand(root.left, attributeChildenIndex);
        root.right = new Node(rightInstances, root, attributeChildenIndex);
        expand(root.right, attributeChildenIndex);
    }

    private void printDecisionTree(Node root, int level) {

        if (root.classBelonged != -1) {
            System.out.print(" " + root.classBelonged);
            leafNodeCount++;
            return;
        }
        System.out.println();
        for (int i = 0; i < level; i++) {
            System.out.print("| ");
        }
        System.out.print(root.attributeSelected + " = 0 :");
        nodeCount++;
        printDecisionTree(root.left, level + 1);
        System.out.println();
        for (int i = 0; i < level; i++) {
            System.out.print("| ");
        }
        System.out.print(root.attributeSelected + " = 1 :");
        nodeCount++;
        printDecisionTree(root.right, level + 1);
    }

    private boolean testOneInstance(Node root, Instance instance) {

        if (root.classBelonged != -1) {
            if (instance.label == root.classBelonged) {
                return true;
            } else {
                return false;
            }
        }
        if (instance.attributes[root.attributeIndex.get(root.attributeSelected)] == 0) {
            return testOneInstance(root.left, instance);
        } else {
            return testOneInstance(root.right, instance);
        }
    }

    private String test(Node root, List<Instance> list) {

        int correct = 0;
        for (Instance instance : list) {
            if (testOneInstance(root, instance)) {
                correct++;
            }
        }
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);
        return defaultFormat.format(1.0 * correct / list.size());
    }

    private void generateNodeID(Node root) {

        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        int startId = 0;
        int level = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node cur = queue.poll();
                if (cur.left != null && cur.right != null) {
                    cur.id = startId++;
                }
                if (cur.left != null) {
                    queue.offer(cur.left);
                }
                if (cur.right != null) {
                    queue.offer(cur.right);
                }
                if (cur.left == null && cur.right == null) {
                    sumOfDepthOfLeafNode += level;
                }
            }
            level++;
        }
    }

    private int[] shuffle(int internalNodeCount, int pruneNumber) {

        int[] internalNodes = new int[internalNodeCount - 1];//55
        int[] pruneNodes = new int[pruneNumber];
        for (int i = 0; i < internalNodes.length; i++) {
            internalNodes[i] = i + 1;
        }
        for (int i = 0; i < pruneNumber; i++) {
            int j = (int)(Math.random() * (internalNodes.length - i));
            pruneNodes[i] = internalNodes[j];
            swap(internalNodes, internalNodes.length - 1 - i, j);
        }
        return pruneNodes;
    }

    private void prune(Node root, int[] pruneNodes) {

        for (int i = 0; i < pruneNodes.length; i++) {
            Node target = find(root, pruneNodes[i]);
            if (target != null) {
                target.left = null;
                target.right = null;
                target.classBelonged = target.positive > target.negative ? 0 : 1;
            }
        }
    }

    private Node find(Node root, int id) {

        if (root == null) {
            return null;
        }
        if (root.id == id) {
            return root;
        }
        Node left = find(root.left, id);
        if (left != null) {
            return left;
        }
        Node right = find(root.right, id);
        if (right != null) {
            return right;
        }
        return null;
    }

    private void swap(int[] array, int i, int j) {

        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static void main(String[] args) {

        String trainFile;
        String testFile;
        if (args.length != 3) {
            System.out.println("Please input three arguments!");
        } else {
            trainFile = args[0];
            testFile = args[1];
            double pruneFactor = Double.parseDouble(args[2]);
            ID3 id3 = new ID3(trainFile);

            id3.expand(root, root.attributeIndex);
            id3.generateNodeID(root);
            System.out.print("Decision Tree");
            id3.printDecisionTree(root, 0);
            System.out.println("\nBefore pruning:\n-------------------");
            System.out.println("Number of training instances = " + root.instances.size());
            System.out.println("Number of training attributes = " + range);
            System.out.println("Total number of nodes in the tree = " + nodeCount);
            System.out.println("Number of leaf nodes in the tree = " + leafNodeCount);
            System.out.println("The sum depth of leaf nodes = " + sumOfDepthOfLeafNode);
            System.out.println("The Average depth = " + (double)sumOfDepthOfLeafNode / leafNodeCount);
            System.out.print("Accuracy of the model on the testing dataset = ");
            System.out.println(id3.test(root, root.instances));
            List<Instance> testInstances = ReadFile.readFile(testFile);
            System.out.println("\nNumber of Testing instances = " + testInstances.size());
            System.out.println("Number of training attributes = " + range);
            System.out.print("Accuracy of the model on the training dataset = ");
            System.out.println(id3.test(root, testInstances));
            System.out.println("\nDecision Tree after pruning");
            int internalNodeCount = nodeCount - leafNodeCount;
            int pruneNumber = (int) (internalNodeCount * pruneFactor);
            int[] randomNumbers = id3.shuffle(internalNodeCount, pruneNumber);
            id3.prune(root, randomNumbers);
            nodeCount = 1;
            leafNodeCount = 0;
            id3.printDecisionTree(root, 0);
            System.out.println("\nAfter pruning:\n-------------------");
            System.out.println("Number of training instances = " + root.instances.size());
            System.out.println("Number of training attributes = " + range);
            System.out.println("Total number of nodes in the tree = " + nodeCount);
            System.out.println("Number of leaf nodes in the tree = " + leafNodeCount);
            System.out.print("Accuracy of the model on the training dataset = ");
            System.out.println(id3.test(root, root.instances));
            System.out.println("\nNumber of Testing instances = " + testInstances.size());
            System.out.println("Number of training attributes = " + range);
            System.out.print("Accuracy of the model on the testing dataset = ");
            System.out.println(id3.test(root, testInstances));
        }
    }
}
