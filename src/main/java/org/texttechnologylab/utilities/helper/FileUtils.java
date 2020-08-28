package org.texttechnologylab.utilities.helper;

import org.json.JSONException;
import spark.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static spark.Spark.halt;

/**
 * Created by abrami on 28.09.16.
 */
public class FileUtils {


    public static File downloadFile(File nFile, String pURI) throws IOException{
        return downloadFile(nFile, pURI, true);
    }

    public static File downloadFile(String path, String pURI) throws IOException{
        return downloadFile(path, pURI, true);
    }

    public static File fileExists(String path, String name){

        File tFile = new File(path+name);

        if(tFile.exists()){
            return tFile;
        }
        return null;

    }

    public static Set<File> getFiles(String sPath, String sSuffix) throws IOException {

        Set<File> returnSet = new HashSet<>(0);

        try (Stream<Path> paths = Files.walk(Paths.get(sPath))) {
            paths
                    .filter(Files::isRegularFile).filter(f->{
                        if(sSuffix.length()>0){
                            return f.getFileName().toString().endsWith(sSuffix);
                        }
                        return true;
                    })
                    .forEach(f->{
                        returnSet.add(f.toFile());
                    });
        }

        return returnSet;

    }

    public static boolean isArchive(File f){

        return isArchive(f.getName());

    }

    public static File stringToImage(String pString) throws IOException {
        // create a buffered image
        BufferedImage image = null;
        byte[] imageByte;

        Base64.Decoder decoder = Base64.getDecoder();
        imageByte = decoder.decode(pString);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(bis);
        bis.close();

// write the image to a file
        File outputfile = TempFileHandler.getTempFile();
        ImageIO.write(image, "png", outputfile);

        return outputfile;

    }

    public static boolean isArchive(String f){

        boolean rBoolean = false;

            switch (f.substring(f.lastIndexOf(".")+1)){
                case "bz":
                case "bz2":
                case "gz":
                case "zip":
                rBoolean = true;
                    break;
                default:
                    rBoolean = false;
            }

        return rBoolean;

    }

    public static String getPathFromArchiv(String path){

        return path.substring(path.lastIndexOf("/")+1, path.lastIndexOf("."));

    }

    //    File download overwrites existing files by default.
    //    Set overwrite parameter to false to change that behaviour


    public static File downloadFile(String path, String pURI, boolean overwrite) throws IOException {

        File rFile = null;

        String tPath = "";

            URL uPath = null;
            try {
                uPath = new URL(pURI);
            }
            catch (MalformedURLException e){

            }

            if(!path.endsWith("/")){
                path = path.concat("/");
            }

            File tFile = null;
            if(uPath!=null){
                tFile = new File(path+uPath.toString().substring(uPath.toString().lastIndexOf("/")+1));

                if(!overwrite && tFile.exists()) {
                    System.out.println("File exists already. Overwriting is disabled, skipping download.");
                    return tFile;
                }
                org.apache.commons.io.FileUtils.copyURLToFile(uPath, tFile);

            }

        return tFile;

    }

    public static File downloadFile(File tFile, String pURI, boolean overwrite) throws IOException {

        String tPath = "";

            URL uPath = null;
            try {
                uPath = new URL(pURI);
            }
            catch (MalformedURLException e){

            }


            if(uPath!=null){

                if(!overwrite && tFile.exists()) {
                    System.out.println("File exists already. Overwriting is disabled, skipping download.");
                    return tFile;
                }
                org.apache.commons.io.FileUtils.copyURLToFile(uPath, tFile);

            }

        return tFile;

    }

    public static File downloadFile(String pURI) throws IOException {
        return downloadFile("/tmp/", pURI);
    }

    public static boolean returnDocument(Response response, File pFile, String mime) throws IOException, JSONException {
            return RESTUtils.returnDocument(response, pFile, mime);
    }

    public static boolean returnDocument(Response response, File pFile, String mime, String sCharset) throws IOException, JSONException {
        return RESTUtils.returnDocument(response, pFile, mime, sCharset);
    }

    public static boolean returnDocument(Response response, File pFile) throws IOException, JSONException {
        return RESTUtils.returnDocument(response, pFile);
    }

    public static boolean returnFile(Response response, File pFile) throws IOException, JSONException {
        return RESTUtils.returnFile(response, pFile);
    }



        public static BufferedImage getImage(File pFile) throws IOException {
        BufferedImage i = ImageIO.read(pFile);

        BufferedImage convertedImg = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
        convertedImg.getGraphics().drawImage(i, 0, 0, null);

        return convertedImg;
    }

    public static void returnImage(Response response, File pFile) throws IOException, JSONException {

        String charset = getCharset(pFile);
        returnImage(response, charset, pFile.getName(), getImage(pFile));

    }

    public static void returnImage(Response response, String mimetype, String sName, BufferedImage returnImage) throws JSONException {
        try {

            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "POST,GET");
            response.header("Access-Control-Allow-Headers", "Content-Type, *");
            response.header("Access-Control-Allow-Credentials", "true");
            if(sName.length()>0) {
                response.raw().setHeader("Content-Disposition", "attachment; filename=" + sName);
            }
            else{
                response.raw().setHeader("Content-Disposition", "attachment; filename=file"+System.currentTimeMillis()%new Random().nextInt()+".png");
            }

            OutputStream out = response.raw().getOutputStream();

            ImageIO.write(returnImage, mimetype, out);
            out.close();

        } catch (Exception e) {
            SparkUtils.prepareReturnFailure(response, e.getMessage(), e.getStackTrace());
        }
        halt(200);
    }

        public static String getCharset(File pFile) throws FileNotFoundException {
            FileInputStream fInput = new FileInputStream(pFile);
            InputStreamReader input = new InputStreamReader(fInput);
            String charset = input.getEncoding();
            return charset;
        }

    public static BufferedInputStream getBufferedFile(File pFile) throws FileNotFoundException {
        FileInputStream fInput = new FileInputStream(pFile);
        BufferedInputStream buffer = new BufferedInputStream(fInput);
        return buffer;
    }


    public static String getContentFromFile(File pFile) throws IOException {
        return StringUtils.getContent(pFile);
    }

    public static String getContentFromFile(File pFile, String charset) throws IOException {
        return StringUtils.getContent(pFile, charset);
    }

    public static void writeContent(String pContent, File pFile) throws IOException {
        writeContent(pContent, pFile, "UTF-8");
    }

    public static void writeContent(String pContent, File pFile, String sEncoding) throws IOException {
        PrintWriter lWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(pFile), sEncoding));
        lWriter.println(pContent);
        lWriter.close();
    }

}
