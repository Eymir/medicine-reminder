package siwos.dr.services;

import siwos.dr.activities.ConfirmTaking;
import siwos.dr.activities.Main;
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
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	Context ctx;
	private static final int HELLO_ID = 1;
	
	private static int COUNTER = 0;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		ctx = context;
		
		try {
			Bundle bundle = intent.getExtras();
		    int id = bundle.getInt(TreatmentsDbAdapter.KEY_ROWID);
		    
//		    Toast.makeText(context, "Alarm Receiver id: " + id, Toast.LENGTH_SHORT).show();
		    
		    AlarmScheduler.unscheduleTreatment(ctx, id);
		    if (TreatmentsDbAdapter.getInstance(context).takeAnotherPill(id))
		    	AlarmScheduler.scheduleTreatment(ctx, id);
		    else {
		    	TreatmentsDbAdapter.getInstance(context).updateActive(false, id);
		    	TreatmentsDbAdapter.getInstance(context).updateScheduled(false, id);
		    }
		    
		    
		    
		    NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		    
		    
		    CharSequence tickerText = "Drug Remind";
		    long when = System.currentTimeMillis();
		    Notification notification = new Notification(R.drawable.heart, tickerText, when);
		    CharSequence contentTitle = context.getString(R.string.notify_title);
		    CharSequence contentText = context.getString(R.string.notify_text);
		    Intent notificationIntent = new Intent(context, ConfirmTaking.class);
		    notificationIntent.putExtra(TreatmentsDbAdapter.KEY_ROWID, id);
		    notificationIntent.putExtra("counter ", AlarmReceiver.COUNTER++);
		    PendingIntent contentIntent = PendingIntent.getActivity(context, COUNTER, notificationIntent, 0);
		    notification.defaults |= Notification.DEFAULT_SOUND;
		    notification.defaults |= Notification.DEFAULT_VIBRATE;
		    notification.defaults |= Notification.DEFAULT_LIGHTS;
		    notification.flags |= Notification.FLAG_AUTO_CANCEL;
		    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		    nm.notify(COUNTER, notification);
		    
		} catch (Exception e) {
//		     Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
		     e.printStackTrace();
		}
	}
}
