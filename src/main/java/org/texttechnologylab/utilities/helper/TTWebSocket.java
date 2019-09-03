package org.texttechnologylab.utilities.helper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class TTWebSocket extends WebSocketClient {

    public static String sDefaultURI = "ws://141.2.108.196:8080/uima";

    public TTWebSocket(URI pURI){
        super(pURI);
    }

    public void send(JSONObject pObject){
        this.send(pObject.toString());
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println(this.getURI()+" open");
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println(s);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }



}
