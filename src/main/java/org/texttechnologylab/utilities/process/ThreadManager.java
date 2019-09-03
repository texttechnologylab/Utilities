package org.texttechnologylab.utilities.process;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.texttechnologylab.utilities.process.exception.ProgressNotExistException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by abrami on 08.09.16.
 */
public class ThreadManager implements Runnable {

    static int currThreads = 0;
    static int maxThreads = Runtime.getRuntime().availableProcessors();

    private Logger pLogger = null;

    boolean bRun = true;

    Thread pThread = null;

    public static Set<HucomputeProcess> threadSet = new HashSet<>(0);

    public ThreadManager(int maxThreads, Logger pLogger){
        this(pLogger);
        this.maxThreads = maxThreads;
    }

    public ThreadManager(Logger pLogger){
        this.pLogger = pLogger;
        this.pThread = new Thread(this);
        this.pThread.start();

/*
        Runnable cleanThread = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    threadSet.stream().filter(t -> {
                        return t.isDelivered();
                    }).forEach(ft -> {

                        if (ft.getFinish().getTime() + 10000 > System.currentTimeMillis()) {
                            System.out.println("Remove: " + ft.getPID());
                            threadSet.remove(ft);
                        }
                    });

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            }
        };

        new Thread(cleanThread).start();*/
    }

    public void close(){
        this.bRun = false;

        int count=0;
        while(this.pThread.isAlive()){
            try {
                wait(1000);

                if(count>3){
                    this.pThread.interrupt();
                }
                else{
                    count++;
                }

            } catch (InterruptedException e) {
                if(this.pLogger!=null){
                    this.pLogger.error(e.getMessage(), e);
                }
                else{
                    e.printStackTrace();
                }

            }
        }

    }

    public Logger getLogger(){
        return this.pLogger;
    }

    public void add(HucomputeProcess pProcess){
        threadSet.add(pProcess);
    }

    public HucomputeProcess get(String sPID) throws ProgressNotExistException {

        List<HucomputeProcess> tList = threadSet.parallelStream().filter(t->t.getPID().equalsIgnoreCase(sPID)).collect(Collectors.toList());

        if(tList.size()==1){
            return tList.get(0);
        }
        else{
            throw new ProgressNotExistException("Process "+sPID+" does not exist!");
        }

    }

    public Set<HucomputeProcess> getProcesses(){
        return threadSet;
    }

    private HucomputeProcess getNextProcess(){

        try {
            List<HucomputeProcess> iList = new ArrayList<>();

            if(threadSet!=null){

                if(threadSet.size()>0){
                    iList = threadSet.parallelStream().filter(t -> t.getProgressStatus().equals(ProcessConst.PROGRESS_STATUS.OPEN)).collect(Collectors.toList());
                }

                if (iList.size() > 0) {
                    return iList.get(0);
                }

            }
        }
        catch (Exception e){
            pLogger.error(e.getMessage(), e);
        }

        return null;

    }


    @Override
    public void run() {

        while(bRun) {
            try {

                synchronized (this) {

                    while (this.currThreads >= this.maxThreads) {
                        try {
                            wait();
                        } catch (InterruptedException e) {

                        }
                    }
                    HucomputeProcess process = getNextProcess();
                    if (process != null) {
                        if(!process.isRunning()){
                            this.currThreads++;
                            pLogger.debug("Start: "+process.getPID());
                            process.startProcess(this);
                        }

                    }
                }

            }catch(Exception ex){
                pLogger.error("Running Thread Manager: "+ex.getMessage());
            }

        }

        synchronized (this) {
            while (this.currThreads > 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    pLogger.debug("Wait-Threads: "+e.getMessage());
                }
            }
        }


    }

    public synchronized void failureTask(HucomputeProcess pProcess) {
        finishTask(pProcess, ProcessConst.PROGRESS_STATUS.FAILURE);
    }

    public synchronized void finishTask(HucomputeProcess pProcess) {
        finishTask(pProcess, ProcessConst.PROGRESS_STATUS.FINISH);
    }

    public synchronized void finishTask(HucomputeProcess pProcess, ProcessConst.PROGRESS_STATUS pStatus) {
        if(!pProcess.getProgressStatus().equals(pStatus)){
            pProcess.setProgressStatus(pStatus);
        }
        this.currThreads--;
        notifyAll();
    }

    public boolean isFinish(){
        return getFinishThreads()==threadSet.size();
    }

    public long countMaxThreads(){
        return this.maxThreads;
    }

    public long getFinishThreads(){
        long count = 0;
        try {
            count = threadSet.parallelStream().filter(t -> t.isFinish()).count();
        }
        catch (Exception e){
            pLogger.error("Finish-Threads: "+e.getMessage(), e);
        }
        return count;
    }

    public long getRunningThreads(){
        long count = 0;
        try {
            count = threadSet.parallelStream().filter(t -> t.isRunning()).count();
        }
        catch (Exception e){
            pLogger.error("Running-Threads: "+e.getMessage(), e);
        }
        return count;
    }

    public long getOpenThreads(){
        long count = 0;
        try {
            count = threadSet.parallelStream().filter(t -> t.getProgressStatus().equals(ProcessConst.PROGRESS_STATUS.OPEN)).count();
        }
        catch (Exception e){
            pLogger.error("Open-Threads: "+e.getMessage(), e);
        }
        return count;
    }

    public long getFailedThreads(){
        long count = 0;
        try {
            count = threadSet.parallelStream().filter(t -> t.getProgressStatus().equals(ProcessConst.PROGRESS_STATUS.FAILURE)).count();
        }
        catch (Exception e){
            pLogger.error("Failed-Threads: "+e.getMessage(), e);
        }
        return count;
    }

    public JSONObject getStatus() throws JSONException {

        JSONObject rObject = new JSONObject();

            JSONObject status = new JSONObject();
            status.put("maxThreads", this.countMaxThreads());
            status.put("finished", this.getFinishThreads());
            status.put("running", this.getRunningThreads());
            status.put("waiting", this.getOpenThreads());
            status.put("failed", this.getFailedThreads());
            status.put("total", this.getProcesses().size());

        rObject.put("status", status);

        JSONArray rArray = new JSONArray();

        this.getProcesses().stream().forEach(t->{
            try {
                rArray.put(t.getStatus().toJSON());
            } catch (JSONException e) {
                pLogger.error(e.getMessage(), e);
            }
        });

        rObject.put("processes", rArray);

        return rObject;

    }
}
