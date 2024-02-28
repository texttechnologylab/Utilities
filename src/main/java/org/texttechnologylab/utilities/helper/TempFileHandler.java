package org.texttechnologylab.utilities.helper;


import java.io.File;
import java.io.IOException;

/**
 * Created by abrami on 12.01.16.
 */
public class TempFileHandler {

    private static final File tmpdir = new File(System.getProperty("java.io.tmpdir"));



    public static File getTempFile() throws IOException {
        File f = getTempFile("s", "p");
        f.deleteOnExit();
        return f;
    }

    public static File getTempFileName(String sName){

        File f = new File(tmpdir+"/"+sName);
        return f;

    }

    public static File getTempFolder(){
        File f = new File("/tmp");
        return f;
    }

    public static File getTempFile(String sPrefix, String sSuffix) throws IOException {
        File f = File.createTempFile(sPrefix, sSuffix);
        return f;
    }

    public static File getTempFile(String sPrefix) throws IOException {
        File f = File.createTempFile(sPrefix, "");
        return f;
    }

}
