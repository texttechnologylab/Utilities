package org.texttechnologylab.utilities.process;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.texttechnologylab.utilities.process.model.*;

import java.sql.Timestamp;

/**
 * Created by abrami on 28.09.16.
 */
public interface HucomputeProcess extends Runnable, Comparable<HucomputeProcess> {

    String getPID();

    Logger getLogger();

    void startProcess();
    void startProcess(ThreadManager tManager);

    void setRunnable(Runnable r);

    void stopProcess();
    void pauseProcess();
    void resumeProcess();

    boolean isRunning();
    boolean isPaused();
    boolean isFinish();

    boolean isDelivered();

    boolean isFailed();

    Timestamp getCreate();
    Timestamp getStart();
    Timestamp getFinish();
    long getDuration();


    JSONObject toJSON() throws JSONException;

    void setStatus(String sStatus);

    void setStatus(StatusMessage pMessage);

    Status getStatus();
    Result getResult();

    ProcessConst.PROGRESS_STATUS getProgressStatus();
    void setProgressStatus(ProcessConst.PROGRESS_STATUS pStatus);

    String getMessage();
    String getUri();

    double getAbsoluteProgressStatus();
    void setAbsoluteProgressStatus(double dAbsolute);

    Metadata getMetadata();

    ReturnModel getReturnModel() throws JSONException;

}
