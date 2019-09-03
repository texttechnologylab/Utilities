package org.texttechnologylab.utilities.helper.utils;


import org.apache.log4j.Logger;

/**
 * Created by abrami on 14.03.16.
 */
public class Timer {

    Long timeStart = 0l;
    Long timeEnd = 0l;
    Long difference = 0l;
    String sMessage = "";
    Logger pLogger = null;

    public Timer(){
        timeStart = System.currentTimeMillis();
    }

    public Timer(String sMessage){
        this();
        this.sMessage = sMessage;
    }

    public Timer(String sMessage, Logger pLogger){
        this();
        this.sMessage = sMessage;
        this.pLogger = pLogger;
    }

    public void end(){
        timeEnd = System.currentTimeMillis();
        this.getResult();
    }

    public String getResult(){

        this.difference = this.timeEnd - this.timeStart;

        String rString = this.sMessage.length()>0 ? this.sMessage : "";
        if(pLogger!=null){
            pLogger.debug(rString+" -> "+((float)(difference/1000))+"s");
        }
        return rString;
    }

}
