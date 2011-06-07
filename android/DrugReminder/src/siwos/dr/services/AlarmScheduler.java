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
import android.widget.Toast;

public class AlarmScheduler {
	
	public static void scheduleTreatment(Context ctx, int id) {
		final int HOUR = 1000*60*60;
		final int MINUTE = 1000*60;
		//Random rand = new Random();
		
    	Cursor c = TreatmentsDbAdapter.getInstance(ctx).fetchOne(id);
    	if (c.getCount() != 1) return;
    	if (c.getInt(c.getColumnIndex(TreatmentsDbAdapter.KEY_ACTIVE)) == 0) return;
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	Date date = null;
		try {
			date = df.parse(c.getString(c.getColumnIndex(TreatmentsDbAdapter.KEY_START_DATE)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
    	int freq_id = c.getInt(c.getColumnIndex(TreatmentsDbAdapter.KEY_FREQUENCY_ID));
    	Cursor freqCursor = FrequenciesDbAdapter.getInstance(ctx).fetchOne(freq_id);
    	if (freqCursor.getCount() != 1) return;
    	int interval = freqCursor.getInt(freqCursor.getColumnIndex(FrequenciesDbAdapter.KEY_INTERVAL));
    	Date cur_date = new Date();
    	while (cur_date.getTime() > date.getTime()) {
    		date.setTime(date.getTime() + (interval*MINUTE));   
    	}

		Intent intent = new Intent(ctx, AlarmReceiver.class);
		intent.putExtra(TreatmentsDbAdapter.KEY_ROWID, id);
		//Toast.makeText(ctx, "w AlarmSchedulerze: " + id + " wyciagniete jest: " + intent.getIntExtra(TreatmentsDbAdapter.KEY_ROWID, -1), Toast.LENGTH_SHORT).show();
		// In reality, you would want to have a static variable for the request code instead of 192837
		//int msgId = rand.nextInt();
		PendingIntent sender = PendingIntent.getBroadcast(ctx, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			 // Get the AlarmManager service
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		//am.cancel(sender);
		am.set(AlarmManager.RTC_WAKEUP, date.getTime(), sender);
    }

	public static void unscheduleTreatment(Context ctx, int id) {
		Intent intent = new Intent(ctx, AlarmReceiver.class);
		intent.putExtra(TreatmentsDbAdapter.KEY_ROWID, id);
		// In reality, you would want to have a static variable for the request code instead of 192837
		PendingIntent sender = PendingIntent.getBroadcast(ctx, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			 // Get the AlarmManager service
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
	}

}
