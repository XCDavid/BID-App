package com.morpho.common.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.morpho.common.view.custom.AlertDialogTool;
import com.morpho.common.view.custom.DialogUses;
import com.morpho.mrz.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by alfredohernandez on 17/08/17.
 * actinver
 */

public class BaseMorphoActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFont();
        loadSharedPreferences();
    }

    private String fontName = "Raleway-Regular.ttf";
    protected SharedPreferences sharedPreferences;

    /// Alerts
    public void showAlert(final Context context, final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialogTool.showAlertDialog(title,
                        message,
                        getString(R.string.accept), null, null, context, new DialogUses() {
                            @Override
                            public void cancelButtonAction() {
                            }

                            @Override
                            public void acceptButtonAction() {
                            }
                        });
            }
        });

    }

    public void showAlert(final Context context, final String title, final String message, final TextView input) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialogTool.showAlertDialog(title,
                        message,
                        getString(R.string.accept), null, input, context, new DialogUses() {
                            @Override
                            public void cancelButtonAction() {
                            }

                            @Override
                            public void acceptButtonAction() {
                            }
                        });
            }
        });

    }

    public void showAlert(final Context context, final String title, final String message, final DialogUses uses) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialogTool.showAlertDialog(title,
                        message,
                        getString(R.string.accept), null, null, context, uses);
            }
        });

    }

    public void showAlert(final Context context, final String title, final String message, final String positve, final String negative,
                          final DialogUses uses) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialogTool.showAlertDialog(title,
                        message,
                        positve, negative, null, context, uses);
            }
        });

    }


    /**
     * Display a simple toast with LONG duration
     * @param context The context
     * @param message Your message
     */
    protected void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // Font Loading...

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void loadFont(){
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/"+ fontName)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public void setOrientationByWindowSize(){
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            //Toast.makeText(this, "Large screen",Toast.LENGTH_LONG).show();
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            //Toast.makeText(this, "Normal sized screen" , Toast.LENGTH_LONG).show();
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            //Toast.makeText(this, "Small sized screen" , Toast.LENGTH_LONG).show();
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            //Toast.makeText(this, "Screen size is neither large, normal or small" , Toast.LENGTH_LONG).show();
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    protected void loadSharedPreferences(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }
}
