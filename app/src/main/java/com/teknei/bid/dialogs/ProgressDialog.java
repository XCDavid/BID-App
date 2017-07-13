package com.teknei.bid.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.TextView;

import com.teknei.bid.R;

public class ProgressDialog extends Dialog {
		TextView txvMessage;
		Activity activityOrigin;

		String menssageIn;

		public ProgressDialog(Activity context, String message) {
			super(context);
			
			activityOrigin = context;
			/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			/** Design the dialog in main.xml file */
			setContentView(R.layout.progress_dialog);
			
			this.menssageIn =message;

			txvMessage = (TextView)findViewById(R.id.tv_message_progress_dialog);
			txvMessage.setText(menssageIn);
		}
}
