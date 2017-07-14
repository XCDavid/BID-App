package com.teknei.bid.ws;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ServerConnection {
    /**
     * HTTP Header User-Agent
     */
    public static final String HEADER_USER_AGENT = "User-Agent";
    /**
     * Android User Agent
     */
    public static final String ANDROID_USER_AGENT = "http.agent";
    public static final String APPLICATION_JSON = "application/json";
    public static final String HEADER_TOKEN_CODE = "Authorization";
    public static final String HEADER_TOKEN_AUX_VALUE = "Token ";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";

    private String tokenID = "";
    public static final int TIME_OUT = 5000;
    Integer statusResponse = null;


    public Object[] connection(Context context, String stringJSON, String serverMethod, String token, String method) {
        // Creamos la peticion http
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, TIME_OUT);
        HttpClient clienteHTTP = new DefaultHttpClient(httpParameters);

        HttpResponse httpResponse = null;

        HttpPost httpPOST = null;
        HttpGet httpGet = null;
        HttpDelete httpDelete = null;
        this.tokenID = token;
//		this.tokenID = HerApplication.HEADER_APPLICATION_KEY;
        //Selecciona que tipo de metodo crear
        switch (method) {
            case ServerConnection.METHOD_POST:
                httpPOST = new HttpPost(serverMethod);
                break;
            case ServerConnection.METHOD_GET:
                httpGet = new HttpGet(serverMethod);
                break;
            case ServerConnection.METHOD_DELETE:
                httpDelete =  new HttpDelete(serverMethod);
                break;
        }
//        if (method.equals(ServerConnection.METHOD_POST)) {
//            httpPOST = new HttpPost(serverMethod);
//        } else if (method.equals(ServerConnection.METHOD_GET)) {
//            httpGet = new HttpGet(serverMethod);
//        }
        //Creamos la entidad de datos que enviaremos si existe el JSONOBJECT
        String sendJSON = stringJSON;
        if (sendJSON != null) {
            sendJSON = stringJSON.replaceAll("\\\\", "");
        }
        StringEntity entityData = null;
        try {
            if (sendJSON != null) {
                entityData = new StringEntity(sendJSON);
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        //Si hay entidad de datos se agrega al Post en caso de que el metodo POST tambien exista
        if (entityData != null) {
            entityData.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            if (httpPOST != null) {
                httpPOST.setEntity(entityData);
            }
        }

        // Ejecuta la peticion HTTP POST / GET / DELETE al servidor
        try {
            if (httpPOST != null) {
                //User aget add
//                httpPOST.setHeader(HEADER_USER_AGENT, System.getProperty(ANDROID_USER_AGENT));

              /****///Token add       //Authorization      //Token" "token_del_login *****************************
                httpPOST.setHeader(HEADER_TOKEN_CODE, HEADER_TOKEN_AUX_VALUE+tokenID);
                //Normal headers add
                httpPOST.setHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                httpResponse = clienteHTTP.execute(httpPOST);
            } else if (httpGet != null) {
                //User aget add
//                httpGet.setHeader(HEADER_USER_AGENT, System.getProperty(ANDROID_USER_AGENT));
                //Token add
                httpGet.setHeader(HEADER_TOKEN_CODE, HEADER_TOKEN_AUX_VALUE+tokenID);
                //Normal headers add
                httpGet.setHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                httpResponse = clienteHTTP.execute(httpGet);
            } else if (httpDelete != null) {
                httpDelete.setHeader(HEADER_TOKEN_CODE, HEADER_TOKEN_AUX_VALUE+tokenID);
                //Normal headers add
                httpDelete.setHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                httpResponse = clienteHTTP.execute(httpDelete);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }

        String responseJSONString = null;
        if (httpResponse != null) {
            statusResponse = httpResponse.getStatusLine().getStatusCode();
            HttpEntity entity = httpResponse.getEntity();
            try {
                responseJSONString = EntityUtils.toString(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("response -> ", ":" + responseJSONString);
        }

        JSONObject respuestaJSONObject = null;
        if (responseJSONString != null) {
            try {
                respuestaJSONObject = new JSONObject(responseJSONString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new Object[]{respuestaJSONObject, statusResponse, tokenID};
    }
}