package org.texttechnologylab.utilities.helper.tools.textimager;

import org.apache.uima.cas.CAS;
import org.apache.uima.fit.util.CasIOUtil;
import org.texttechnologylab.utilities.helper.FileUtils;
import org.texttechnologylab.utilities.helper.RESTUtils;
import org.texttechnologylab.utilities.helper.StringUtils;
import org.texttechnologylab.utilities.helper.TempFileHandler;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextImagerMongoDocument implements Comparable<TextImagerMongoDocument>{

    public static enum EXPORT{XMI, TEI};

    private String _id = "";
    private String documentID = "";
    private long job = -1l;

    boolean written = false;

    public TextImagerMongoDocument(String _id, String documentID, long sJob){
        this._id=_id;
        this.documentID=documentID;
        this.job=sJob;
    }

    public String getID(){
        return this._id;
    }

    public String getDocumentID(){
        return this.documentID;
    }

    public Long getJobID(){
        return this.job;
    }

    @Override
    public int compareTo(@NotNull TextImagerMongoDocument o) {
        return o.getID().compareTo(this.getID());
    }

    public String get(){
        Map<String, Object> params = new HashMap<>(0);
        params.put("jobId", job);
        params.put("_id", _id);

        String rString = RESTUtils.getObjectFromRestAsString(TextImagerBigAPI.sURI+"document", RESTUtils.METHODS.GET, params);

        return rString;
    }

    public void write(String sPath, EXPORT e) throws IOException {


        if(!written){

            switch (e){
                case TEI:
                    // todo

                case XMI:
                default:
                    StringUtils.writeContent(get(), new File(sPath+"/"+documentID));
            }


            written=true;
        }

    }

    public void write(String sPath) throws IOException {

        if(!written){
            StringUtils.writeContent(get(), new File(sPath+"/"+documentID));
            written=true;
        }

    }

    public CAS getCas(){

        CAS rCas = null;

        String xmi = get();

        try {
            File tf = TempFileHandler.getTempFile("aaa", "bbb");

            FileUtils.writeContent(xmi, tf);

            CasIOUtil.readCas(rCas, tf);

            tf.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return rCas;

    }
}
