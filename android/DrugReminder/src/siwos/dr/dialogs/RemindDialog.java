package siwos.dr.dialogs;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

public class RemindDialog extends AlertDialog {

	public RemindDialog(Context context, String msg) {
		super(context);
		Builder builder = new Builder(context);
		
		//final AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
		getWindow().setSoftInputMode(
    			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	setTitle(msg);
    	setButton(BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO: confirm taking medicine in DB
			}
		});
    	
    	setButton(BUTTON_NEGATIVE , "Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

    	
    	this.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				//TODO: co jak sie zmieni orientacja okna? trzeba zapisac jakas zmienna globalna
				//Toast.makeText(getApplicationContext(), "wywoluje onDismiss", Toast.LENGTH_SHORT).show();
			}
		});
    }
}
