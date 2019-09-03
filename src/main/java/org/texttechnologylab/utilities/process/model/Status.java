package org.texttechnologylab.utilities.process.model;

import io.swagger.annotations.ApiModelProperty;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.texttechnologylab.utilities.helper.DateUtils;
import org.texttechnologylab.utilities.process.ProcessConst;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * Created by abrami on 20.07.16.
 */
public class Status {

    @ApiModelProperty(example = "Some status is displayed", required = true)
    Set<StatusMessage> messages = Collections.synchronizedSet(new HashSet<StatusMessage>());

    @ApiModelProperty(example = "RUNNING", required = true)
    private ProcessConst.PROGRESS_STATUS status;

    @ApiModelProperty(example = "0.5", required = true)
    private double absolute;

    @ApiModelProperty(example = "20291010291-39101919", required = true)
    private String pid;

    @ApiModelProperty(example = "2016-08-09 12:12:01", required = true)
    private Timestamp create;

    @ApiModelProperty(example = "2016-08-09 12:12:05", required = true)
    private Timestamp start;

    @ApiModelProperty(example = "2016-08-09 12:12:15", required = true)
    private Timestamp finish;

    @ApiModelProperty(example = "60", required = true)
    private long duration;

    private String uri = "";

    private final Semaphore semaphore = new Semaphore(1);

    public Status(String uri, String message, ProcessConst.PROGRESS_STATUS status, double absolute, String pid, Timestamp create, Timestamp start, Timestamp finish){
        setStatus(status);
        setAbsolute(absolute);
        setPID(pid);
        this.setURI(uri);
        setCreateTimeStamp(create);
        setStartTimeStamp(start);
        setFinishTimeStamp(finish);
        try {
            setMessage(message);
        } catch (InterruptedException e) {
        }

    }

    public Status(String uri, Set<StatusMessage> messages, ProcessConst.PROGRESS_STATUS status, double absolute, String pid, Timestamp create, Timestamp start, Timestamp finish){
        setStatus(status);
        setAbsolute(absolute);
        setPID(pid);
        this.setURI(uri);
        setCreateTimeStamp(create);
        setStartTimeStamp(start);
        setFinishTimeStamp(finish);
        this.messages = messages;
    }

    public Status(JSONObject pStatus) throws JSONException {
        this.setStatus(ProcessConst.PROGRESS_STATUS.valueOf(pStatus.getString("status")));
        this.setPID(pStatus.getString("pid"));
        this.start = Timestamp.valueOf(pStatus.getString("start"));
        this.finish= Timestamp.valueOf(pStatus.getString("finish"));
        this.create= Timestamp.valueOf(pStatus.getString("create"));
        this.duration = pStatus.getLong("duration");
        this.setAbsolute(pStatus.getDouble("absolute"));
        this.setURI(pStatus.getString("uri"));
        try {
            if(pStatus.has("message")) {
                this.setMessage(pStatus.getString("message"));
            }
        } catch (InterruptedException e) {
        }

        if(pStatus.has("messageList")){

            JSONArray r = pStatus.getJSONArray("messageList");

            for(int a=0; a<r.length(); a++){
                messages.add(new StatusMessage(r.getJSONObject(a)));
            }

        }

    }

    public void setMore(String sURI){
        this.setURI(sURI);
    }

    public Status(String sStatus) throws JSONException {
        this(new JSONObject(sStatus));
    }

    public void setTimes(Timestamp create, Timestamp start, Timestamp finish, long duration){
        this.create = create;
        this.start = start != null ? start : new Timestamp(0l);
        this.finish = finish != null ? finish : new Timestamp(0l);
        this.duration = duration > 0 ? duration : -1l;
    }

    public void setTimes(Timestamp create, Timestamp start, Timestamp finish){
        this.create = create;
        this.start = start != null ? start : new Timestamp(0l);
        this.finish = finish != null ? finish : new Timestamp(0l);
        calculateDuration();
    }

    private void calculateDuration(){

        if(this.start!=null) {

            if (this.finish == null || this.finish.getTime() == 0l) {
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                this.duration = ts.getTime() - this.start.getTime();
            } else {
                this.duration = this.finish.getTime() - this.start.getTime();
            }
        }
        else{
            this.duration = -1l;
        }

    }

    public JSONObject toJSON() throws JSONException {
        return toJSON(false);
    }

    public JSONObject toJSON(boolean full) throws JSONException {
        JSONObject rObject = new JSONObject();

            rObject.put("uri", this.getURI());

            rObject.put("status", this.getStatus());

            if(full){
                try {
                    rObject.put("messageList", getMessagesJSON());
                } catch (InterruptedException e) {
                }
            }

            rObject.put("absolute", this.getAbsolute());
            rObject.put("pid", this.getPID());
            rObject.put("start", this.getStartTimeStamp());
            rObject.put("create", this.getCreateTimeStamp());
            rObject.put("finish", this.getFinishTimeStamp());
            rObject.put("duration", this.getDuration());
            rObject.put("durationReadable", this.getDurationReadable());
            rObject.put("statusRequest", this.getStatusRequest());
            rObject.put("resultRequest", this.getResultRequest());
            rObject.put("stopRequest", this.getStopRequest());
            rObject.put("message", this.getMessageString());

        return rObject;
    }

    public boolean isFinish(){
        return this.getStatus().equals(ProcessConst.PROGRESS_STATUS.FINISH) || this.getStatus().equals(ProcessConst.PROGRESS_STATUS.FAILURE);
    }

    public boolean isRunning(){
        return this.getStatus().equals(ProcessConst.PROGRESS_STATUS.RUNNING);
    }

    public boolean isOpen(){
        return this.getStatus().equals(ProcessConst.PROGRESS_STATUS.OPEN);
    }

    public StatusMessage getMessage() {

        StatusMessage rMessage = null;
        try {
            rMessage = this.getMessages().parallelStream().sorted().findFirst().get();
        }
        catch (Exception e){

        }
        return rMessage;

    }

    public String getMessageString(){

        StatusMessage s = getMessage();

        if(s!=null){
            return s.getMessage();
        }

        return "";

    }

    public Set<StatusMessage> getMessages() throws InterruptedException {
        return this.messages;
        //return this.getMessages(this.messages.size());
    }

    public Set<StatusMessage> getMessages(int count) throws InterruptedException {

        Set<StatusMessage> rSet = new HashSet<>(0);

        Iterator<StatusMessage> i = this.messages.iterator();

        while(i.hasNext()){
            rSet.add(i.next());
        }

        return rSet;

    }

    public void setMessage(String sMessage) throws InterruptedException {
        this.getMessages().add(new StatusMessage(sMessage, this.getStatus()));
    }

    public void setMessageObject(StatusMessage pMessage) throws InterruptedException {
        this.getMessages().add(pMessage);
    }

    public JSONArray getMessagesJSON() throws InterruptedException {

        JSONArray rArray = new JSONArray();

        List<StatusMessage> sm = getMessages().stream().collect(Collectors.toList());
        sm.stream().forEachOrdered(m->{
            try {
                rArray.put(m.toJSON());
            } catch (JSONException e) {
            }
        });

        return rArray;

    }

    public String getStatusString() {
        return status.toString();
    }

    public ProcessConst.PROGRESS_STATUS getStatus() {
        return status;
    }
    public void setStatus(ProcessConst.PROGRESS_STATUS pstatus) {
        this.status = pstatus;
    }

    public double getAbsolute() {
        return absolute;
    }
    public void setAbsolute(double absolute) {
        this.absolute = absolute;
    }

    public String getPID() {
        return pid;
    }
    public void setPID(String pid) {
        this.pid = pid;
    }

    public Timestamp getCreateTimeStamp(){

        if(create==null){
            return new Timestamp(0l);
        }

        return create;
    }
    void setCreateTimeStamp(Timestamp create){
        this.create = create;
    }

    public Timestamp getStartTimeStamp(){

        if(start==null){
            return new Timestamp(0l);
        }

        return start;

    }
    void setStartTimeStamp(Timestamp start){
        this.start = start;
    }
    public Timestamp getFinishTimeStamp(){

        if(finish==null){
            return new Timestamp(0l);
        }
        return finish;
    }

    void setFinishTimeStamp(Timestamp finish){
        this.finish = finish;
    }

    public long getDuration(){

        if(this.duration<=0){
            calculateDuration();
        }

        return duration;
    }

    public String getDurationReadable(){

        long duration = getDuration();
        return DateUtils.getTime(duration);
    }

    public String getURI(){
        return this.uri;
    }
    public void setURI(String uri){
        this.uri = uri;
    }

    @ApiModelProperty(example = "http://mytentacleurl/MYPID/status", required = true)
    public String getStatusRequest(){
        String rString = getURI()+"/"+this.getPID()+"/status";
        return rString;
    }

    @ApiModelProperty(example = "http://mytentacleurl/MYPID/result", required = true)
    public String getResultRequest(){
        String rString = getURI()+"/"+this.getPID()+"/result";
        return rString;
    }

    @ApiModelProperty(example = "http://mytentacleurl/MYPID/stop", required = true)
    public String getStopRequest(){
        String rString = getURI()+"/"+this.getPID()+"/stop";
        return rString;
    }

}
