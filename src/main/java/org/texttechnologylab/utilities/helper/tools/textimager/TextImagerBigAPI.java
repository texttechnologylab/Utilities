package org.texttechnologylab.utilities.helper.tools.textimager;

import com.goebl.david.WebbException;
import org.apache.uima.cas.CAS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.utilities.helper.RESTUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TextImagerBigAPI implements Runnable{

    public enum STATE {RUNNING,COMPLETING,COMPLETED,INITIALIZING,FAILURE,UNKNOWN};


    public static String sURI="https://textimager.hucompute.org/rest-big-data/";
    long lJobId = -1;
    String outputPath ="";



    public static STATE lastState = STATE.UNKNOWN;

    Set<TextImagerMongoDocument> documents = new HashSet<>(0);

    public void setJobID(long lJobId){
        this.lJobId=lJobId;
    }

    public void setOutputPath(String sPath){
        this.outputPath = sPath;
    }

    public Long getJobID(){
        return this.lJobId;
    }


    @Override
    public void run() {
        try {

            documents.addAll(getDocuments(lJobId));

            while(isRunning()){

                documents.addAll(getDocuments(lJobId));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                documents.forEach(d->{
                    try {
                        d.write(outputPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void proceed(){
        Thread t = new Thread(this);
        t.start();
    }


    public TextImagerBigAPI(){

    }

    public TextImagerBigAPI(String sApiURI){
        sURI=sApiURI;
    }

    public TextImagerBigAPI(String sApiURI, long lJobId){
        this(sApiURI);
        this.setJobID(lJobId);
    }

    public TextImagerBigAPI(String sApiURI, long lJobId, String sOutputPath){
        this(sApiURI, lJobId);
        this.setOutputPath(sOutputPath);
    }

    public void startJob(String sURI, String sLang, String sInputFormat, String sFileSuffix, String[] pipeline, String sSession) throws JSONException {

        sInputFormat = sInputFormat.startsWith(".") ? sInputFormat.substring(sInputFormat.lastIndexOf("."), sInputFormat.length()) : sInputFormat;

        Map<String, Object> params = new HashMap<>(0);

        params.put("url", sURI);
        params.put("language", sLang);
        params.put("inputFormat", sInputFormat);
        params.put("fileSuffix", sFileSuffix);

        // todo liste
        StringBuilder pPipeline = new StringBuilder();
        for (int i = 0; i < pipeline.length; i++) {
            if(i>0){
                pPipeline.append("&");
            }
            pPipeline.append("pipeline="+pipeline[i]);
        }

        if(sSession.length()>0){
            params.put("session", sSession);
        }

        JSONObject rObject = RESTUtils.getObjectFromRest(TextImagerBigAPI.sURI+"analyse?"+pPipeline.toString(), RESTUtils.METHODS.POST, params);

        if(rObject.has("jobId")){
            long lId = rObject.getLong("jobId");


            if(lId>0) {

                this.setJobID(lId);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                this.proceed();
            }
            else{
                System.err.println("Error JobID is not valid: "+lId);
                lastState= STATE.FAILURE;
            }

        }


    }

    public boolean isRunning(){
        try {
            return getStatus().equals(STATE.RUNNING) || getStatus().equals(STATE.INITIALIZING) || getStatus().equals(STATE.COMPLETING);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isFinish(){
        try {
            return getStatus().equals(STATE.COMPLETED);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public STATE getStatus() throws JSONException {
        return getStatus(this.lJobId);
    }

    public STATE getStatus(long lJobID) throws JSONException {

        STATE rState = null;

        if(lastState.equals(STATE.FAILURE)){
            return lastState;
        }

        Map<String, Object> params = new HashMap<>(0);
        params.put("jobId", lJobID);

        try {
            JSONObject rObject = RESTUtils.getObjectFromRest(sURI + "jobInfo", RESTUtils.METHODS.GET, params);
            rState = STATE.valueOf(rObject.getString("state").toUpperCase());

            if(rState.equals(STATE.RUNNING)) {
                System.out.println(rState+" "+getJobID()+" (Estimate: "+rObject.getString("completion")+") "+rObject.getInt("done")+ " / "+rObject.getInt("total"));
            }
            if(rState.equals(STATE.COMPLETING)) {
                System.out.println(rState+": "+getJobID()+" "+rObject.getInt("done")+ " / "+rObject.getInt("total"));
            }
            if(rState.equals(STATE.INITIALIZING) && !lastState.equals(rState)) {
                System.out.println(rState+": "+getJobID());
            }

        }
        catch (WebbException e){
            rState = STATE.FAILURE;
            //e.printStackTrace();
        }

        lastState = rState;



        return rState;

    }


    public Set<TextImagerMongoDocument> getDocuments() throws JSONException {
        if(lastState.equals(STATE.COMPLETING)){
            return this.getDocuments(this.lJobId);
        }
        else{
            return new HashSet<>(0);
        }

    }

    public Set<TextImagerMongoDocument> getDocuments(long lJobID) throws JSONException {
        if(lastState.equals(STATE.COMPLETING)){
            return getDocuments(lJobID, 100, 0);
        }
        else{
            return new HashSet<>(0);
        }

    }

    public Set<CAS> getDocumentsAsCAS() throws JSONException {
        if(lastState.equals(STATE.COMPLETING)){
            return getDocumentsAsCAS(this.lJobId);
        }
        else{
            return new HashSet<>(0);
        }
    }

    public Set<CAS> getDocumentsAsCAS(long lJobID) throws JSONException {

        if(!lastState.equals(STATE.COMPLETING)){
            return new HashSet<>(0);
        }

        Set<TextImagerMongoDocument> docs = getDocuments(lJobID, 100, 0);
        Set<CAS> rCasSet = new HashSet<>(0);

        docs.forEach(d->{
            rCasSet.add(d.getCas());
        });

        return rCasSet;
    }

    public Set<TextImagerMongoDocument> getDocuments(long lJobID, long limit, long page) throws JSONException {

        if(!lastState.equals(STATE.COMPLETING)){
            return new HashSet<>(0);
        }

        Map<String, Object> params = new HashMap<>(0);
        params.put("jobId", lJobID);
        params.put("limit", limit);
        params.put("page", page);

        JSONObject rObject = RESTUtils.getObjectFromRest(sURI+"listDocuments", RESTUtils.METHODS.GET, params);

        Set<TextImagerMongoDocument> rSet = new HashSet<>(0);

            if(rObject.has("documentIds")){
                JSONArray rArray = rObject.getJSONArray("documentIds");

                for(int i=0; i<rArray.length(); i++){
                    JSONObject o = rArray.getJSONObject(i);

                    TextImagerMongoDocument timd = new TextImagerMongoDocument(o.getString("_id"), o.getString("documentId"), lJobID);
                    rSet.add(timd);

                }
            }

        if(rObject.has("total")){
            int iTotal = rObject.getInt("total");
            if(iTotal>limit*page){
                rSet.addAll(getDocuments(lJobID, limit, ++page));
            }
        }

        return rSet;

    }

    @Test
    public void testExport() throws JSONException {

        TextImagerBigAPI etib = new TextImagerBigAPI();
        etib.setJobID(22616);
        etib.setOutputPath("/tmp/output");
        System.out.println(etib.getStatus(22616));
        etib.getDocuments(22616).forEach(d->{
            try {
                d.write("/tmp/output");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //etib.proceed();


    }

}

