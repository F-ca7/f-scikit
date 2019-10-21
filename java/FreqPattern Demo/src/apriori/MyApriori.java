package apriori;

import Util.SetUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @description apriori implementation
 *              with given support and confidence
 * @author FANG
 * @date 2019/10/19 21:17
 **/
public class MyApriori {
    private static String filepath;
    public static final String SEPARATOR = ",";
    private static double minSupport = 0.2;
    private static double minConfidence = 0.5;

    // whether cannot find frequent k‐item
    public static boolean endFlag = false;
    public static List<List<String>> dataset = new ArrayList<>();
    public static List<List<String>> associationRules = new ArrayList<>();

    public static void init(String file, double support , double confidence) {
        filepath = file;
        minSupport = support;
        minConfidence = confidence;
    }

    public static void main(String[] args){
        // loading dataset
        dataset = loadDataFromFile(SEPARATOR);
        System.out.println("Original dataset:");
        printDataset(dataset);

        // get 1‐item sets for candidate
        List<List<String>> oneItemSet = getFirstSet();
        System.out.println("1‐item sets:");
        printDataset(oneItemSet);

        // get frequent 1‐item sets for candidate
        List<List<String>> freqItemSet = prune(oneItemSet);
        System.out.println("Frequent 1‐item sets:");
        printDataset(freqItemSet);

        // join and prune
        while(true){
            // join
            List<List<String>> candidateItemSet = join(freqItemSet);
            System.out.println("CandidateItemSet after join:");
            printDataset(candidateItemSet);
            // prune
            List<List<String>> nextFrequentItemSet = prune(candidateItemSet);
            if(!endFlag){
                // update frequent item set
                freqItemSet = nextFrequentItemSet;
            } else {
                break;
            }
            System.out.println("FrequentItemSet after prune:");
            printDataset(nextFrequentItemSet);
            // generate association rules
            generateAssociationRules(nextFrequentItemSet);

        }

        // until no more frequent k‐item sets can be found
        System.out.println("----------------------------------");
        System.out.println("Found frequent item set by Apriori:");
        printFreqItemSet(freqItemSet);
        System.out.println("----------------------------------");
        System.out.println("Association Rules by Apriori:");
        System.out.println(rulesToString());
    }



    /**
     * load dataset from a txt/csv file
     * @param sep separator for each attribute
     */
    public static List<List<String>> loadDataFromFile(String sep) {
        List<List<String>> dataset = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(filepath))));
            String line;
            while ((line = br.readLine()) != null) {
                // read by line
                String[] lineStrs = line.split(sep);
                List<String> lineList = new ArrayList<>();
                Collections.addAll(lineList, lineStrs);
                dataset.add(lineList);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }

    /**
     * get a set of candidate k‐item sets
     * @param freqItemSet origin frequent item set
     */
    public static List<List<String>> join(List<List<String>> freqItemSet) {
        List<List<String>> nextCandidateItemSet = new ArrayList<>();
        HashSet<String> tmpSet;
        for (int i = 0; i<freqItemSet.size(); i++) {
            HashSet<String> hSet = new HashSet<>(freqItemSet.get(i));
            int oLen = hSet.size();
            tmpSet = (HashSet<String>) hSet.clone();
            for(int h=i+1; h<freqItemSet.size(); h++) {
                // join the i-th row with j-th row
                hSet = (HashSet<String>) tmpSet.clone();
                hSet.addAll(freqItemSet.get(h));
                int newLen = hSet.size();
                if(oLen == newLen-1) {
                    // a new element is added
                    if(isSubsetOf(hSet, dataset) && isNotIn(hSet, nextCandidateItemSet)) {
                        // if the set is a subset of any row from origin dataset
                        List<String> tempList = new ArrayList<>(hSet);
                        nextCandidateItemSet.add(tempList);
                    }
                }

            }

        }
        return nextCandidateItemSet;
    }


    /**
     * whether new set in in next candidate
     */
    private static boolean isNotIn(HashSet<String> hSet,
                                   List<List<String>> nextCandiItemSet) {
        List<String> tempList = new ArrayList<>(hSet);
        for (List<String> strings : nextCandiItemSet)
            if (tempList.equals(strings))
                return false;
        return true;
    }

    /**
     * whether hSet is a subset of a certain row in targetDataset
     * @param hSet a certain set that contains items
     * @param targetDataset  target dataset for comparing
     */
    private static boolean isSubsetOf(HashSet<String> hSet,
                                  List<List<String>> targetDataset) {
        List<String> tempList = new ArrayList<>(hSet);
        List<String> tmpRow;
        for(int i = 0; i< targetDataset.size(); i++){
            tmpRow = new ArrayList<>();
            for(int j = 1; j< targetDataset.get(i).size(); j++)
                tmpRow.add(targetDataset.get(i).get(j));
            if(tmpRow.containsAll(tempList))
                return true;
        }
        return false;
    }

    /**
     * prune
     * the subset frequent item set must be frequent
     * @param candidateItemSet candidate from join
     */
    public static List<List<String>> prune(List<List<String>> candidateItemSet) {
        List<List<String>> supItemSet = new ArrayList<>();
        boolean notFoundFlag = true;
        for (List<String> item : candidateItemSet) {
            int count = getSupportCnt(item);
            // remove subsets that are not frequent
            // first check support
            if (count >= minSupport * getDatasetCnt()) {
                supItemSet.add(item);
                notFoundFlag = false;
            }
        }
        endFlag = notFoundFlag;
        return supItemSet;
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

    private static String setToString(HashSet<String> hSet) {
        List<String> list = new ArrayList<>(hSet);
        return SetUtil.listToString(list);
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



    /**
     * get 1-item candidate set
     */
    public static List<List<String>> getFirstSet() {
        List<List<String>> firstSet = new ArrayList<>();
        HashSet<String> hs  = new HashSet<>();
        for (int i = 0; i < dataset.size(); i++){
            // first row is for attributes
            for(int j = 1; j< dataset.get(i).size(); j++){
                hs.add(dataset.get(i).get(j));
            }
        }
        List<String> tmpList;
        for (String h : hs) {
            tmpList = new ArrayList<>();
            tmpList.add(h);
            firstSet.add(tmpList);
        }
        return firstSet;
    }



    private static void printDataset(List<List<String>> ds) {
        for (List<String> rows : ds) {
            List<String> list = new ArrayList<>(rows);
            for (String s : list) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }

    public static String datasetToString(List<List<String>> ds) {
        StringBuilder stringBuilder = new StringBuilder();
        for (List<String> rows : ds) {
            List<String> list = new ArrayList<>(rows);
            for (String s : list) {
                stringBuilder.append(s).append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public static String frequentSetToString(List<List<String>> freqItemSet) {
        StringBuilder stringBuilder = new StringBuilder();
        for (List<String> row : freqItemSet) {
            for (String s : row) {
                stringBuilder.append(s).append(" ");
            }
            stringBuilder.append(String.format("Support: %f", (float)getSupportCnt(row)/getDatasetCnt()));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }


    private static void printFreqItemSet(List<List<String>> freqItemSet) {
        for (List<String> row : freqItemSet) {
            for (String s : row) {
                System.out.print(s + " ");
            }
            System.out.printf("Support: %f", (float)getSupportCnt(row)/getDatasetCnt());
            System.out.println();
        }
    }

    /**
     * get rows count in dataset except the attribute row
     */
    private static int getDatasetCnt() {
        return dataset.size();
    }

}
