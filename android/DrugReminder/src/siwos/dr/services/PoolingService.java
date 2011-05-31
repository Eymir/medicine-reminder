package siwos.dr.services;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import siwos.dr.data.MedicamentsDbAdapter;
import siwos.dr.data.TreatmentsDbAdapter;
import siwos.dr.data.UserDbAdapter;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

public class PoolingService extends IntentService{

	Handler mHandler;
	
	public PoolingService() {
		super("Pooling Service");
	}
	
	public PoolingService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
    public void onCreate() {
		super.onCreate();
        // The service is being created
		mHandler = new Handler();
		Toast.makeText(getApplicationContext(), "onCreate z PoolingService", Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	private class DisplayToast implements Runnable{
		  String mText;

		  public DisplayToast(String text){
		    mText = text;
		  }

		  public void run(){
		     Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
		  }
		}

	
	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}

	private String fetchTreatment () throws IllegalStateException, IOException {
		String buffer = "";
		if (isOnline()) {
		    HttpClient client = new DefaultHttpClient();
		    HttpGet req = new HttpGet("http://reminder-drupal.vipserv.org/user_treatments_json/" + UserDbAdapter.getInstance(this).getId());
		    //req.setParams(params);
		    HttpResponse res = null;
			try {
				res = client.execute(req);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (res != null) {
				res.getStatusLine().getStatusCode();
				HttpEntity entity = res.getEntity();
				int status = res.getStatusLine().getStatusCode();
				InputStream in = entity.getContent();
				for (int i = in.read(); i != -1; i = in.read()) {
					buffer += (char) i;
				}
				in.close();
				if (entity != null) {
				    entity.consumeContent();
				}
		    	return buffer;
			}
		}
		return "";
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		String content = "";
		JSONArray treatments = null;
		JSONArray medicaments = null;
		//while(true) {
			try {
				Thread.sleep(5000, 0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (isOnline()) {
				mHandler.post(new DisplayToast("polaczony z Internetem"));
				try {
					content = fetchTreatment();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//mHandler.post(new DisplayToast(content));
				
				JSONObject jObject = null;
				try {
					jObject = new JSONObject(content);
					medicaments = jObject.getJSONArray("medicaments");
					processWebMedicaments(medicaments);
					treatments = jObject.getJSONArray("treatments");
					processWebTreatments(treatments);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
					
			} else
				mHandler.post(new DisplayToast("brak połączenia"));
		//}
	}

	private void processWebTreatments(JSONArray treatments) throws JSONException {
		JSONObject treatment_entry;
		String treatment_str = "";
		int web_id;
		String start_date;
		int pills;
		String name;
		int interval;
		
		for (int i = 0; i < treatments.length(); i++) {
			treatment_entry = treatments.getJSONObject(i);
			name = treatment_entry.getString("name");
			web_id = treatment_entry.getInt("web_id");
			start_date = treatment_entry.getString("start_date");
			pills = treatment_entry.getInt("pills");
			interval = treatment_entry.getInt("interval");
			
			treatment_str += "nazwa: ";
			treatment_str  += name;
			treatment_str += " interval: ";
			treatment_str  += interval;
			mHandler.post(new DisplayToast(treatment_str));
			treatment_str = "";
			
			TreatmentsDbAdapter.getInstance(getApplicationContext())
				.insert(web_id, start_date, pills, 0, interval, name, true, false);
				
		}
		
		scheduleTreatments();
	}
	
	private void processWebMedicaments(JSONArray medicaments) throws JSONException {
		JSONObject medicament_entry;
		String name;
		String desc;
		String medicament_str = "";
		for (int i = 0; i < medicaments.length(); i++) {
			medicament_entry = medicaments.getJSONObject(i);
			medicament_str += "nazwa: ";
			name = medicament_entry.optString("name");
			medicament_str += name;
			medicament_str += " interval: ";
			desc = medicament_entry.optString("desc");
			medicament_str += desc;
			mHandler.post(new DisplayToast(medicament_str));
			medicament_str = "";
			MedicamentsDbAdapter.getInstance(getApplicationContext()).insert(name, desc);
		}
	}
	
	//Schedules unscheduled treatments
	private void scheduleTreatments() {
		int id;
		Cursor t_cur = TreatmentsDbAdapter.getInstance(getApplicationContext()).fetchUnscheduled();
		t_cur.moveToFirst();
		while(!t_cur.isAfterLast()) {
			id = t_cur.getInt(t_cur.getColumnIndex(TreatmentsDbAdapter.KEY_ROWID));
			AlarmScheduler.scheduleTreatment(getApplicationContext(), id);
			TreatmentsDbAdapter.getInstance(getApplicationContext()).updateScheduled(true, id);
			t_cur.moveToNext();
		}
	}
}

