package siwos.dr.activities;

import siwos.dr.R;
import siwos.dr.data.FrequenciesDbAdapter;
import siwos.dr.data.MedicamentsDbAdapter;
import siwos.dr.data.TreatmentsDbAdapter;
import siwos.dr.services.AlarmScheduler;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmTaking extends Activity{

	public static final String RELOAD_DATA = "REALOAD DATA";
	
	TextView confirmLabel;
	TextView nextDozeLabel;
	Button confirmBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("ConifmTaking", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_taking);
		
		Bundle extras = getIntent().getExtras();
		final int id = extras.getInt(TreatmentsDbAdapter.KEY_ROWID);
		
		//int counter = extras.getInt("counter");
		//Toast.makeText(getApplicationContext(), "w ConfirmTaking id: " + id + " couter: " + counter, Toast.LENGTH_SHORT).show();
		
		Cursor cur = TreatmentsDbAdapter.getInstance(this).fetchOne(id);
		if (cur.getCount() != 1) return;
		
		int medicamentId = cur.getInt(cur.getColumnIndex(TreatmentsDbAdapter.KEY_MEDICAMENT_ID));
		Cursor medicamentCur = MedicamentsDbAdapter.getInstance(this).fetchOne(medicamentId);
		String medicamentName = medicamentCur.getString(medicamentCur.getColumnIndex(MedicamentsDbAdapter.KEY_NAME));
		
		int freqId = cur.getInt(cur.getColumnIndex(TreatmentsDbAdapter.KEY_FREQUENCY_ID));
		Cursor freqCur = FrequenciesDbAdapter.getInstance(this).fetchOne(freqId);
		int interval = freqCur.getInt(freqCur.getColumnIndex(FrequenciesDbAdapter.KEY_INTERVAL));
		 
		confirmLabel = (TextView) findViewById(R.id.confirm_taking_label);
		confirmLabel.setText(confirmLabel.getText() + " " + medicamentName);
		
		nextDozeLabel = (TextView) findViewById(R.id.confirm_taking_next_doze_label);
		nextDozeLabel.setText(nextDozeLabel.getText() + " " + interval + " " + getString(R.string.confirm_taking_hours));
		//TreatmentsDbAdapter.getInstance(getApplicationContext()).increaseTakenPills(id);
		confirmBtn = (Button) findViewById(R.id.confirm_taking_btn);
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TreatmentsDbAdapter.getInstance(getApplicationContext()).increaseTakenPills(id);
				//unschedule reminds
				AlarmScheduler.unscheduleReminds(getApplicationContext());
				AlarmScheduler.unscheduleTreatment(getApplicationContext(), id);
			    if (TreatmentsDbAdapter.getInstance(getApplicationContext()).takeAnotherPill(id))
			    	AlarmScheduler.scheduleTreatment(getApplicationContext(), id, AlarmScheduler.TYPE_CONFIRMATION);
			    else {
			    	TreatmentsDbAdapter.getInstance(getApplicationContext()).updateActive(false, id);
			    	TreatmentsDbAdapter.getInstance(getApplicationContext()).updateScheduled(false, id);
			    }
			 
			    //send broadcast to listeners who needs to reload its content
			    Intent intent = new Intent();
		        intent.setAction (RELOAD_DATA);
		        //intent.putExtra  ("data1", data1);
		        getApplicationContext().sendOrderedBroadcast (intent, null);

		        NotificationManager nm = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		        nm.cancel(id);
				finish();
			}
		});
		
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("ConifmTaking", "onPause");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d("ConifmTaking", "onResume");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("ConifmTaking", "onDestroy");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_MENU)) {
	        Log.d(this.getClass().getName(), "back button or menu pressed");
	        return true;
	    }
	    else 
	    	return super.onKeyDown(keyCode, event);
	}
}
