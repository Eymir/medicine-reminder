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

public class RemindTaking extends Activity{
	
	TextView label;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remind_taking);
		
		Bundle extras = getIntent().getExtras();
		final int id = extras.getInt(TreatmentsDbAdapter.KEY_ROWID);
		
		//int counter = extras.getInt("counter");
		//Toast.makeText(getApplicationContext(), "w ConfirmTaking id: " + id + " couter: " + counter, Toast.LENGTH_SHORT).show();
		
		Cursor cur = TreatmentsDbAdapter.getInstance(this).fetchOne(id);
		if (cur.getCount() != 1) return;
		
		int medicamentId = cur.getInt(cur.getColumnIndex(TreatmentsDbAdapter.KEY_MEDICAMENT_ID));
		Cursor medicamentCur = MedicamentsDbAdapter.getInstance(this).fetchOne(medicamentId);
		String medicamentName = medicamentCur.getString(medicamentCur.getColumnIndex(MedicamentsDbAdapter.KEY_NAME));
		 
		label = (TextView) findViewById(R.id.remind_taking_label);
		label.setText(label.getText() + " " + medicamentName);
	}
}
