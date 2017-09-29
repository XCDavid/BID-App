package com.teknei.bid1.ws;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ServerConnectionUploadFileBytes {
    public static final String APPLICATION_JSON = "application/json";
    public static final String HEADER_TOKEN_CODE = "Authorization";
    public static final String HEADER_TOKEN_AUX_VALUE = "Token ";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";

    private String tokenID = "";
    public static final int TIME_OUT = 10000;
    Integer statusResponse = null;

    public Object[] connection(Context context, String serverMethod, String token, String method, List<byte[]> files) {
        // Creamos la peticion http
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, TIME_OUT);
        HttpClient clienteHTTP = new DefaultHttpClient(httpParameters);

        HttpResponse httpResponse = null;

        HttpPost httpPOST = null;
        HttpGet httpGet = null;
        HttpDelete httpDelete = null;
        this.tokenID = token;
        //Selecciona que tipo de metodo crear
        switch (method) {
            case ServerConnectionUploadFileBytes.METHOD_POST:
                httpPOST = new HttpPost(serverMethod);
                break;
            case ServerConnectionUploadFileBytes.METHOD_GET:
                httpGet = new HttpGet(serverMethod);
                break;
            case ServerConnectionUploadFileBytes.METHOD_DELETE:
                httpDelete = new HttpDelete(serverMethod);
                break;
        }

        //File attaching
        // creates a unique boundary based on time stamp
        String boundary = "===" + System.currentTimeMillis() + "===";
        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
        multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntity.setBoundary(boundary);
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                byte [] auxF = files.get(i);
                multipartEntity.addBinaryBody("file", auxF, ContentType.create("application/pdf"), "contrato.pdf");
            }

        }
        if (httpPOST != null) {
            multipartEntity.setStrictMode();
            httpPOST.setEntity(multipartEntity.build());
        }

        // Ejecuta la peticion HTTP POST / GET / DELETE al servidor
        Log.i("request -> ", ":" + serverMethod);
        try {
            if (httpPOST != null) {
                //Normal headers add
                httpPOST.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                httpPOST.setHeader(HTTP.CONTENT_TYPE, "multipart/form-data; boundary=" + boundary);
                /****///Token add       //Authorization      //Token" "token_del_login *****************************
                httpPOST.addHeader(HEADER_TOKEN_CODE, HEADER_TOKEN_AUX_VALUE + tokenID);
                httpResponse = clienteHTTP.execute(httpPOST);
            } else if (httpGet != null) {
                //User aget add
                //Normal headers add
                httpGet.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                //Token add
//                httpGet.addHeader(HEADER_TOKEN_CODE, HEADER_TOKEN_AUX_VALUE + tokenID);
                httpResponse = clienteHTTP.execute(httpGet);
            } else if (httpDelete != null) {
                //Normal headers add
                httpDelete.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                httpDelete.addHeader(HEADER_TOKEN_CODE, HEADER_TOKEN_AUX_VALUE + tokenID);
                httpResponse = clienteHTTP.execute(httpDelete);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            Log.d("error Response", "Response: " + ee.getMessage());
        }

        String responseJSONString = null;
        if (httpResponse != null) {
            statusResponse = httpResponse.getStatusLine().getStatusCode();
            Log.i("Status response -> ", "estatus : " + statusResponse);
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
