package siwos.dr.activities;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import siwos.dr.R;
import siwos.dr.data.FrequenciesDbAdapter;
import siwos.dr.data.TreatmentsDbAdapter;
import siwos.dr.data.UserDbAdapter;
import siwos.dr.services.AlarmReceiver;
import siwos.dr.services.AlarmScheduler;
import siwos.dr.services.PoolingService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends ListActivity {
	
	String[] mMenuText; 
	String[] mMenuSummary;
	private int MENU_LENGTH = 5;
	
	private static final int ADD_TREATMENT = 0;
	
	private static final int MENU_ADD_TREATMENT = 0;
	private static final int MENU_DRUGS_YOU_TAKE = 1;
	private static final int MENU_ABOUT_DRUGS = 2;
	//private static final int MENU_HISTORY = 3;
	private static final int MENU_FETCH_WEB_DATA = 3;
	private static final int MENU_USERID = 4;
	
	private static final int DIALOG_USERID_ALERT_ID = 0;
	protected static final String DIALOG_TEXT_CONTENT = "content";
	String _dialogContent = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(savedInstanceState != null)
        	_dialogContent = savedInstanceState.getString(DIALOG_TEXT_CONTENT);
        
		mMenuText = new String[MENU_LENGTH];
		mMenuSummary = new String[MENU_LENGTH];
        
		mMenuText[MENU_ADD_TREATMENT] = getString(R.string.main_add_treatment);
		mMenuSummary[MENU_ADD_TREATMENT] = getString(R.string.main_add_treatment_desc);
		             
		mMenuText[MENU_DRUGS_YOU_TAKE] = getString(R.string.main_drugs_you_take); 
		mMenuSummary[MENU_DRUGS_YOU_TAKE] = getString(R.string.main_drugs_you_take_desc);
		
		mMenuText[MENU_ABOUT_DRUGS] = getString(R.string.main_about_drugs); 
		mMenuSummary[MENU_ABOUT_DRUGS] = getString(R.string.main_about_drugs_desc);
		/*
		mMenuText[MENU_HISTORY] = getString(R.string.main_history); 
		mMenuSummary[MENU_HISTORY] = getString(R.string.main_history_desc);
		*/
		mMenuText[MENU_FETCH_WEB_DATA] = getString(R.string.main_fetch_web_data); 
		mMenuSummary[MENU_FETCH_WEB_DATA] = getString(R.string.main_fetch_web_data_desc);
		
		mMenuText[MENU_USERID] = getString(R.string.main_userid);
		mMenuSummary[MENU_USERID] = getString(R.string.main_userid_desc);
		
        setListAdapter(new SimpleAdapter(this, getListValues(), android.R.layout.simple_list_item_2,
		        new String[] { "name", "desc" }, new int[] { android.R.id.text1, android.R.id.text2 }));
    }
    
    private List<Map<String, String>> getListValues() {
		List<Map<String, String>> values = new ArrayList<Map<String, String>>();
	    int length = mMenuText.length;
	    for (int i = 0; i < length; i++) {
	      Map<String, String> v = new HashMap<String, String>();
	      v.put("name", mMenuText[i]);
	      v.put("desc", mMenuSummary[i]);
	      values.add(v);
	    }
	    return values;
	}
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//Toast.makeText(this, "w listenerze", Toast.LENGTH_SHORT).show();
		switch(position) {
		case MENU_ADD_TREATMENT:
			showAddTreatment();
			break;
		case MENU_DRUGS_YOU_TAKE:
			showCurrentDrugs();
			break;
		case MENU_ABOUT_DRUGS:
			showDrugsInfo();
			break;
			/*
		case MENU_HISTORY:
			showHistory();
			break;
			*/
		case MENU_FETCH_WEB_DATA:
			fetch_data();
			break;
		case MENU_USERID:
			this.removeDialog(DIALOG_USERID_ALERT_ID);
			this.showDialog(DIALOG_USERID_ALERT_ID);
			break;
		}
	}
    
    protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
	    case DIALOG_USERID_ALERT_ID:
	        dialog = createInputUseridAlert();
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
    
    private AlertDialog createInputUseridAlert() {
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	//alert.setMessage("Do you really want to deactivate treatment?/n You haven't taken all pills yet");
    	final EditText idText = new EditText(this);
    	if (_dialogContent != null)
    		idText.setText(_dialogContent);
    	idText.setId(5);
    	alert.setView(idText);    	
    	
    	alert.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UserDbAdapter.getInstance(getApplicationContext()).setId(idText.getText().toString().trim());
//				Toast.makeText(getApplicationContext(), getString(R.string.main_your_userid) + " " + idText.getText().toString().trim(), Toast.LENGTH_SHORT).show();
			}
		});
    	
    	alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	
    	final AlertDialog alertDialog = alert.create();
    	
    	alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				_dialogContent = idText.getText().toString();
			}
		});
    	
    	return alert.create();
    }
    
    private void showAddTreatment() {
		Intent i = new Intent(Main.this, AddNewTreatment.class);
		startActivityForResult(i, ADD_TREATMENT);
    }
    
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == ADD_TREATMENT) {
            if (resultCode == RESULT_OK) {
            	int id = data.getExtras().getInt(TreatmentsDbAdapter.KEY_ROWID);
//            	Toast.makeText(this, "dodano: " + id, Toast.LENGTH_SHORT);
            	AlarmScheduler.scheduleTreatment(this, id);
            	TreatmentsDbAdapter.getInstance(this).updateScheduled(true, id);
            }
        }
    }

    private void showCurrentDrugs() {
    	Intent intent = new Intent(this, ShowCurrentDrugs.class);
    	startActivity(intent);
    	//Toast.makeText(this, "w showCurrntDrugs", Toast.LENGTH_SHORT).show();
    	// get a Calendar object with current time
    }
    
    private void showDrugsInfo() {
    	Intent intent = new Intent(this, DrugsList.class);
    	startActivity(intent);
    }
    
    private void fetch_data() {
//    	Toast.makeText(this, "text", Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent(this, PoolingService.class);
    	startService(intent);
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	savedInstanceState.putString(DIALOG_TEXT_CONTENT, _dialogContent);
    }
}
