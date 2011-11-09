package siwos.dr.services;

import siwos.dr.activities.ConfirmTaking;
import siwos.dr.activities.Main;
import siwos.dr.activities.RemindTaking;
import siwos.dr.data.TreatmentsDbAdapter;
import siwos.dr.dialogs.DialogCreator;
import siwos.dr.*;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	Context ctx;
	private static final int HELLO_ID = 1;
	
	private static int COUNTER = 0;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager nm;
		CharSequence contentTitle;
	    CharSequence contentText;
	    Intent notificationIntent;
	    PendingIntent contentIntent;
	    Notification notification = null;
	    
		ctx = context;
		
		try {
			Bundle bundle = intent.getExtras();
			int type = bundle.getInt(AlarmScheduler.TYPE);
		    int id = bundle.getInt(TreatmentsDbAdapter.KEY_ROWID);
		    
		    nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		    
		    if (type == AlarmScheduler.TYPE_CONFIRMATION) {
		    	CharSequence tickerText = "Drug Remind";
		    	long when = System.currentTimeMillis();
		    	notification = new Notification(R.drawable.heart, tickerText, when);
		    	contentTitle = context.getString(R.string.notify_title);
		    	contentText = context.getString(R.string.notify_text);
		    	notificationIntent = new Intent(context, ConfirmTaking.class);
		    	notificationIntent.putExtra(TreatmentsDbAdapter.KEY_ROWID, id);
		    	notificationIntent.putExtra("counter ", AlarmReceiver.COUNTER++);
		    	contentIntent = PendingIntent.getActivity(context, COUNTER, notificationIntent, 0);
		    	notification.flags |= Notification.FLAG_NO_CLEAR;
		    } else {
		    	CharSequence tickerText = "Drug Remind";
		    	long when = System.currentTimeMillis();
		    	notification = new Notification(R.drawable.heart_exclamation, tickerText, when);
		    	contentTitle = context.getString(R.string.notify_remind_title);
		    	contentText = context.getString(R.string.notify_remind_text);
		    	notificationIntent = new Intent(context, RemindTaking.class);
		    	notificationIntent.putExtra(TreatmentsDbAdapter.KEY_ROWID, id);
		    	notificationIntent.putExtra("counter ", AlarmReceiver.COUNTER++);
		    	contentIntent = PendingIntent.getActivity(context, COUNTER, notificationIntent, 0);
		    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
		    }
		    //set up remind about taking medicine in case that user miss the confirmation
	    	AlarmScheduler.scheduleTreatment(context, id, AlarmScheduler.TYPE_REMINDER);
		    
		    notification.defaults |= Notification.DEFAULT_SOUND;
	    	//notification.sound = Uri.parse("siwos/dr/resources/ambulance.mp3");
		    //notification.sound = Uri.parse("siwoz.dr.resources.ambulance.mp3");
		    notification.defaults |= Notification.DEFAULT_VIBRATE;
		    notification.defaults |= Notification.DEFAULT_LIGHTS;
		    notification.flags |= Notification.FLAG_INSISTENT;
		    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		    nm.notify(COUNTER, notification);
		} catch (Exception e) {
		     //Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
		     e.printStackTrace();
		}
	}
}
