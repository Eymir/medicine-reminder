package siwos.dr.activities;

import siwos.dr.R;
import siwos.dr.data.FrequenciesDbAdapter;
import siwos.dr.data.MedicamentsDbAdapter;
import siwos.dr.data.TreatmentsDbAdapter;
import siwos.dr.resources.ResourcesServe;
import siwos.dr.services.AlarmScheduler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCurrentDrugs extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.treatments_table);
		fillData();
	}

	private void addHeadline(TableLayout table) {
		TextView tv;
		TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.treatments_header, null);
		
		tv = (TextView)tr.findViewById(R.id.treatment_header_medicament_name);
		tv.setText("Medicine name");
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(18);
		
		tv = (TextView)tr.findViewById(R.id.treatment_header_treatment_frequency);
		tv.setText("Frequency");
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(18);
		
		tv = (TextView)tr.findViewById(R.id.treatment_header_pills_to_take);
		tv.setText("To take");
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(18);
		
		tv = (TextView)tr.findViewById(R.id.treatment_header_active);
		tv.setText("Active");
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(18);
				
		tr.setBackgroundColor(Color.WHITE);
		
		table.addView(tr);
	}
	
	private void fillData() {
		final TableLayout table = (TableLayout) findViewById(R.id.treatments_table);
	    TableRow tr; 
	    TextView tv;
	    final Cursor cursor = TreatmentsDbAdapter.getInstance(this).fetchAll();//fetchAllCurrent();
		startManagingCursor(cursor);
		cursor.moveToFirst();
		table.removeAllViews();
		addHeadline(table);
		while(cursor.isAfterLast() == false) {
			tr = (TableRow) getLayoutInflater().inflate(R.layout.treatments_row, null);
			tr.setPadding(0, 10, 0, 0);
			
			//set id
			final int id = cursor.getInt(cursor.getColumnIndex(TreatmentsDbAdapter.KEY_ROWID));
			tr.setId(id);
			
			//set medicine name cell
			tv = (TextView)tr.findViewById(R.id.treatment_row_medicament_name);
			int medId = cursor.getInt(cursor.getColumnIndex(TreatmentsDbAdapter.KEY_MEDICAMENT_ID));
			Cursor medCur = MedicamentsDbAdapter.getInstance(this).fetchOne(medId);
			if(medCur.getCount() != 1) return;
			String curStr = medCur.getString(medCur.getColumnIndex(MedicamentsDbAdapter.KEY_NAME));
			if (curStr.length() >= 15) {
				curStr = curStr.substring(0, 15);
				curStr += "...";
			}
			tv.setText(curStr);
			
			
			//set frequency cell
			tv = (TextView)tr.findViewById(R.id.treatment_row_treatment_frequency);
			int freqId = cursor.getInt(cursor.getColumnIndex(TreatmentsDbAdapter.KEY_FREQUENCY_ID));
			Cursor freqCur = FrequenciesDbAdapter.getInstance(this).fetchOne(freqId);
			if (freqCur.getCount() != 1) return;
			curStr = ResourcesServe.getFrequencyName(this, freqCur.getString(freqCur.getColumnIndex(FrequenciesDbAdapter.KEY_NAME)));
			//TODO: get string from resources
			if (curStr.length() >= 15) {
				curStr = curStr.substring(0, 15);
				curStr += "...";
			}
			tv.setText(curStr);
			
			//pills to take
			tv = (TextView)tr.findViewById(R.id.treatment_row_pills_to_take);
			int all = cursor.getInt(cursor.getColumnIndex(TreatmentsDbAdapter.KEY_PILLS));
			int taken = cursor.getInt(cursor.getColumnIndex(TreatmentsDbAdapter.KEY_PILLS_TAKEN));
			tv.setText(Integer.toString(all - taken));
			
			//set active
			CheckBox cb = (CheckBox) tr.findViewById(R.id.treatment_row_active);
			if (cursor.getInt(cursor.getColumnIndex(TreatmentsDbAdapter.KEY_ACTIVE)) == 0)
				cb.setChecked(false);
			else
				cb.setChecked(true);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked == false) {
						confirmDisactivation(id);
					} else {
						TreatmentsDbAdapter.getInstance(getApplicationContext()).updateActive(true, id);
						AlarmScheduler.scheduleTreatment(getApplicationContext(), id);
						TreatmentsDbAdapter.getInstance(getApplicationContext()).updateScheduled(true, id);
//						Toast.makeText(getApplicationContext(), "wybrano: " + id, Toast.LENGTH_SHORT).show();
					}
				}
			});
						
			table.addView(tr);
			//registerForContextMenu(tr);
			
			cursor.moveToNext();
		}
	}
	
	private void confirmDisactivation(final int id) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setMessage("Do you really want to deactivate treatment?/n You haven't taken all pills yet");
    	
    	alert.setPositiveButton(getString(R.string.deactivate), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				TreatmentsDbAdapter.getInstance(getApplicationContext()).updateActive(false, id);
				AlarmScheduler.unscheduleTreatment(getApplicationContext(), id);
				TreatmentsDbAdapter.getInstance(getApplicationContext()).updateScheduled(false, id);
				fillData();
			}
		});
    	
    	alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	    	
    	alert.show();

	}
}
