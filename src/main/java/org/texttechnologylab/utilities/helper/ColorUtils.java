package org.texttechnologylab.utilities.helper;

import java.awt.*;
import java.util.Random;

/**
 * Created by abrami on 28.09.16.
 */
public class ColorUtils {

    public static Color generateColor(){
        Random random=new Random(); // Probably really put this somewhere where it gets executed only once
        int red=random.nextInt(256);
        int green=random.nextInt(256);
        int blue=random.nextInt(256);
        Color c = new Color(red, green, blue);
        return c;
    }

    public static String generateColorString(){
        return "#"+Integer.toHexString(generateColor().getRGB()).substring(2);
    }

}
