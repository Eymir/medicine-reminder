package siwos.dr.activities;

import siwos.dr.R;
import siwos.dr.data.FrequenciesDbAdapter;
import siwos.dr.data.MedicamentsDbAdapter;
import siwos.dr.data.TreatmentsDbAdapter;
import siwos.dr.resources.ResourcesServe;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TreatmentsArchive extends ShowCurrentDrugs{
	
	private void addHeadline(TableLayout table) {
		TextView tv;
		TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.treatments_archive_header, null);
		
		tv = (TextView)tr.findViewById(R.id.treatment_archive_header_medicament_name);
		tv.setText("Medicine name");
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(18);
		
		tv = (TextView)tr.findViewById(R.id.treatment_archive_header_treatment_frequency);
		tv.setText("Frequency");
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(18);
		
		tv = (TextView)tr.findViewById(R.id.treatment_archive_header_start_date);
		tv.setText("To take");
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(18);
						
		tr.setBackgroundColor(Color.WHITE);
		
		table.addView(tr);
	}
	
	
	private void fillData() {
		String date;
	    TableRow tr; 
	    TextView tv;
	    Cursor freqCur;
	    int freqId;
	    String curStr;
	    Cursor medCur;
	    int medId;
	    
	    final TableLayout table = (TableLayout) findViewById(R.id.treatments_table);
	    final Cursor cursor = TreatmentsDbAdapter.getInstance(this).fetchAll();//fetchAllCurrent();
		startManagingCursor(cursor);
		cursor.moveToFirst();
		table.removeAllViews();
		addHeadline(table);
		while(cursor.isAfterLast() == false) {
			tr = (TableRow) getLayoutInflater().inflate(R.layout.treatments_archive_row, null);
			tr.setPadding(0, 10, 0, 0);
			
			//set id
			final int id = cursor.getInt(cursor.getColumnIndex(TreatmentsDbAdapter.KEY_ROWID));
			tr.setId(id);
			
			//set medicine name cell
			tv = (TextView)tr.findViewById(R.id.treatment_archive_row_medicament_name);
			medId = cursor.getInt(cursor.getColumnIndex(TreatmentsDbAdapter.KEY_MEDICAMENT_ID));
			medCur = MedicamentsDbAdapter.getInstance(this).fetchOne(medId);
			if(medCur.getCount() != 1) return;
			curStr = medCur.getString(medCur.getColumnIndex(MedicamentsDbAdapter.KEY_NAME));
			if (curStr.length() >= 15) {
				curStr = curStr.substring(0, 15);
				curStr += "...";
			}
			tv.setText(curStr);
			
			
			//set frequency cell
			tv = (TextView)tr.findViewById(R.id.treatment_archive_row_treatment_frequency);
			freqId = cursor.getInt(cursor.getColumnIndex(TreatmentsDbAdapter.KEY_FREQUENCY_ID));
			freqCur = FrequenciesDbAdapter.getInstance(this).fetchOne(freqId);
			if (freqCur.getCount() != 1) return;
			curStr = ResourcesServe.getFrequencyName(this, freqCur.getString(freqCur.getColumnIndex(FrequenciesDbAdapter.KEY_NAME)));
			//TODO: get string from resources
			if (curStr.length() >= 15) {
				curStr = curStr.substring(0, 15);
				curStr += "...";
			}
			tv.setText(curStr);
			
			//pills to take
			tv = (TextView)tr.findViewById(R.id.treatment_archive_row_start_date);
			date = cursor.getString(cursor.getColumnIndex(TreatmentsDbAdapter.KEY_START_DATE));
			tv.setText(date);
						
			table.addView(tr);
			//registerForContextMenu(tr);
			
			cursor.moveToNext();
		}
	}	
}
