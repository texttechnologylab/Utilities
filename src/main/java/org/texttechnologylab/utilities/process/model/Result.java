package org.texttechnologylab.utilities.process.model;

import com.goebl.david.Webb;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.texttechnologylab.utilities.process.HucomputeProcess;
import org.texttechnologylab.utilities.process.ProcessConst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by abrami on 12.10.16.
 */
public class Result {

    private int iLimit = 10000;
    private int counter = 0;
    private int maxpage = 0;
    List<JSONObject> resultArray = Collections.synchronizedList(new ArrayList<JSONObject>());
    HucomputeProcess huProcess = null;

    public Result(HucomputeProcess huP, int size){
        this(huP);
        this.setLimit(size);
    }

    public Result(HucomputeProcess huP){
        this.setLimit(10000); // default
        this.huProcess = huP;
    }

    public Result(JSONObject pObject) throws JSONException {
        this.setLimit(pObject.getInt("pagesize"));
        if(pObject.has("result")){
            this.setResult(pObject.getJSONArray("result"));
        }
        this.counter = pObject.getInt("page");
        this.maxpage = pObject.getInt("maxpage");
    }

    public int getMaxpage(){
        return this.maxpage;
    }

    public void setMaxpage(){
        this.maxpage = (resultArray.size()%getLimit()==0) ? (resultArray.size()/getLimit()) : (resultArray.size()/getLimit())+1;
    }

    public int getLimit(){
        return this.iLimit;
    }

    public void setLimit(int iLimit){
        this.iLimit = iLimit;
    }

    public void setResult(JSONArray pArray){
        resultArray = Collections.synchronizedList(new ArrayList<JSONObject>());
        for(int a=0; a<pArray.length(); a++){
            try {
                resultArray.add(pArray.getJSONObject(a));
            } catch (JSONException e) {
                this.huProcess.getLogger().error(e.getMessage(), e);
            }
        }
        setMaxpage();
    }

    public void setResult(ArrayList<JSONObject> pArray){
        resultArray.addAll(pArray);
        setMaxpage();
    }

    public void addResult(JSONArray pArray) throws JSONException {
        for(int a=0; a<pArray.length(); a++){
            resultArray.add(pArray.getJSONObject(a));
        }
        setMaxpage();
    }

    public void addResult(JSONObject pObject){
        this.resultArray.add(pObject);
        setMaxpage();
    }

    public JSONObject getNextResult(boolean full) throws JSONException {
        JSONObject rObject = getResult(counter++, full);
        return rObject;
    }

    public JSONObject getResult() throws JSONException {
        return getResult(0, true);
    }

    public JSONObject toJSON() throws JSONException {
        return getResult();
    }


    public JSONObject toJSON(boolean full) throws JSONException {
        return getNextResult(full);
    }

    public JSONObject getNextPageRemote(){

        JSONObject rObject = new JSONObject();

            String request = this.huProcess.getStatus().getResultRequest()+"&page="+(counter+1);

            Webb webb = Webb.create();

            rObject = webb.get(request).ensureSuccess().asJsonObject().getBody();

        return rObject;
    }

    public JSONObject getResult(int iPage) throws JSONException {
        return getResult(iPage, true);
    }

    public JSONObject getResult(int iPage, boolean full) throws JSONException {

        JSONObject rObject = new JSONObject();

        rObject.put("size", resultArray.size());
        rObject.put("pagesize", getLimit());
        rObject.put("maxpage", this.getMaxpage());
        rObject.put("page", iPage);

        this.counter = iPage;

            if(full) {

                if (iPage > 0) {

                    JSONArray rArray = new JSONArray();

                    int start = iPage * getLimit();
                    int stop = start + getLimit();

                    if (stop > resultArray.size()) {
                        stop = resultArray.size();
                    }

                    resultArray.subList(start, stop).stream().forEach(a->{
                        rArray.put(a);
                    });

                    rObject.put("data", rArray);

                } else if(iPage<0) {
                    rObject.put("data", resultArray);
                    huProcess.setProgressStatus(ProcessConst.PROGRESS_STATUS.DELIVERED);
                }

                if(iPage==this.getMaxpage()){
                    huProcess.setProgressStatus(ProcessConst.PROGRESS_STATUS.DELIVERED);
                }
            }
            else{
                rObject.put("data", new JSONArray());
            }

        return rObject;

    }

}
