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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public static File createZipArchive(List<File> inputFiles) throws IOException {

        File rFile = TempFileHandler.getTempFile("aaa", "bbb");

        return createZipArchive(inputFiles, rFile);


    }

    public static File createZipArchive(List<File> inputFiles, File pTargetFile) throws IOException {

        File rFile = pTargetFile;

        FileOutputStream fos = new FileOutputStream(rFile);

        ZipOutputStream zos = new ZipOutputStream(fos);

        byte[] buffer = new byte[1024];

        for (int i = 0; i < inputFiles.size(); i++) {

            FileInputStream fis = new FileInputStream(inputFiles.get(i));

            // begin writing a new ZIP entry, positions the stream to the start of the entry data
            zos.putNextEntry(new ZipEntry(inputFiles.get(i).getName()));

            int length;

            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();

            fis.close();

        }
        zos.close();

        return rFile;

    }

    public static File compressGZ(File pFile) throws IOException {
        return compressGZ(pFile, false);
    }

    public static File decompressGZ(File pFile) throws IOException {
        return decompressGZ(pFile, false);
    }

    public static File compressGZ(File pFile, boolean bPersistent) throws IOException {

        File rFile = TempFileHandler.getTempFileName(pFile.getName()+".gz");
        compressGZ(Paths.get(pFile.getAbsolutePath()), Paths.get(rFile.getAbsolutePath()));
        rFile.deleteOnExit();
        return rFile;

    }

    public static File decompressGZ(File pFile, boolean bPersistent) throws IOException {

        File rFile = TempFileHandler.getTempFileName(pFile.getName().replace(".gz", ""));

        decompressGZ(Paths.get(pFile.getAbsolutePath()), Paths.get(rFile.getAbsolutePath()));
        rFile.deleteOnExit();
        return rFile;

    }

    public static void decompressGZ(Path input, Path output) throws IOException {

        try (GZIPInputStream gis = new GZIPInputStream(
                new FileInputStream(input.toFile()))) {
            Files.copy(gis, output);

        }

    }

    public static void compressGZ(Path input, Path output) throws IOException {

        try (GZIPOutputStream gos = new GZIPOutputStream(
                new FileOutputStream(output.toFile()))) {

            Files.copy(input, gos);
        }

    }

}
