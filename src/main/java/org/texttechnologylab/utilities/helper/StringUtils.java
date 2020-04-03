package org.texttechnologylab.utilities.helper;

import org.apache.commons.io.IOUtils;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

/**
 * Created by abrami on 24.08.16.
 */
public class StringUtils {

    public static String encodeBorlandFormat(String pString) {
        return pString.replace("[", "(").replace("]", ")").replace("¤", " ").replace("\t", " ").replace("\r", " ").replace("\n", " ");
    }

    public static String encodeXml(String pString) {
        return pString.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static String decodeXml(String pString) {
        return pString.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
    }

    public static String stacktraceToString(StackTraceElement[] stackTrace){

        StringBuilder sb = new StringBuilder();

        for (StackTraceElement stackTraceElement : stackTrace) {
            sb.append(stackTraceElement.toString());
            sb.append("\n");
        }

        return sb.toString();

    }


    public static String getContent(File pFile) throws IOException {
        return getContent(pFile, "UTF-8");
    }

    public static String getContent(File pFile, String sCharSet) throws IOException {
        StringBuilder lResult = new StringBuilder();
        BufferedReader lReader = new BufferedReader(new InputStreamReader(new FileInputStream(pFile), sCharSet.length()==0 ? "UTF-8": sCharSet));
        String lLine;
        while ((lLine = lReader.readLine()) != null) {
            lResult.append(lLine).append("\n");
        }
        lReader.close();
        return lResult.toString();
    }

    public static String toMD5(String password) throws NoSuchAlgorithmException {
        MessageDigest m= MessageDigest.getInstance("MD5");
        m.update(password.getBytes(),0,password.length());
        return new BigInteger(1,m.digest()).toString(16);
    }

    public static String getContent(InputStream pInputStream) throws IOException {
        return getContent(pInputStream, "UTF-8");
    }

    public static String getContent(URL pURI) throws IOException {
        String rString = "";
        InputStream in = pURI.openStream();
        try {
            rString = IOUtils.toString(in);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return rString;
    }

    public static String getContent(InputStream pInputStream, String sCharSet) throws IOException {
        StringBuilder lResult = new StringBuilder();
        BufferedReader lReader = new BufferedReader(new InputStreamReader(pInputStream, sCharSet.length()==0 ? "UTF-8": sCharSet));
        String lLine;
        while ((lLine = lReader.readLine()) != null) {
            lResult.append(lLine).append("\n");
        }
        lReader.close();
        return lResult.toString();
    }

    public static void writeContent(String pContent, File pFile) throws IOException {
        writeContent(pContent, pFile, "UTF-8");
    }

    public static void writeContent(String pContent, File pFile, String sCharSet) throws IOException {
        writeContent(pContent, pFile, false, "UTF-8");
    }

    public static void writeContent(String pContent, File pFile, boolean bAppend) throws IOException {
        writeContent(pContent, pFile, bAppend, "UTF-8");
    }

    public static void writeContent(String pContent, File pFile, boolean bAppend, String sCharSet) throws IOException {
        PrintWriter lWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(pFile), sCharSet.length()==0 ? "UTF-8": sCharSet), bAppend);
        lWriter.println(pContent);
        lWriter.close();
    }

    public static File stringToImage(String pString) throws IOException {

        return stringToImage(pString, "png");

    }

    public static File stringToImage(String pString, String mimeType) throws IOException {

        // create a buffered image
        BufferedImage image = null;
        byte[] imageByte;

        BASE64Decoder decoder = new BASE64Decoder();
        imageByte = decoder.decodeBuffer(pString);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(bis);
        bis.close();

        // write the image to a file
        File outputfile = TempFileHandler.getTempFile();
        ImageIO.write(image, mimeType, outputfile);

        return outputfile;

    }

    public static String setToString (Set<String> input){

        String rString = "";

        int a=1;
        for(String s : input){
            rString+=s;
            if(a<input.size()){
                rString+=",";
            }
            a++;
        }

        return rString;

    }

    public static Long getLongFromUri(String pUri) throws URISyntaxException {

        Long l = -1l;

            try {
                l = Long.valueOf(pUri);
            }
            catch (Exception e){

            }
                if (l > 0) {
                    return l;
                }

            if (pUri.contains("_")) {
                String[] splitNode = pUri.split("_");

                Long rLong = Long.valueOf(splitNode[0]);
                return rLong;
            }

            URI u = null;

            u = new URI(pUri);
            if (u != null) {
                return getLongFromUri(u);
            }


        return l;
    }

    public static Long getLongFromUri(URI pUri){
        Long l = -1l;
        String p = "";
        if(pUri!=null) {
            p = pUri.getPath();
            if(pUri.getPath() == null){
                p = pUri.toString();
            }
            String id = p.substring(p.lastIndexOf("/") + 1);
            l = Long.valueOf(id);
        }

        return l;
    }

    public static String encodeVarName(String pString) {
        if (pString == null) return null;
        StringBuilder lResult = new StringBuilder("__");
        boolean lNeededEncoding = false;
        for (int i=0; i<pString.length(); i++) {
            char c = pString.charAt(i);
            int cp = pString.codePointAt(i);
            if (i == 0) {
                if (((cp >= 65) && (cp <= 90)) || ((cp >= 97) && (cp <= 122))) {
                    lResult.append(c);
                }
                else {
                    lResult.append("_"+cp+"_");
                    lNeededEncoding = true;
                }
            }
            else {
                if (((cp >= 65) && (cp <= 90)) || ((cp >= 97) && (cp <= 122)) || ((cp >= 48) && (cp <= 57))) {
                    lResult.append(c);
                }
                else {
                    lResult.append("_"+cp+"_");
                    lNeededEncoding = true;
                }
            }
        }
        if (lNeededEncoding == false) {
            return pString;
        }
        else {
            return lResult.toString();
        }
    }

    /**
     * Decode a varname. It will only be decoded if it begins with two underscores (see encoding function).
     * Otherwise it will be returned the parameter unchanged.
     * If any error occurs during the decoding the parameter will be returned unchanged.
     * This is the equivalent to JavaScript HuDesktopUtil.decodeVarName
     * @param pString
     * @return
     */
    public static String decodeVarName(String pString) {
        if (pString == null) return null;
        if (pString.indexOf("__") != 0) return pString;
        if (pString.length()==2) return "";
        StringBuilder lResult = new StringBuilder();
        for (int i = 2; i < pString.length(); i++) {
            char c = pString.charAt(i);
            if (c == '_') {
                int lStart = i+1;
                i++;
                while (pString.charAt(i) != '_') i++;
                lResult.append(Character.toChars(Integer.parseInt(pString.substring(lStart, i))));
            }
            else {
                lResult.append(c);
            }
        }
        return lResult.toString();
    }


}
