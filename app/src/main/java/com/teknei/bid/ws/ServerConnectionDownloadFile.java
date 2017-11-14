package com.teknei.bid.ws;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ServerConnectionDownloadFile {
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
    public static final String HEADER_BASIC_AUX_VALUE = "Basic ";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";

    private String tokenID = "";
    private String basicAutho = "";
    private String operationId = "";
    public static final int TIME_OUT = 10000;
    Integer statusResponse = null;


    public Object[] connection(Context context, String stringJSON, String serverMethod, String token, String method, File file, String autho,String operationID) {
        // Creamos la peticion http
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, TIME_OUT);
        HttpClient clienteHTTP = new DefaultHttpClient(httpParameters);

        HttpResponse httpResponse = null;

        HttpPost httpPOST = null;
        HttpGet httpGet = null;
        HttpDelete httpDelete = null;
        this.tokenID = token;
        this.basicAutho = autho;
        this.operationId = operationID;

        Log.v("method            ", "method:        " + method);
        Log.v("token             ", "token:         " + token);
        Log.v("http URL SEND     ", "http:          " + serverMethod);
        Log.v("json SEND NO File ", "json no file:  " + stringJSON);
        Log.v("Autho             ", "Autho:         " + autho);

//		this.tokenID = HerApplication.HEADER_APPLICATION_KEY;
        //Selecciona que tipo de metodo crear
        switch (method) {
            case ServerConnectionDownloadFile.METHOD_POST:
                httpPOST = new HttpPost(serverMethod);
                break;
            case ServerConnectionDownloadFile.METHOD_GET:
                httpGet = new HttpGet(serverMethod);
                break;
            case ServerConnectionDownloadFile.METHOD_DELETE:
                httpDelete = new HttpDelete(serverMethod);
                break;
        }

        //Creamos la entidad de datos que enviaremos si existe el JSONOBJECT
        String sendJSON = stringJSON;
        if (sendJSON != null) {
            sendJSON = stringJSON.replaceAll("\\\\", "");
//            int i = sendJSON.indexOf("{");
//            sendJSON = sendJSON.substring(i);
//            sendJSON = stringJSON.replaceAll("\"", "\\\\\"");
//            sendJSON = "\\\"" +sendJSON + "\\\"";
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
        if (entityData != null && file == null && !serverMethod.equals("http://192.168.1.200:28080/BIDServer/rest/v1/credential")) {
            entityData.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            if (httpPOST != null) {
                httpPOST.setEntity(entityData);
            }
        }

        //Image attaching
        // creates a unique boundary based on time stamp
        String boundary = "===" + System.currentTimeMillis() + "===";
        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
        multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntity.setBoundary(boundary);
        File fileOut = file;
        File fileAux = new File(Environment.getExternalStorageDirectory() + File.separator + "face_14.jpg");
        if (!fileAux.exists()) {
            try {
                fileAux.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileOut != null) {
//            multipartEntity.addBinaryBody("file", fileAux, ContentType.create("image/jpeg"), fileAux.getName());
            multipartEntity.addBinaryBody("json", fileOut, ContentType.APPLICATION_JSON, fileOut.getName());

//            ContentBody cbFile = new FileBody(file, "multipart/form-data");
//            multipartEntity.addPart("json", cbFile);
        }
        //Json string attaching
        if (sendJSON != null) {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(stringJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//                multipartEntity.addTextBody("json2", sendJSON, ContentType.TEXT_PLAIN);
            multipartEntity.setStrictMode();
            if (httpPOST != null && (serverMethod.equals("http://192.168.1.200:28080/BIDServer/rest/v1/credential") || serverMethod.equals("http://192.168.1.200:28080/BIDServer/rest/v1/credential"))) {
                httpPOST.setEntity(multipartEntity.build());
            }
        }
//        }
        // Ejecuta la peticion HTTP POST / GET / DELETE al servidor

        try {
            if (httpPOST != null) {
                //Normal headers add
                httpPOST.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                if (serverMethod.equals("http://192.168.1.200:28080/BIDServer/rest/v1/credential") || serverMethod.equals("http://192.168.1.200:28080/BIDServer/rest/v1/credential")) {
                    httpPOST.setHeader(HTTP.CONTENT_TYPE, "multipart/form-data; boundary=" + boundary);
                }
                /****///Token add       //Authorization      //Token" "token_del_login *****************************
                httpPOST.addHeader(HEADER_TOKEN_CODE, HEADER_TOKEN_AUX_VALUE + tokenID);
                if (basicAutho != null && !basicAutho.equals("")) {
                    //*///Basic Authorization add       //Authorization      //Basic" "code
                    httpPOST.addHeader(HEADER_TOKEN_CODE, HEADER_BASIC_AUX_VALUE + basicAutho);
                }
                httpResponse = clienteHTTP.execute(httpPOST);
            } else if (httpGet != null) {
                //Normal headers add
                httpGet.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                //Token add
                httpGet.addHeader(HEADER_TOKEN_CODE, HEADER_TOKEN_AUX_VALUE + tokenID);
                if (basicAutho != null && !basicAutho.equals("")) {
                    //*///Basic Authorization add       //Authorization      //Basic" "code
                    httpGet.setHeader(HEADER_TOKEN_CODE, HEADER_BASIC_AUX_VALUE + basicAutho);
                }
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

//        String responseJSONString = null;
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "contract_" + operationId + ".pdf");
        if (httpResponse != null) {
            statusResponse = httpResponse.getStatusLine().getStatusCode();
            Log.i("Status response -> ", "estatus : " + statusResponse);
            HttpEntity entity = httpResponse.getEntity();

            try {
                InputStream is = entity.getContent();
//                responseJSONString = EntityUtils.toString(entity);
                String typeS = "someFileName.pdf ";
                String directory = Environment.getExternalStorageDirectory() + File.separator;
//                directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/SomeFolderName/");
                // if no directory exists, create new directory
//                if (!directory.exists()) {
//                    directory.mkdir();
//                }
                if (f.exists()) {

                    f.delete();
//                    f = new File(Environment.getExternalStorageDirectory() + File.separator + "contract_" + operationId + ".pdf");
                } else {

                    FileOutputStream fi = new FileOutputStream(Environment.getExternalStorageDirectory()
                            + File.separator + "contract_" + operationId + ".pdf");

                    byte[] buf = new byte[8 * 1024];
                    int len = 0;
                    while ((len = is.read(buf)) > 0) {
                        fi.write(buf, 0, len);
                    }
                    fi.close();
                }

                is.close();
                System.out.println("Hurra!! : D");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONObject respuestaJSONObject = null;
//        if (responseJSONString != null) {
//            try {
//                respuestaJSONObject = new JSONObject(responseJSONString);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return new Object[]{f, statusResponse, tokenID};
    }
}
