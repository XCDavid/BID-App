package com.mobbeel.mobbscan.simple.ws;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
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
    public static final String HEADER_TOKEN_CODE = "TOKEN";

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
	    this.tokenID = token;
//		this.tokenID = HerApplication.HEADER_APPLICATION_KEY;
        if (method.equals("POST")) {
            httpPOST = new HttpPost(serverMethod);
        } else if (method.equals("GET")) {
            httpGet = new HttpGet(serverMethod);
        }
        //Creamos la entidad de datos que enviaremos
        String sendJSON = stringJSON;
        if(sendJSON != null) {
            sendJSON = stringJSON.replaceAll("\\\\", "");
        }
        StringEntity entityData = null;
        try {
            if(sendJSON != null) {
                entityData = new StringEntity(sendJSON);
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        if (entityData != null) {
            entityData.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            if (httpPOST != null) {
                httpPOST.setEntity(entityData);
            }
        }

        // Ejecuta la peticion HTTP POST / GET al servidor
        try {
            if (httpPOST != null) {
                //User aget add
//                httpPOST.setHeader(HEADER_USER_AGENT, System.getProperty(ANDROID_USER_AGENT));
                //Token add
                httpPOST.setHeader(HEADER_TOKEN_CODE, tokenID);
                //Normal headers add
                httpPOST.setHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                httpResponse = clienteHTTP.execute(httpPOST);
            } else if (httpGet != null) {
                //User aget add
//                httpGet.setHeader(HEADER_USER_AGENT, System.getProperty(ANDROID_USER_AGENT));
                //Token add
                httpGet.setHeader(HEADER_TOKEN_CODE, tokenID);
                //Normal headers add
                httpGet.setHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                httpResponse = clienteHTTP.execute(httpGet);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }

        String responseJSONString = null;
        if (httpResponse != null) {
//       		 Header[] encabezadoall = respuesta.getAllHeaders();
//       		 for(int i=0; i<encabezadoall.length; i++){
//       			 String val = encabezadoall[i].getValue();
////       			 Log.d("nombre header", encabezadoall[i].getName());
////       			 Log.e("contenedor encabezado", "="+val);
//       			 if((encabezadoall[i].getName()).equals("Set-Cookie")){
//       				int index = val.indexOf("JSESSIONID=");
//					int endIndex = val.indexOf(";", index);
//					tokenID = val.substring(index + "JSESSIONID=".length(), endIndex);
//       			 }
//       		 }
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
