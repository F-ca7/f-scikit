package Util;

import org.junit.Test;

import java.util.*;

/**
 * @description utils for set and list operation
 * @author FANG
 * @date 2019/10/20 17:10
 **/
public class SetUtil {
    public static List<List<String>> powerSetWithoutEmptySet(List<String> set) {
        int size = 2 << set.size();
        List<List<String>> powerSet = new ArrayList<>(size);
        powerSet.add(Collections.emptyList());
        for (String element : set) {
            int preSize = powerSet.size();
            for (int i = 0; i < preSize; i++) {
                List<String> combineSubset = new ArrayList<>(powerSet.get(i));
                combineSubset.add(element);
                powerSet.add(combineSubset);
            }
        }
        // remove empty set and whole set
        powerSet.remove(powerSet.size()-1);
        powerSet.remove(0);
        return powerSet;
    }

    public static List<String> listDiff(List<String> list, List<String> subList) {
        Set<String> hSet = new HashSet<>(list);
        Set<String> hSubset = new HashSet<>(subList);
        Set<String> res = new HashSet<>(list.size()-subList.size());
        res.addAll(hSet);
        res.removeAll(hSubset);
        return new ArrayList<>(res);
    }

    public static String listToString(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for(int i=0; i<list.size()-1; i++) {
            stringBuilder.append(list.get(i));
            stringBuilder.append(", ");
        }
        stringBuilder.append(list.get(list.size()-1));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}
