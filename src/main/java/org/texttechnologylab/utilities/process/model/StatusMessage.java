package org.texttechnologylab.utilities.process.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.texttechnologylab.utilities.helper.DateUtils;
import org.texttechnologylab.utilities.process.ProcessConst;

import java.sql.Timestamp;

/**
 * Created by abrami on 14.10.16.
 */
public class StatusMessage implements Comparable<StatusMessage> {

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String message = "";

    ProcessConst.PROGRESS_STATUS status = ProcessConst.PROGRESS_STATUS.UNKNOW;

    public StatusMessage(String sMessage, ProcessConst.PROGRESS_STATUS pStatus){
        this.message = sMessage;
        this.status = pStatus;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public StatusMessage(JSONObject pObject) throws JSONException {
        if(pObject.has("message")){
            this.message = pObject.getString("message");
        }
        if(pObject.has("status")){
            this.status = ProcessConst.PROGRESS_STATUS.valueOf(pObject.getString("status"));
        }
        if(pObject.has("timestamp")) {
            this.timestamp = new Timestamp(pObject.getLong("timestamp"));
        }

    }

    @Override
    public int hashCode() {
        return (int)timestamp.getTime();
    }

    public String getTimeStampString(){
        return DateUtils.longToDate(timestamp.getTime());
    }

    public Timestamp getTimestamp(){
        return timestamp;
    }

    public String getMessage(){
        return this.message;
    }

    public ProcessConst.PROGRESS_STATUS getStatus(){
        return this.status;
    }

    @Override
    public int compareTo(StatusMessage o) {
        return o.getTimestamp().compareTo(this.getTimestamp());
    }

    @Override
    public String toString() {
        return getTimeStampString()+" "+getMessage()+" "+getStatus();
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject rObject = new JSONObject();

        rObject.put("timestampReadable", getTimeStampString());
        rObject.put("timestamp", getTimestamp().getTime());
        rObject.put("message", getMessage());
        rObject.put("status", getStatus());

        return rObject;
    }


}
