package siwos.dr.resources;

import android.content.Context;
import siwos.dr.R;

public class ResourcesServe {

	public static String getFrequencyName(Context ctx, String variableName) {
		if(variableName.compareTo("once_a_day") == 0) return ctx.getString(R.string.frequency_once_daily);
		else if(variableName.compareTo("twice_a_day") == 0) return ctx.getString(R.string.frequency_twice_daily);
		else if(variableName.compareTo("tree_times_a_day") == 0) return ctx.getString(R.string.frequency_tree_times_daily);
		else if(variableName.compareTo("four_times_a_day") == 0) return ctx.getString(R.string.frequency_four_times_daily);
		else if(variableName.compareTo("every_two_hours") == 0) return ctx.getString(R.string.frequency_every_two_hours);
		else if(variableName.compareTo("every_hour") == 0) return ctx.getString(R.string.frequency_every_hour);
		else return "";
	}
	
}
