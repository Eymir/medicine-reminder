package siwos.dr.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import siwos.dr.data.FrequenciesDbAdapter;
import siwos.dr.data.TreatmentsDbAdapter;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class AlarmScheduler {
	
	public static final String TYPE = "type";
	//type of first notification when user can confirm taking a medicine
	public static final int TYPE_CONFIRMATION = 0;
	//type of notification when user hasn't confirmed talking a medicine
	public static final int TYPE_REMINDER = 1;
	
	public static final int REMIND_ID = -1;
	
	public static void scheduleTreatment(Context ctx, int id, int type) {
		final int HOUR = 1000*60*60;
		final int MINUTE = 1000*60;
		final int REMIND_INTERVAL = 1; //in minutes
		
		Date date = null;
		PendingIntent sender;
		Intent intent;
		//Random rand = new Random();
		
		if (type == TYPE_CONFIRMATION) {
			Log.d("AlarmScheduler", "scheduleTreatment - confirmation: " + id);
			Cursor c = TreatmentsDbAdapter.getInstance(ctx).fetchOne(id);
			if (c.getCount() != 1) return;
			if (c.getInt(c.getColumnIndex(TreatmentsDbAdapter.KEY_ACTIVE)) == 0) return;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				date = df.parse(c.getString(c.getColumnIndex(TreatmentsDbAdapter.KEY_START_DATE)));
			} catch (ParseException e) {
				e.printStackTrace();
				return;
			}
			int freq_id = c.getInt(c.getColumnIndex(TreatmentsDbAdapter.KEY_FREQUENCY_ID));
			Cursor freqCursor = FrequenciesDbAdapter.getInstance(ctx).fetchOne(freq_id);
			if (freqCursor.getCount() != 1) return;
			int interval = freqCursor.getInt(freqCursor.getColumnIndex(FrequenciesDbAdapter.KEY_INTERVAL));
			Date cur_date = new Date();
			date.setTime(cur_date.getTime() + (interval*MINUTE));

			intent = new Intent(ctx, AlarmReceiver.class);
			intent.putExtra(TreatmentsDbAdapter.KEY_ROWID, id);
			intent.putExtra(TYPE, TYPE_CONFIRMATION);
			//Toast.makeText(ctx, "w AlarmSchedulerze: " + id + " wyciagniete jest: " + intent.getIntExtra(TreatmentsDbAdapter.KEY_ROWID, -1), Toast.LENGTH_SHORT).show();
			// In reality, you would want to have a static variable for the request code instead of 192837
			//int msgId = rand.nextInt();
		} else {
			Log.d("AlarmScheduler", "scheduleTreatment - remind: " + id);
			date = new Date();
			date.setTime(date.getTime() + (REMIND_INTERVAL * MINUTE));
			intent = new Intent(ctx, AlarmReceiver.class);
			intent.putExtra(TreatmentsDbAdapter.KEY_ROWID, id);
			intent.putExtra(TYPE, TYPE_REMINDER);
			id = REMIND_ID;
		}	
			
		sender = PendingIntent.getBroadcast(ctx, id, intent, 0);
		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
	
		am.set(AlarmManager.RTC_WAKEUP, date.getTime(), sender);
    }

	public static void unscheduleTreatment(Context ctx, int id) {
		Log.d("AlarmScheduler", "unscheduleTreatment: " + id);
		Intent intent = new Intent(ctx, AlarmReceiver.class);
		intent.putExtra(TreatmentsDbAdapter.KEY_ROWID, id);
		//In reality, you would want to have a static variable for the request code instead of 192837
		PendingIntent sender = PendingIntent.getBroadcast(ctx, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			 // Get the AlarmManager service
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
	}

	public static void unscheduleReminds(Context ctx) {
		Log.d("AlarmScheduler", "unscheduleReminds");
		unscheduleTreatment(ctx, REMIND_ID);
	}
}
