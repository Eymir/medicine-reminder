package siwos.dr.activities;

import siwos.dr.R;
import siwos.dr.data.MedicamentsDbAdapter;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DrugsList extends ListActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drugs_list);
		fillData();
	}

	private void fillData() {
		Cursor drugs_cur = MedicamentsDbAdapter.getInstance(this).fetchAll();
		startManagingCursor(drugs_cur);
		String[] from = new String[]{MedicamentsDbAdapter.KEY_NAME};
		int[] to = new int[]{R.id.list_entry};
		SimpleCursorAdapter placesAdap = new SimpleCursorAdapter(this, R.layout.list_entry, drugs_cur, from, to);
		setListAdapter(placesAdap);
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//Toast.makeText(BranchListView.this, "wybrano: " + position, Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, DrugDescription.class);
		i.putExtra(MedicamentsDbAdapter.KEY_ROWID, (int)id);
		startActivity(i);
		//setResult(Activity.RESULT_OK, i);
		//fire another activity
//		Toast.makeText(this, "wybrano: " + id, Toast.LENGTH_SHORT).show();
	}
}
