package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.activities.FakeINEActivity;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetFace extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = "GetFace";

    private String token;
    private String curp;
    private String idoperation;

    private Activity activityOrigin;

    private String errorMessage;
    private boolean responseOk = false;
    //private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private Bitmap bmp;
    private int option;

    public GetFace(Activity context, int option ,String curp, String id, String tokenOld) {
        this.activityOrigin = context;
        this.curp = curp;
        this.idoperation = id;
        this.token  = tokenOld;
        this.option = option;
    }

    @Override
    protected void onPreExecute() {
        ConnectivityManager check = (ConnectivityManager) activityOrigin.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = check.getAllNetworkInfo();
        for (int i = 0; i < info.length; i++) {
            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                hasConecction = true;
            }
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        if (hasConecction) {
            Log.i("Wait", "timer after DO: " + System.currentTimeMillis());
//            while (System.currentTimeMillis() < endTime) {
//                //espera hasta que pasen los 2 segundos en caso de que halla terminado muy rapido el hilo
//            }
            Log.i("Wait", "timer finish : " + System.currentTimeMillis());

            String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,
                    SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));

            Log.v("token             :", "token        :" + token);
            Log.v("END POINT         :", "endpoint     :" + endPoint);
            Log.v("CURP              :", "curp         :" + curp);

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            Call<ResponseBody> call = api.enrollmentPicturesSearchCustomerImage(token, option+"", curp, idoperation);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //progressDialog.dismiss();

                    Log.d(CLASS_NAME, response.code() + " ");

                    responseStatus = response.code();

                    if (responseStatus >= 200 && responseStatus < 300) {

                        bmp = BitmapFactory.decodeStream(response.body().byteStream());

                        if (bmp != null){

                            switch (option) {

                                case 1:
                                    ((FakeINEActivity) activityOrigin).printINEFront(bmp);
                                    break;

                                case 2:
                                    ((FakeINEActivity) activityOrigin).printINEBack(bmp);
                                    break;

                                case 3:
                                    ((FakeINEActivity) activityOrigin).printFace(bmp);
                                    break;
                            }
                        }

                    } else {
                        if (responseStatus >= 300 && responseStatus < 400) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                        } else if (responseStatus >= 400 && responseStatus < 500) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);

                        } else if (responseStatus >= 500 && responseStatus < 600) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_500);

                        }

                        /*
                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();
                        */
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //progressDialog.dismiss();

                    t.printStackTrace();

                    /*
                    AlertDialog dialogoAlert;
                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), "Error al conectarse con el servidor", ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();
                    */
                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) { }
}
