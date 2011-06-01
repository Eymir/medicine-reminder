package siwos.dr.activities;

import siwos.dr.R;
import siwos.dr.data.FrequenciesDbAdapter;
import siwos.dr.data.MedicamentsDbAdapter;
import siwos.dr.data.TreatmentsDbAdapter;
import siwos.dr.services.AlarmScheduler;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmTaking extends Activity{
	
	TextView confirmLabel;
	TextView nextDozeLabel;
	Button confirmBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_taking);
		
		Bundle extras = getIntent().getExtras();
		int id = extras.getInt(TreatmentsDbAdapter.KEY_ROWID);
		int counter = extras.getInt("counter");
		
//		Toast.makeText(getApplicationContext(), "w ConfirmTaking id: " + id + " couter: " + counter, Toast.LENGTH_SHORT).show();
		
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
		TreatmentsDbAdapter.getInstance(getApplicationContext()).increaseTakenPills(id);
		confirmBtn = (Button) findViewById(R.id.confirm_taking_btn);
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TreatmentsDbAdapter.getInstance(getApplicationContext()).increaseTakenPills(id);
					//AlarmScheduler.scheduleTreatment(getApplicationContext(), id);
				finish();
			}
		});
	}
	
}
