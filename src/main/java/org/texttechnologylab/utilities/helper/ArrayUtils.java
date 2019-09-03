package org.texttechnologylab.utilities.helper;

import org.json.JSONArray;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by abrami on 24.08.16.
 */
public class ArrayUtils {



    public static JSONArray setToArray(Set<String> sSet){

        JSONArray rArray = new JSONArray();

        sSet.parallelStream().forEachOrdered(s->{
            rArray.put(s);
        });

        return rArray;

    }

    public static String setToString(Set<String> input) {

        String rString = "";

        int a = 1;
        for (String s : input) {
            rString += s;
            if (a < input.size()) {
                rString += ",";
            }
            a++;
        }

        return rString;

    }

    public static long[] setToArrayLong(Set<Long> input) {

        long[] rArray = new long[input.size()];

        int a = 0;
        for (long s : input) {
            rArray[a] = s;
            a++;
        }

        return rArray;

    }

    public static Set<String> arrayToSet(String[] input) {

        Set<String> rSet = new HashSet<>();

        for (int a = 0; a < input.length; a++) {
            rSet.add(input[a]);
        }

        return rSet;

    }

    public static Set<Long> arrayToSet(long[] input) {

        Set<Long> rSet = new HashSet<>();

        for (int a = 0; a < input.length; a++) {
            rSet.add(input[a]);
        }

        return rSet;

    }

    public static JSONArray stringSetToJSONArray(Set<String> sSet){

        JSONArray rArray = new JSONArray();

        sSet.parallelStream().forEachOrdered(s->{
            rArray.put(s);
        });

        return rArray;

    }

    public static String[] stringSetToArray(Set<String> sSet){

        String[] rString = new String[sSet.size()];

        int a=0;
        for(String l: sSet){
            rString[a]=l;
            a++;
        }

        return rString;
    }

    public static long[] longSetToArray(Set<Long> pSet){

        long[] rLong = new long[pSet.size()];

        int a=0;
        for(Long l: pSet){
            rLong[a]=l;
            a++;
        }

        return rLong;
    }

    public static int[] intSetToArray(Set<Integer> pSet){

        int[] rInt = new int[pSet.size()];

        int a=0;
        for(Integer i: pSet){
            rInt[a]=i;
            a++;
        }

        return rInt;
    }

    public static Object[] objectSetToArray(Set<Object> pSet){

        Object[] rObject = new Object[pSet.size()];

        int a=0;
        for(Object i: pSet){
            rObject[a]=i;
            a++;
        }

        return rObject;
    }

    public static Set<Long> longArrayToSet(Long[] pArray){

        Set<Long> rSet = new HashSet<>(0);

        for(Long l: pArray){
            rSet.add(l);
        }

        return rSet;
    }


    public static Set<Integer> integerArrayToSet(Integer[] pArray){

        Set<Integer> rSet = new HashSet<>(0);

        for(Integer l: pArray){
            rSet.add(l);
        }

        return rSet;
    }


    public static Set<Object> objectArrayToSet(Object[] pArray){

        Set<Object> rSet = new HashSet<>(0);

        for(Object l: pArray){
            rSet.add(l);
        }

        return rSet;
    }


}
