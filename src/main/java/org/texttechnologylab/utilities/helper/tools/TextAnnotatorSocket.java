package org.texttechnologylab.utilities.helper.tools;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.texttechnologylab.utilities.helper.RESTUtils;
import org.texttechnologylab.utilities.helper.StringUtils;
import org.texttechnologylab.utilities.helper.TTWebSocket;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class TextAnnotatorSocket extends TTWebSocket {

    String sSession = "";
    String sUsername = "";
    String sUserUri = "";

    public TextAnnotatorSocket(String sSession) throws URISyntaxException {
        this(new URI(TextAnnotatorSocket.sDefaultURI), sSession);

    }

    public TextAnnotatorSocket(URI pURI, String sSession){
        super(pURI);
        this.sSession = sSession;
        this.connect();

    }

    public TextAnnotatorSocket(URI pURI, String sUsername, String sPassword) throws NoSuchAlgorithmException, MalformedURLException, JSONException {
        super(pURI);
        JSONObject login = login(sUsername, StringUtils.toMD5(sPassword));
        this.connect();

    }

    public TextAnnotatorSocket(String sUsername, String sPassword) throws URISyntaxException, NoSuchAlgorithmException, JSONException, MalformedURLException {
        this(new URI(TextAnnotatorSocket.sDefaultURI), sUsername, sPassword);
        this.connect();

    }

    public JSONObject login(String sUsername, String sPassword) throws NoSuchAlgorithmException, MalformedURLException, JSONException {
        return login(new URL("http://authority.hucompute.org/login"), sUsername, sPassword);
    }

    public JSONObject login(URL pURI, String sUsername, String sPassword) throws NoSuchAlgorithmException, JSONException {
        JSONObject rObject = new JSONObject();

        Map<String, Object> mapSet = new HashMap<>(0);
        mapSet.put("username", sUsername);
        mapSet.put("password", StringUtils.toMD5(sPassword));

        rObject = RESTUtils.getObjectFromRest(pURI.toString(), RESTUtils.METHODS.POST, mapSet);

        if(rObject.getBoolean("success")){
           this.sSession = rObject.getString("session");
           this.sUsername = rObject.getString("username");
           this.sUserUri= rObject.getString("uri");
        }

        return rObject;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        super.onOpen(serverHandshake);
        System.out.println("Open!");
        try {
            this.send(String.valueOf(new JSONObject().put("cmd", "session").put("data", new JSONObject().put("session", this.sSession))));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
