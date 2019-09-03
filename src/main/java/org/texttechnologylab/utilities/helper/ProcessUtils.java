package org.texttechnologylab.utilities.helper;

import java.util.Random;

/**
 * Created by abrami on 28.09.16.
 */
public class ProcessUtils {


    public static String generatePID(){
        long l = new Random(System.currentTimeMillis()).nextLong();
        long b = System.currentTimeMillis()%l;
        long r = new Random(b+l).nextLong();
        return Math.abs((((l+b+r)*b)*r)/new Random(r).nextInt())+"_"+Math.abs(new Random(b).nextLong());
    }



}
