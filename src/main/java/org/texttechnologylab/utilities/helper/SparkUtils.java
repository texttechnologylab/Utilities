package org.texttechnologylab.utilities.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import spark.HaltException;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static spark.Spark.halt;

/**
 * Created by abrami on 28.09.16.
 */
public class SparkUtils {

    public static boolean prepareReturn(spark.Response res, String result, int status, String mimetype){
        PrintWriter out = null;
        try {
            res.raw().setContentType(mimetype);
            res.type(mimetype);
            res.header("Content-Type", mimetype);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.header("Access-Control-Allow-Credentials", "true");
            res.header("Allow", "POST, GET, PUT, DELETE");
            res.raw().setCharacterEncoding(StandardCharsets.UTF_8.name());
            out = res.raw().getWriter();

            out.println(result);

            out.close();
        }
        catch (Exception e){
            try {
                halt(404, e.getMessage());
            }
            catch (HaltException eh){
                System.out.println(eh.getMessage());
            }
            return false;
        }

        try {
            halt(status);
        }
        catch (HaltException he){
            System.out.println(he.getMessage());
        }
        return true;
    }

    public static boolean prepareReturn(spark.Response res, String result){

        return prepareReturn(res, result, 200, "application/json");

    }

    public static boolean prepareReturnHTML(spark.Response res, String result){

        return prepareReturn(res, result, 200, "text/html");

    }

    public static boolean prepareReturn(spark.Response res, String result, String sMimeType){

        return prepareReturn(res, result, 200, sMimeType);

    }

    public static boolean prepareReturn(spark.Response res, JSONObject rObject){

        return prepareReturn(res, rObject.toString(), 200, "application/json");

    }

    public static boolean prepareReturnSuccess(spark.Response res, JSONObject rObject) throws JSONException {

        rObject.put("success", true);
        return prepareReturn(res, rObject.toString(), 200, "application/json");

    }

    public static boolean prepareReturn(spark.Response res, JSONArray rArray) throws JSONException {

        JSONObject rObject = new JSONObject();
        rObject.put("success", true);
        rObject.put("result", rArray);

        return prepareReturn(res, rObject.toString(), 200, "application/json");

    }

    public static boolean prepareReturnFailure(spark.Response res, JSONObject rObject){

        return prepareReturn(res, rObject.toString(), 400, "application/json");

    }

    public static boolean prepareReturnFailure(spark.Response res, String message, StackTraceElement[] stacktrace) throws JSONException {

        String sStacktrace = "";

        for(StackTraceElement element : stacktrace){
            sStacktrace.concat(element.toString());
        }

        JSONObject rObject = new JSONObject();
        rObject = createFailure(message, sStacktrace);

        return prepareReturn(res, rObject.toString(), 400, "application/json");

    }

    public static JSONObject createSuccess(String result) throws JSONException {
        JSONObject rObject = new JSONObject();
        rObject.put("success", true);
        rObject.put("result", result);
        return rObject;
    }

    public static JSONObject createSuccess(JSONObject result) throws JSONException {
        JSONObject rObject = new JSONObject();
        rObject.put("success", true);
        rObject.put("result", result);
        return rObject;
    }

    public static JSONObject createSuccess(JSONArray result) throws JSONException {
        JSONObject rObject = new JSONObject();
        rObject.put("success", true);
        rObject.put("result", result);
        return rObject;
    }

    public static JSONObject createFailure(String sMessage) throws JSONException {
        JSONObject rObject = new JSONObject();

            rObject.put("success", false);
            rObject.put("message", sMessage);

        return rObject;
    }
    public static JSONObject createFailure(String sMessage, String sStacktrace) throws JSONException {
        JSONObject rObject = createFailure(sMessage);
        rObject.put("stacktrace", sStacktrace);
        return rObject;
    }

}
