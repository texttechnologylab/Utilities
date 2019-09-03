package org.texttechnologylab.utilities.process.impl;

import com.goebl.david.Webb;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.texttechnologylab.utilities.helper.ProcessUtils;
import org.texttechnologylab.utilities.process.HucomputeProcess;
import org.texttechnologylab.utilities.process.ProcessConst;
import org.texttechnologylab.utilities.process.ThreadManager;
import org.texttechnologylab.utilities.process.model.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by abrami on 28.09.16.
 */
public class HucomputeProcess_Impl implements HucomputeProcess {

    protected String sUri = "";
    private String sPID = "";
    protected boolean bRunnable = true;
    protected boolean bPause = false;

    protected Set<StatusMessage> status = new HashSet<>(0);

    private Logger pLogger = null;

    protected Timestamp create = null;
    protected Timestamp start = null;
    protected Timestamp finish = null;

    protected ThreadManager tManager = null;

    protected Thread pThread = null;

    protected double absolute = 0.0d;

    protected Metadata pMetadata = null;

    protected Runnable pRunnable = null;

    protected ProcessConst.PROGRESS_STATUS pStatus = ProcessConst.PROGRESS_STATUS.OPEN;

    public HucomputeProcess_Impl(Metadata metadata, Logger pLogger){
        this.pLogger = pLogger;
        this.pMetadata = metadata;
        this.sUri = pMetadata.getURL();
        this.create = new Timestamp(System.currentTimeMillis());
        sPID = ProcessUtils.generatePID();

    }

    @Override
    public String getPID() {
        return this.sPID;
    }

    @Override
    public Logger getLogger(){
        return this.pLogger;
    }

    @Override
    public void startProcess() {
        this.start = new Timestamp(System.currentTimeMillis());
        this.setProgressStatus(ProcessConst.PROGRESS_STATUS.RUNNING);
        if(pRunnable!=null){
            this.pThread = new Thread(this.pRunnable);
        }
        else{
            this.pThread = new Thread(this);
        }

        this.pThread.start();
        insertIntoProcessmanagement();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                insertIntoProcessmanagement();
            }
        }, 0, 10000);
    }

    private void insertIntoProcessmanagement(){

        final String path = "http://processes.hucompute.org/process";
        try{

            Webb webb = Webb.create();

            JSONObject rObject = webb.post(path).param("query", this.getStatus().getStatusRequest()).ensureSuccess().asJsonObject().getBody();

        }
        catch (Exception e){
            this.pLogger.error(e.getMessage(), e);
        }

    }

    @Override
    public void startProcess(ThreadManager tManager) {

        this.tManager = tManager;
        startProcess();
    }

    @Override
    public void setRunnable(Runnable r) {
        this.pRunnable = r;
    }

    @Override
    public void stopProcess() {
        this.bRunnable = false;
        this.setProgressStatus(ProcessConst.PROGRESS_STATUS.FINISH);
    }

    @Override
    public void pauseProcess() {
        if(isRunning()) {
            this.setProgressStatus(ProcessConst.PROGRESS_STATUS.PAUSED);
            this.bPause = true;
        }
    }

    @Override
    public void resumeProcess() {
        if(isPaused()) {
            this.setProgressStatus(ProcessConst.PROGRESS_STATUS.RUNNING);
            this.bPause = false;
        }
    }

    @Override
    public boolean isRunning() {
        return this.getProgressStatus().equals(ProcessConst.PROGRESS_STATUS.RUNNING);
    }

    @Override
    public boolean isPaused() {
        return this.bPause && this.getProgressStatus().equals(ProcessConst.PROGRESS_STATUS.PAUSED);
    }

    @Override
    public boolean isFinish() {
        return getProgressStatus().equals(ProcessConst.PROGRESS_STATUS.FINISH);
    }

    @Override
    public boolean isDelivered() {
        return getProgressStatus().equals(ProcessConst.PROGRESS_STATUS.DELIVERED);
    }

    @Override
    public boolean isFailed() {
        return getProgressStatus().equals(ProcessConst.PROGRESS_STATUS.FAILURE);
    }

    @Override
    public Timestamp getCreate() {
        return this.create;
    }

    @Override
    public Timestamp getStart() {
        return this.start;
    }

    @Override
    public Timestamp getFinish() {
        return this.finish;
    }

    @Override
    public long getDuration() {

        if(finish==null || start == null){
            return System.currentTimeMillis()-create.getTime();
        }
        return finish.getTime()-start.getTime();
    }

    @Override
    public JSONObject toJSON() throws JSONException {

        ReturnModel rm = new ReturnModel(this.getStatus(), this.pMetadata, this.getResult());
        return rm.toJSON();

    }

    @Override
    public void setStatus(String sStatus) {
        this.status.add(new StatusMessage(sStatus, this.getProgressStatus()));
    }

    @Override
    public void setStatus(StatusMessage pMessage) {
        this.status.add(pMessage);
    }

    @Override
    public Status getStatus() {
        Status rStatus = new Status(getUri(), getMessage(), getProgressStatus(), 0.0d, getPID(), this.create, this.start, this.finish);
        return rStatus;
    }

    @Override
    public Result getResult() {
        return null;
    }

    @Override
    public ProcessConst.PROGRESS_STATUS getProgressStatus() {
        return this.pStatus;
    }

    @Override
    public void setProgressStatus(ProcessConst.PROGRESS_STATUS pStatus) {
        this.pStatus = pStatus;
        switch(this.pStatus){
            case RUNNING:
                this.start = new Timestamp(System.currentTimeMillis());
                break;

            case FAILURE:
            case FINISH:
                this.finish = new Timestamp(System.currentTimeMillis());
                if(this.tManager!=null) {
                    this.tManager.finishTask(this, pStatus);
                }
                break;
        }
    }

    @Override
    public String getMessage() {

        String returnString = "";

        try{
            StatusMessage m = this.status.parallelStream().sorted().findFirst().get();
            returnString = m.getMessage();
        }
        catch (Exception e){
            this.pLogger.error(e.getMessage(), e);
        }

        return returnString;

    }

    @Override
    public String getUri() {
        return this.sUri;
    }

    @Override
    public double getAbsoluteProgressStatus() {
        return this.absolute;
    }

    @Override
    public void setAbsoluteProgressStatus(double dAbsolute) {
        this.absolute = dAbsolute;
    }

    @Override
    public Metadata getMetadata() {
        return this.pMetadata;
    }

    @Override
    public ReturnModel getReturnModel() throws JSONException {
        ReturnModel rm = new ReturnModel(this.getStatus(), this.getMetadata(), this.getResult());
        return rm;
    }

    @Override
    public int compareTo(HucomputeProcess o) {
        return this.getPID().compareTo(o.getPID());
    }

    @Override
    public int hashCode() {
        return this.getPID().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode()==obj.hashCode();
    }

    @Override
    public void run() {

    }

}
