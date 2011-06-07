package siwos.dr.activities;

import siwos.dr.R;
import siwos.dr.data.MedicamentsDbAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class DrugDescription extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drug_description);
		
		TextView name;
		TextView desc;
		
		name = (TextView) findViewById(R.id.drug_description_name);
		desc = (TextView) findViewById(R.id.drug_description_content);
		
		int id = getIntent().getIntExtra(MedicamentsDbAdapter.KEY_ROWID, -1);
		//Toast.makeText(this, "wybrano: " + id, Toast.LENGTH_SHORT).show();
		
		Cursor cur = MedicamentsDbAdapter.getInstance(this).fetchOne(id);
		if(cur.getCount() != 0) {
			name.setText(cur.getString(cur.getColumnIndex(MedicamentsDbAdapter.KEY_NAME)));
			desc.setText(cur.getString(cur.getColumnIndex(MedicamentsDbAdapter.KEY_DESCRIPTION)));
		}
	}

}
