package com.mobbeel.mobbscan.simple.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mobbeel.mobbscan.simple.R;

public class AlertDialog extends Dialog implements View.OnClickListener {
		Button okButton;
		TextView txvTitle;
		TextView txvMessage;
		Activity activityOrigin;
		
		String titleIn;
		String menssageIn;
		String actionIn;
		
		public AlertDialog(Activity context, String title, String message, String action) {
			super(context);
			
			activityOrigin = context;
			/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			/** Design the dialog in main.xml file */
			setContentView(R.layout.alert_dialog);
			
			this.titleIn =title;
			this.menssageIn =message;
			this.actionIn =action;
			
			txvTitle = (TextView)findViewById(R.id.alert_title);
			txvMessage = (TextView)findViewById(R.id.alert_message);
			okButton = (Button) findViewById(R.id.ok_buttom);
			okButton.setOnClickListener(this);
			
			txvTitle.setText(titleIn);
			txvMessage.setText(menssageIn);
		}

		@Override
		public void onClick(View v) {
			if (v == okButton) {
				dismiss();
				if(actionIn.equals("close"))
					activityOrigin.finish();
			}			
		}
}
