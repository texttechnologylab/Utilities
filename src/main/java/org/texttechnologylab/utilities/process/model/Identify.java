package org.texttechnologylab.utilities.process.model;

import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

/**
 * Created by abrami on 08.09.16.
 */
public class Identify {

    @ApiModelProperty(example = "http://someuri.com/sometentacle", required = true)
    String url = "";

    @ApiModelProperty(example = "Some Tentacle", required = true)
    String name = "";

    @ApiModelProperty(example = "Some nice Tentacle Description", required = true)
    String description = "";

    @ApiModelProperty(example = "1970-01-01 00:00:00", required = true)
    Timestamp timestamp= null;

    @Ignore
    public JSONObject toJSON() throws JSONException {
        JSONObject rObject = new JSONObject();

            rObject.put("url", this.url);
            rObject.put("name", this.name);
            rObject.put("description", this.description);
            rObject.put("timestamp", this.timestamp);

        return new JSONObject().put("identify", rObject);
    }

    @Ignore
    public Identify(String url, String name, String description, Timestamp timestamp){
        this.url=url;
        this.name=name;
        this.description=description;
        this.timestamp=timestamp;
    }

    @Ignore
    public Identify(JSONObject pObject) throws JSONException {

        JSONObject tObject = pObject;

        if(pObject.has("identify")){
            tObject = pObject.getJSONObject("identify");
        }

        this.url=tObject.getString("url");
        this.name=tObject.getString("name");
        this.description=tObject.getString("description");
        this.timestamp= Timestamp.valueOf(tObject.getString("timestamp"));
    }

    public String getName(){ return this.name; }
    public String getURL(){ return this.url; }
    public String getDescription(){ return this.description; }
    public Timestamp getTimestamp(){ return this.timestamp; }

}
