package org.texttechnologylab.utilities.helper;

import com.goebl.david.Request;
import com.goebl.david.Webb;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Response;

import javax.activation.MimetypesFileTypeMap;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.util.*;

import static spark.Spark.halt;

/**
 * Created by abrami on 24.08.16.
 */

public class RESTUtils {

    public static enum METHODS {GET, POST, DELETE, PUT}

    public static void enableSSLTrustCertificates(){

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }};

            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
            }

    }

    public static JSONObject getObjectFromRest(String uri, String session){
        JSONObject rObject = new JSONObject();
        String rString = "";

        Set<Map<String, Object>> params= new HashSet<>(0);
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("session", session);
        params.add(m);

        return getObjectFromRest(uri, METHODS.GET, params);

    }

    public static JSONObject getObjectFromRest(String uri, METHODS method, Set<Map<String, Object>> params){

        enableSSLTrustCertificates();

        JSONObject rObject = new JSONObject();

        Webb webb = Webb.create();


        Request request = null;

        switch (method){
            case GET:
                request = webb.get(uri);
                break;

            case POST:
                request = webb.post(uri);
                break;

            case DELETE:
                request = webb.delete(uri);
                break;

            case PUT:
                request = webb.put(uri);
                break;
        }


        for(Map<String, Object> k : params){
            Iterator<String> kSet = k.keySet().iterator();

            while(kSet.hasNext()){
                String s = kSet.next();
                request = request.param(s, k.get(s));
            }

        }

        rObject = request.ensureSuccess().asJsonObject().getBody();

        return rObject;
    }

    public static JSONObject getObjectFromRest(String uri, METHODS method, String sBody){

        enableSSLTrustCertificates();

        JSONObject rObject = new JSONObject();

        Webb webb = Webb.create();


        Request request = null;

        switch (method){
            case GET:
                request = webb.get(uri);
                break;

            case POST:
                request = webb.post(uri);
                break;

            case DELETE:
                request = webb.delete(uri);
                break;

            case PUT:
                request = webb.put(uri);
                break;
        }


        request = request.body(sBody);

        rObject = request.ensureSuccess().asJsonObject().getBody();

        return rObject;
    }

    public static JSONObject getObjectFromRest(String uri, METHODS method, Map<String, Object> params){

        enableSSLTrustCertificates();

        JSONObject rObject = new JSONObject();

        Webb webb = Webb.create();

        Request request = null;

        switch (method){
            case GET:
                request = webb.get(uri);
                break;

            case POST:
                request = webb.post(uri);
                break;

            case DELETE:
                request = webb.delete(uri);
                break;

            case PUT:
                request = webb.put(uri);
                break;
        }

        Set<String> sSet = params.keySet();

        for (String s : sSet) {
            request = request.param(s, params.get(s));

        }

        rObject = request.ensureSuccess().asJsonObject().getBody();

        return rObject;
    }

    public static JSONObject getObjectFromRest(String uri, METHODS method, Map<String, Object> params, Map<String, Object> headers){

        enableSSLTrustCertificates();

        JSONObject rObject = new JSONObject();

        Webb webb = Webb.create();

        Request request = null;

        switch (method){
            case GET:
                request = webb.get(uri);
                break;

            case POST:
                request = webb.post(uri);
                break;

            case DELETE:
                request = webb.delete(uri);
                break;

            case PUT:
                request = webb.put(uri);
                break;
        }

        Set<String> hSet = headers.keySet();

        for (String s : hSet) {
            request = request.header(s, headers.get(s));
        }

        Set<String> sSet = params.keySet();

        for (String s : sSet) {
            request = request.param(s, params.get(s));

        }

        rObject = request.ensureSuccess().asJsonObject().getBody();

        return rObject;
    }

    public static String getObjectFromRestAsString(String uri, METHODS method, Map<String, Object> params){

        enableSSLTrustCertificates();

        String rObject = "";

        Webb webb = Webb.create();

        Request request = null;

        switch (method){
            case GET:
                request = webb.get(uri);
                break;

            case POST:
                request = webb.post(uri);
                break;

            case DELETE:
                request = webb.delete(uri);
                break;

            case PUT:
                request = webb.put(uri);
                break;
        }

        Set<String> sSet = params.keySet();

        for (String s : sSet) {
            request = request.param(s, params.get(s));

        }

        rObject = request.ensureSuccess().asString().getBody();

        return rObject;
    }

    public static boolean returnDocument(Response response, File pFile, String mime) throws IOException, JSONException {

        String charset = FileUtils.getCharset(pFile);
        BufferedInputStream buffer = FileUtils.getBufferedFile(pFile);

        String s = IOUtils.toString(buffer, charset);

        response.header("Access-Control-Allow-Origin", "*");
        response.header("Access-Control-Allow-Methods", "POST,GET");
        response.header("Access-Control-Allow-Headers", "Content-Type, *");
        response.header("Access-Control-Allow-Credentials", "true");

        if(mime.length()>0) {
            response.raw().setContentType(mime);
        }
        response.raw().setHeader("Content-Disposition", "attachment; filename="+pFile.getName());
        response.raw().setCharacterEncoding(charset);

        try {

            PrintWriter out = response.raw().getWriter();

            out.print(s);
            out.close();

        } catch (Exception e) {
            return SparkUtils.prepareReturnFailure(response, e.getMessage(), e.getStackTrace());
        }
        halt(200);
        return true;

    }

    public static boolean returnDocument(Response response, File pFile, String mime, String sCharset) throws IOException, JSONException {

        String charset = sCharset;
        String s = "";


        BufferedInputStream buffer = FileUtils.getBufferedFile(pFile);
        if(sCharset.length()>0){
            charset = FileUtils.getCharset(pFile);
            s = IOUtils.toString(buffer, charset);
        }
        else{
            s = IOUtils.toString(buffer);
        }

        response.header("Access-Control-Allow-Origin", "*");
        response.header("Access-Control-Allow-Methods", "POST,GET");
        response.header("Access-Control-Allow-Headers", "Content-Type, *");
        response.header("Access-Control-Allow-Credentials", "true");

        if(mime.length()>0) {
            response.raw().setContentType(mime);
        }
        response.raw().setHeader("Content-Disposition", "attachment; filename="+pFile.getName());
        if(charset.length()>0) {
            response.raw().setCharacterEncoding(charset);
        }
        try {

            PrintWriter out = response.raw().getWriter();

            out.print(s);
            out.close();

        } catch (Exception e) {
            return SparkUtils.prepareReturnFailure(response, e.getMessage(), e.getStackTrace());
        }
        halt(200);
        return true;

    }

    public static boolean returnDocument(Response response, File pFile) throws IOException, JSONException {

        return returnDocument(response, pFile, "", "");

    }


    public static boolean returnFile(Response response, File pFile) throws IOException, JSONException {


        final String mimetype = new MimetypesFileTypeMap().getContentType(pFile);

        response.header("Content-Disposition", String.format("attachment; filename="+pFile.getName()));
        response.raw().setContentLength((int) pFile.length());
        response.type(mimetype);

        final byte[] out = org.apache.commons.io.FileUtils.readFileToByteArray(pFile);
        response.raw().setContentType(mimetype);
        final OutputStream os = response.raw().getOutputStream();
        for (int start = 0; start < out.length; start += 65536) {
            os.write(out, start, Math.min(out.length - start, 65536));
        }

        return true;

    }

}
