package org.texttechnologylab.utilities.process.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.texttechnologylab.utilities.process.exception.InvalidModelException;

/**
 * Created by abrami on 28.09.16.
 */
public class ReturnModel {

    private Status pStatus;
    private Metadata pMetadata;
    private Result pReturn;

    public ReturnModel(Status pStatus, Metadata pMetadata, Result pReturn){
        this.pStatus = pStatus;
        this.pMetadata = pMetadata;
        this.pReturn = pReturn;
    }

    public ReturnModel(JSONObject pObject) throws InvalidModelException, JSONException {
            checkValid(pObject);
            this.pReturn = new Result(pObject.getJSONObject("result"));
            this.pStatus = new Status(pObject.getJSONObject("status"));
            this.pMetadata = new Metadata(pObject.getJSONObject("metadata"));
    }

    public Status getStatus(){
        return this.pStatus;
    }

    public Metadata getMetadata(){
        return this.pMetadata;
    }

    public Result getResult(){
        return this.pReturn;
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject rObject = new JSONObject();

            rObject.put("metadata", this.pMetadata.toJSON());
            rObject.put("status", this.getStatus().toJSON());
            rObject.put("result", this.getResult().toJSON());

        return rObject;

    }

    public JSONObject toJSON(boolean fullStatus, boolean fullResult) throws JSONException {

        JSONObject rObject = new JSONObject();

            rObject.put("metadata", this.pMetadata.toJSON());
            rObject.put("status", this.getStatus().toJSON(fullStatus));
            rObject.put("result", this.getResult().toJSON(fullResult));

        return rObject;

    }

    void checkValid(JSONObject rModel) throws InvalidModelException, JSONException {

        if(!rModel.has("metadata")){
            throw new InvalidModelException("Element 'metadata' is required!");
        }
        else{
            if(!(rModel.get("metadata") instanceof JSONObject)){
                throw new InvalidModelException("Element 'metadata' have to be a JSONObject");
            }
        }


        if(!rModel.has("result")){
            throw new InvalidModelException("Element 'result' is required!");
        }
        else{
            if(!(rModel.get("result") instanceof JSONObject)){
                throw new InvalidModelException("Element 'result' have to be a JSONArray");
            }
        }

        if(!rModel.has("status")){
            throw new InvalidModelException("Element 'status' is required!");
        }
        else{
            if(!(rModel.get("status") instanceof JSONObject)){
                throw new InvalidModelException("Element 'status' have to be a JSONObject");
            }
        }

    }

}
