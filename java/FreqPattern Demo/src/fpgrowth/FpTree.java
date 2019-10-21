package fpgrowth;

import Util.SetUtil;

import java.io.*;
import java.util.*;


/**
 * @description implementation of FpTree
 * @author FANG
 * @date 2019/10/20 16:30
 **/
public class FpTree {
    private static double minSupport = 0.2;
    private static double minConfidence = 0.5;
    private static String filepath;
    public static final String SEPARATOR = ",";
    private static List<List<String>> dataset = new ArrayList<>();
    private static List<List<String>> associationRules = new ArrayList<>();
    private static StringBuilder stringBuilder = new StringBuilder();

    public static void main(String[] args) {
        FpTree.minSupport = 0.2;
        List<List<String>> transRecords = FpTree
                .loadDataFromFile(SEPARATOR);
        System.out.println("----------------------------------");
        System.out.println("Found frequent item set by FpTree:");
        FpTree.FPGrowthHelper(transRecords, null);
        System.out.println(stringBuilder.toString());
        System.out.println("----------------------------------");
        System.out.println("Association Rules by FpTree:");
        System.out.println(rulesToString());
    }


    public static void init(String file, double support , double confidence) {
        filepath = file;
        minSupport = support;
        minConfidence = confidence;
        associationRules.clear();
        dataset.clear();
    }



    /**
     * load dataset from a txt/csv file
     * @param sep separator for each attribute
     */
    public static List<List<String>> loadDataFromFile(String sep) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(filepath))));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().length() > 0) {
                    String[] lineStrs = line.split(sep);
                    List<String> lineList = new ArrayList<>();
                    Collections.addAll(lineList, lineStrs);
                    dataset.add(lineList);
                }
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return dataset;
    }

    /**
     * FP-Growth recursion helper
     * @param dataset dataset that consists of transactional records
     * @param sufPattern pattern growth is achieved by the concatenation of the suffix pattern
     */
    public static void FPGrowthHelper(List<List<String>> dataset,
                                      List<String> sufPattern) {
        // build the node table
        ArrayList<FpNode> nodeTable = buildTable(dataset);
        // build fp-tree
        FpNode treeRoot = buildFpTree(dataset, nodeTable);
        if(nodeTable == null) {
             return;
        }
        if (treeRoot.getChildren() == null
                || treeRoot.getChildren().size() == 0)
            return;
        List<List<String>> itemSet = new ArrayList<>();
        // output head node with its suffix pattern
        if (sufPattern != null) {
            for (FpNode header : nodeTable) {
                List<String> tmpList = new ArrayList<>();
                tmpList.add(header.getName());
                stringBuilder.append(header.getName());
                for (String s : sufPattern) {
                    stringBuilder.append(" ").append(s);
                    tmpList.add(s);
                }
                stringBuilder.append(String.format(" Support: %f\n",(double) header.getCount() / getDatasetCnt() ));
                itemSet.add(tmpList);
            }
            generateAssociationRules(itemSet);
        }
        // find the conditional pattern base
        // for each head node
        for (FpNode headNode : nodeTable) {
            // add to suffix pattern
            List<String> newPattern = new LinkedList<>();
            newPattern.add(headNode.getName());
            if (sufPattern != null)
                newPattern.addAll(sufPattern);
            // find conditional pattern base of the head node
            List<List<String>> newTransRecords = new LinkedList<>();
            FpNode nextNode = headNode.getNext();
            while (nextNode != null) {
                int counter = nextNode.getCount();
                List<String> prevNodes = new ArrayList<>();
                FpNode parent = nextNode;
                // traverse next node's ancestors
                while ((parent = parent.getParent()).getName() != null) {
                    prevNodes.add(parent.getName());
                }
                while (counter > 0) {
                    newTransRecords.add(prevNodes);
                    counter--;
                }
                nextNode = nextNode.getNext();
            }
            // recursion with new record and pattern
            FPGrowthHelper(newTransRecords, newPattern);
        }
    }

    /**
     * build the head node table
     * in descending order of support count
     */
    private static ArrayList<FpNode> buildTable(List<List<String>> dataset) {
        ArrayList<FpNode> headNodes;
        if (dataset.size() > 0) {
            headNodes = new ArrayList<>();
            Map<String, FpNode> map = new HashMap<>();
            // calculate support for each item
            for (List<String> record : dataset) {
                for (String item : record) {
                    if (!map.keySet().contains(item)) {
                        FpNode node = new FpNode(item);
                        node.setCount(1);
                        map.put(item, node);
                    } else {
                        map.get(item).increCount(1);
                    }
                }
            }
            Set<String> names = map.keySet();
            for (String name : names) {
                FpNode node = map.get(name);
                if ((double)node.getCount()/getDatasetCnt() >= minSupport) {
                    headNodes.add(node);
                }
            }
            Collections.sort(headNodes);
            return headNodes;
        } else {
            return null;
        }
    }

    /**
     * build fp tree
     * @param dataset consisting of transactional records
     * @param nodeTable the table of head nodes (in order)
     * @return root node
     */
    private static FpNode buildFpTree(List<List<String>> dataset,
                                      ArrayList<FpNode> nodeTable) {
        FpNode root = new FpNode();
        for (List<String> row : dataset) {
            LinkedList<String> record = sortByHead(row, nodeTable);
            FpNode subTreeRoot = root;
            FpNode tmpRoot;
            if (root.getChildren() != null) {
                while (!record.isEmpty()) {
                    tmpRoot = subTreeRoot.findChild(record.peek());
                    if(tmpRoot != null) {
                        // already existed, increment
                        tmpRoot.increCount(1);
                        subTreeRoot = tmpRoot;
                        record.poll();
                    } else {
                        break;
                    }
                }
            }
            insertNodes(subTreeRoot, record, nodeTable);
        }
        return root;
    }

    /** 
     * sort items in each transaction record descending
     * @param record a row of dataset
     * @param headNodes node in head table
     */
    private static LinkedList<String> sortByHead(List<String> record,
                                                 ArrayList<FpNode> headNodes) {
        Map<String, Integer> map = new HashMap<>();
        for (String item : record) {
            for (int i = 0; i < headNodes.size(); i++) {
                // here headNodes are in descending order
                FpNode node = headNodes.get(i);
                if (node.getName().equals(item)) {
                    map.put(item, i);
                }
            }
        }
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(
                map.entrySet());
        // sort items in single record
        list.sort(Comparator.comparingInt(Map.Entry::getValue));

        LinkedList<String> res = new LinkedList<>();
        // write back item names
        for (Map.Entry<String, Integer> entry : list) {
            res.add(entry.getKey());
        }
        return res;
    }

    /**
     * insert a transaction record into the tree
     * @param ancestor ancestor node
     * @param record transaction record
     * @param nodes list of head nodes
     */
    private static void insertNodes(FpNode ancestor,
                                    LinkedList<String> record,
                                    ArrayList<FpNode> nodes) {
        while (record.size() > 0) {
            String item = record.poll();
            FpNode leaf = new FpNode(item);
            leaf.setCount(1);
            leaf.setParent(ancestor);
            ancestor.addChild(leaf);
            for (FpNode f1 : nodes) {
                // find the right route
                if (f1.getName().equals(item)) {
                    while (f1.getNext() != null) {
                        f1 = f1.getNext();
                    }
                    f1.setNext(leaf);
                    break;
                }
            }
            insertNodes(leaf, record, nodes);
        }
    }


    /**
     * get rows count in dataset except the attribute row
     */
    private static int getDatasetCnt() {
        return dataset.size();
    }

    public static String getFreqResult() {
        return stringBuilder.toString();
    }

    /**
     * count times of list that occurs in the dataset
     */
    private static int getSupportCnt(List<String> list) {
        int count = 0;
        boolean notOccurred;
        for (List<String> strings : dataset) {
            notOccurred = false;
            for (String s : list) {
                boolean curExist = false;
                for (int j = 1; j < strings.size(); j++) {
                    if (s.equals(strings.get(j))) {
                        // the k-th item in list is found in record-i
                        curExist = true;
                    }
                }
                if (!curExist) {
                    // if current item is already not existed in
                    notOccurred = true;
                    break;
                }
            }
            if (!notOccurred)
                count++;
        }
        return count;
    }


    /**
     * generate association rules from frequent item sets
     * @param supItemSets frequent item sets
     */
    public static void generateAssociationRules(List<List<String>> supItemSets) {
        double conf;
        List<String> rule;
        for (List<String> itemSet : supItemSets) {
            int supCntL = getSupportCnt(itemSet);
            // for each non-empty subset of itemSet
            List<List<String>> subsets = SetUtil.powerSetWithoutEmptySet(itemSet);
            for(List<String> subset:subsets) {
                int supCntS = getSupportCnt(subset);
                // check confidence
                conf = (double)supCntL/supCntS;
                if(conf > minConfidence) {
                    rule = new ArrayList<>(3);
                    // s => l-s, confidence
                    rule.add(SetUtil.listToString(subset));
                    rule.add(SetUtil.listToString(SetUtil.listDiff(itemSet, subset)));
                    rule.add(String.valueOf(conf));
                    associationRules.add(rule);
                }
            }
        }
    }




    public static String rulesToString() {
        StringBuilder stringBuilder = new StringBuilder(associationRules.size()*10);
        for(List<String> rule:associationRules) {
            stringBuilder.append(rule.get(0));
            stringBuilder.append(" => ");
            stringBuilder.append(rule.get(1));
            stringBuilder.append(", ");
            stringBuilder.append(rule.get(2));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}