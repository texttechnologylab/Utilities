package org.texttechnologylab.utilities.process.model;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by abrami on 13.09.16.
 */
public class Metadata {

    private Identify process;
    private String query;
    private String name;
    private HashMap<String,String> queryParams;

    public Identify getIdentify(){
        return this.process;
    }

    public String getQuery(){
        return this.query;
    }

    public String getName(){
        return this.name;
    }

    public String getURL(){
        return this.getIdentify().getURL();
    }

    public HashMap<String,String> getQueryParams(){return this.queryParams;}

    @Ignore
    public Metadata(Identify pIdentify, String query){
        this.process = pIdentify;
        this.query = query;
    }

    @Ignore
    public Metadata(Identify pIdentify, String query, String name){
        this.process = pIdentify;
        this.query = query;
        this.name = name;
    }

    @Ignore
    public Metadata(JSONObject pMetaData) throws JSONException {

        this.process = new Identify(pMetaData.getJSONObject("identify"));
        this.query = pMetaData.getString("request");
        if(pMetaData.has("name")){
            this.name = pMetaData.getString("name");
        }

    }

    //Fuer sparql tentacle
    @Ignore
    public Metadata(Identify pIdentify, String query, HashMap<String,String> queryParams) throws JSONException {
        this.process = pIdentify;
        this.query = query;
        this.queryParams = queryParams;
    }

    @Ignore
    public JSONObject toJSON() throws JSONException {

        JSONObject rObject = process.toJSON();
        rObject.put("request", this.query);
        rObject.put("name", this.name);
        return rObject;

    }

}
