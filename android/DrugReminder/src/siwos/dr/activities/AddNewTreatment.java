package siwos.dr.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
import siwos.dr.*;
import siwos.dr.data.FrequenciesDbAdapter;
import siwos.dr.data.MedicamentsDbAdapter;
import siwos.dr.data.TreatmentsDbAdapter;
import siwos.dr.resources.ResourcesServe;

public class AddNewTreatment extends Activity{
	
	static final int START_DATE_DIALOG_ID = 0;
	static final int START_TIME_DIALOG_ID = 1;
	static final String START_YEAR = "start_year";
	static final String START_MOTH = "start_month";
	static final String START_DAY = "start_day";
	static final String START_HOUR = "start_hour";
	static final String START_MINUTE = "start_minute";
	
	private int _startYear;
	private int _startMonth;
	private int _startDay;
	private int _startHour;
	private int _startMinute;
	private long _frequencyId;
	
	Button dateBtn;
	Button timeBtn;
	Spinner frequencySpinner;
	Button submitBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_treatment);
		
		// get the current date
		if (savedInstanceState == null) {
	        final Calendar c = Calendar.getInstance();
	        _startYear = c.get(Calendar.YEAR);
	        _startMonth = c.get(Calendar.MONTH);
	        _startDay = c.get(Calendar.DAY_OF_MONTH);
	        _startHour = c.get(Calendar.HOUR_OF_DAY);
	    	_startMinute = c.get(Calendar.MINUTE);
	    	addMinutes(10);
		}
		else {
			_startYear = savedInstanceState.getInt(START_YEAR);
			_startMonth = savedInstanceState.getInt(START_MOTH);
			_startDay = savedInstanceState.getInt(START_DAY);
			_startHour = savedInstanceState.getInt(START_HOUR);
	    	_startMinute = savedInstanceState.getInt(START_MINUTE);
		}
		
		dateBtn = (Button) findViewById(R.id.new_treatment_change_start_date_btn);
		timeBtn = (Button) findViewById(R.id.new_treatment_change_start_time_btn);
		
		dateBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(START_DATE_DIALOG_ID);
			}
		});
		
		timeBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(START_TIME_DIALOG_ID);
			}
		});
		
		updateStartDateDisplay();
		updateStartTimeDisplay();
		
		String[] from = new String[]{FrequenciesDbAdapter.KEY_NAME};
		int[] to = new int[]{android.R.id.text1};
		frequencySpinner = (Spinner) findViewById(R.id.new_treatment_frequency_spinner);
		SimpleAdapter sa = new SimpleAdapter(this, getFrequenciesList(), android.R.layout.simple_spinner_item, from, to);
		sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SimpleAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {

	        public boolean setViewValue(View view, Object data,
	                String textRepresentation) {
	            // We configured the SimpleAdapter to create TextViews (see
	            // the 'to' array), so this cast should be safe:
	            TextView textView = (TextView) view;
	            textView.setText(textRepresentation);
	            return true;
	        }
	    };
	    sa.setViewBinder(viewBinder);
		frequencySpinner.setAdapter(sa);
		frequencySpinner.setOnItemSelectedListener(new OnPlaceItemSelectedListener());
		
		submitBtn = (Button) findViewById(R.id.new_treatment_submit);
		submitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int freq = (int)_frequencyId;
				EditText medicineText = (EditText)findViewById(R.id.new_treatment_medicament);
				EditText pillsText = (EditText)findViewById(R.id.new_treatment_pills_number);
				Date date = new Date(_startYear - 1900, _startMonth, _startDay, _startHour, _startMinute);
				Date curDate = new Date();
				if (curDate.getTime() > date.getTime()){
					Toast.makeText(getApplicationContext(), getString(R.string.add_new_treatment_date_in_past), Toast.LENGTH_SHORT).show();
					return;
				}
					
				
				long medicineId;
				Cursor medicamentCursor = MedicamentsDbAdapter.
					getInstance(getApplicationContext()).
						fetchOne(medicineText.getText().toString());
				if (medicamentCursor.getCount() == 0)
					medicineId = MedicamentsDbAdapter.
						getInstance(getApplicationContext()).
							insert(medicineText.getText().toString());
				else
					medicineId = 
						medicamentCursor.
							getInt(medicamentCursor.
									getColumnIndex(MedicamentsDbAdapter.KEY_ROWID));
				
				int pills = Integer.parseInt(pillsText.getText().toString().trim());
				if (TreatmentsDbAdapter.
					getInstance(getApplicationContext()).
						insert(date, pills, 0, freq, (int)medicineId, true, false) != 0)
					Toast.makeText(getApplicationContext(), getString(R.string.main_treatment_added), Toast.LENGTH_SHORT).show();
				Cursor c = TreatmentsDbAdapter.getInstance(getApplicationContext()).fetchAll();
				c.moveToLast();
				Intent data = new Intent();
				int id = c.getInt(c.getColumnIndex(TreatmentsDbAdapter.KEY_ROWID));
				id = TreatmentsDbAdapter.getInstance(getApplicationContext()).getHighestId(); 
				data.putExtra(TreatmentsDbAdapter.KEY_ROWID, id);
				if (getParent() == null) {
				    setResult(Activity.RESULT_OK, data);
				} else {
				    getParent().setResult(Activity.RESULT_OK, data);
				}
				finish();
			}
		});
	}

	private List<Map<String, String>> getFrequenciesList() {
		List<Map<String, String>> values = new ArrayList<Map<String, String>>();
		Cursor c = FrequenciesDbAdapter.getInstance(this).fetchAll();
	    int length = c.getCount();
	    c.moveToFirst();
	    for (int i = 0; i < length; i++) {
	      Map<String, String> v = new HashMap<String, String>();
	      v.put(FrequenciesDbAdapter.KEY_NAME,
	    		  ResourcesServe.getFrequencyName(this, c.getString(c.getColumnIndex(FrequenciesDbAdapter.KEY_NAME))).trim());
	      v.put(FrequenciesDbAdapter.KEY_ROWID,
	    		  String.valueOf(c.getInt(c.getColumnIndex(FrequenciesDbAdapter.KEY_ROWID))));
	      values.add(v);
	      c.moveToNext();
	    }
	    return values;
	}
		
	private class OnPlaceItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	
	    	Map<String, String> map = (Map<String, String>)frequencySpinner.getItemAtPosition(pos);
	    	Toast.makeText(parent.getContext(), "The selected is " + map.get(FrequenciesDbAdapter.KEY_ROWID), Toast.LENGTH_LONG).show();
	    	_frequencyId = Integer.parseInt(map.get(FrequenciesDbAdapter.KEY_ROWID));
	     
	    }

	    public void onNothingSelected(AdapterView parent) {
	      // Do nothing.
	    }
	}
	
	private void addMinutes(int offset) {
		Date d = new Date(_startYear - 1900, _startMonth, _startDay, _startHour, _startMinute);
		Date d2 = new Date(d.getTime() + offset*60*1000);
		
		_startDay = d2.getDate();
		_startHour = d2.getHours();
		_startMinute = d2.getMinutes();
		_startMonth = d2.getMonth();
		_startYear = d2.getYear() + 1900;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case START_DATE_DIALOG_ID:
	        return new DatePickerDialog(this,
	                    startDateSetListener,
	                    _startYear, _startMonth, _startDay);
	        
		case START_TIME_DIALOG_ID:
	        return new TimePickerDialog(this, 
	        		startTimeSetListener, 
	        		_startHour, _startMinute, true); 
	    }
	    return null;
	}
	
	
	private DatePickerDialog.OnDateSetListener startDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
                _startYear = year;
                _startMonth = monthOfYear;
                _startDay = dayOfMonth;
                updateStartDateDisplay();
            }
        };
	
    private TimePickerDialog.OnTimeSetListener startTimeSetListener =
    	new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				_startHour = hourOfDay;
				_startMinute = minute;
				updateStartTimeDisplay();
			}
		};
        
    public void updateStartDateDisplay() {
    	StringBuilder sb = new StringBuilder();
        // Month is 0 based so add 1
    	if (_startDay<10) sb.append("0");
        sb.append(_startDay).append("-");
        if (_startMonth+1<10) sb.append("0");
        sb.append(_startMonth + 1).append("-")
        .append(_startYear).append(" ");
    	dateBtn.setText(sb);
    }
    
    public void updateStartTimeDisplay() {
    	StringBuilder sb = new StringBuilder();
        // Month is 0 based so add 1
    	if (_startHour<10) sb.append("0");
        sb.append(_startHour).append(":");
        if (_startMinute<10) sb.append("0");
        sb.append(_startMinute);
    	timeBtn.setText(sb);
    }
        
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt(START_YEAR, _startYear);
		savedInstanceState.putInt(START_MOTH, _startMonth);
		savedInstanceState.putInt(START_DAY, _startDay);
		savedInstanceState.putInt(START_HOUR, _startHour);
		savedInstanceState.putInt(START_MINUTE, _startMinute);
		
		super.onSaveInstanceState(savedInstanceState);
	}
}
