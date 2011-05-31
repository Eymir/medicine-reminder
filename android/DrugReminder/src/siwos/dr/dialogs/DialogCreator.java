package siwos.dr.dialogs;

import java.text.DecimalFormat;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogCreator {
	private static DialogCreator instance = null;
	protected DialogCreator() {	}
	public static DialogCreator getInstance(){
		if (instance == null)
			instance = new DialogCreator();
		return instance;
	}
	
	public static final int REMIND_TYPE = 0;
	
	public Dialog createDialog(Context ctx, int type, String msg) {
		Dialog dialog = null;
		switch(type){
		case REMIND_TYPE:
			dialog = new RemindDialog(ctx, msg);
			break;
		}
		return dialog;
	}
	
	
	/*
	private Dialog createRemindDialog(Context ctx) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
		alert.create().getWindow().setSoftInputMode(
    			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	alert.setTitle(ctx.getString(R.string.dialog_alert_title));
    	
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO: confirm taking medicine in DB
			}
		});
    	
    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

    	final AlertDialog alertDialog = alert.create();
    	
    	alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				//TODO: co jak sie zmieni orientacja okna? trzeba zapisac jakas zmienna globalna
				//Toast.makeText(getApplicationContext(), "wywoluje onDismiss", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	return alertDialog;
	}
	*/
}
