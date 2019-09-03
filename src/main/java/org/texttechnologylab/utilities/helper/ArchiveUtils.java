package org.texttechnologylab.utilities.helper;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

/**
 * Created by abrami on 28.09.16.
 */
public class ArchiveUtils {

    public static File unzipbz2(File f) throws IOException {

        BZip2CompressorInputStream gzis =
                new BZip2CompressorInputStream(new FileInputStream(f));

        String newPath = f.getPath().replace(".bz2", "");

        File rFile = new File(newPath);

        if(rFile.exists()){
            return rFile;
        }

        FileOutputStream out =
                new FileOutputStream(newPath);

        byte[] buffer = new byte[1024];

        int len;
        while ((len = gzis.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        gzis.close();
        out.close();

        rFile = new File(newPath);

        return rFile;

    }

    public static File gunzip(File f) throws IOException {

        GZIPInputStream gzis =
                new GZIPInputStream(new FileInputStream(f));
        String newPath = f.getPath().replace(".gz", "");

        FileOutputStream out =
                new FileOutputStream(newPath);

        File rFile = new File(newPath);

        if(rFile.exists()){
            return rFile;
        }

        byte[] buffer = new byte[1024];

        int len;
        while ((len = gzis.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        gzis.close();
        out.close();

        return rFile;

    }

    public static ArrayList<File> unzipzip(File f)throws IOException, ArchiveException {
        ArrayList<File> archiveContents = new ArrayList<File>();

        File tmp = TempFileHandler.getTempFolder();
        if (!tmp.exists()) {
            tmp.mkdir();
        }


        final FileInputStream is = new FileInputStream(f);
        ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, is);

        ZipArchiveEntry entry = (ZipArchiveEntry) ais.getNextEntry();
        while (entry != null) {
            File outputFile = new File(tmp, entry.getName());
            FileOutputStream os = new FileOutputStream(outputFile);

            if(!outputFile.exists()){


            IOUtils.copy(ais, os);

            }

            os.close();

            archiveContents.add(outputFile);

            entry = (ZipArchiveEntry) ais.getNextEntry();
        }

        ais.close();
        is.close();

        return archiveContents;
    }

    public static void deleteDir(){
        deleteDir(new File("tmp"));
    }

    private static void deleteDir(File file){
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

}
