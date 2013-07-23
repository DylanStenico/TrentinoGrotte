package eu.fbk.fse.trentinogrotte;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import eu.fbk.fse.trentinogrotte.model.Cave;
import eu.fbk.fse.trentinogrotte.util.CavesJSONReader;

public class ListActivity extends Activity {

	private static final int MENU_REFRESH_ID = 0;
	ArrayList<Cave> mCaves;
	CaveAdapter cavesAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		//Button button = (Button) findViewById(R.id.buttonRefresh);
		ListView listView = (ListView) findViewById(R.id.listView);
		mCaves = new ArrayList<Cave>();
		ArrayList<Cave> tmpCaves = getIntent().getExtras().getParcelableArrayList("Caves");
		for(Cave cave : tmpCaves)
		{
			mCaves.add(cave);
			Log.d("cave from intent",cave.toString());
		}
		refreshCaves(false);

		/*button.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
//				List<Cave> caves = CavesJSONReader.getCaves("");
//				for(Cave cave : caves){
//					Log.d("TAG",cave.getName());
//					mCaves.add(cave);
//
//				}
//				cavesAdapter.notifyDataSetChanged();

				refreshCaves();
			}
		});*/

		cavesAdapter = new CaveAdapter(this, android.R.layout.simple_list_item_1,mCaves);
		listView.setAdapter(cavesAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// Voglio vedere i dettagli della grotta
				Intent detailsintent = new Intent(ListActivity.this, CaveDetails.class);
				detailsintent.putExtra("SELECTED_CAVE", mCaves.get(position));
				startActivity(detailsintent);
			}
		
		});
		
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//refreshCaves(false);
	}
	
	



	private void refreshCaves(boolean data)
	{
		mCaves.clear();
		new GetCavesTask(ListActivity.this, data).execute();
	}
	
	private class GetCavesTask extends AsyncTask<String,Integer,Boolean> {

		ProgressDialog dialog;
        boolean online;


		public GetCavesTask(Context mContext, boolean online) {
			super();
			dialog = new ProgressDialog(mContext);
			this.online = online;
		}

		@Override
		protected void onPreExecute() {
			// TODO visualizzare il progress dialog
			dialog.setMessage(getString(R.string.loading));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO network connection
			File sd = Environment.getExternalStorageDirectory();
			File f = new File(sd, "TrentinoGrotte.txt");
			if(!online && !f.exists()) online = true;
			List<Cave> caves = CavesJSONReader.getCaves("",online);
			if(caves.size()>0){
				for(Cave cave : caves){
					Log.d("TAG",cave.getName()+" "+cave.getLatitude()+" "+cave.getLongitude());
					//write(""+ cave.getName()+" "+cave.getLatitude()+" "+cave.getLongitude());
					mCaves.add(cave);
				}
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO togliere il progress dialog e, se andata bene, aggiornare la listView
			
			if(dialog.isShowing())
				dialog.dismiss();
			if(result){
				cavesAdapter.notifyDataSetChanged();
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
//		menu.add(Menu.NONE,MENU_MAP_ID,Menu.NONE,R.string.map);
//		menu.add(Menu.NONE, MENU_OSM_ID, Menu.NONE, R.string.osm);
		menu.add(Menu.NONE,MENU_REFRESH_ID,Menu.NONE, R.string.refresh);
//		menu.add(Menu.NONE,MENU_CLEAR_ID,Menu.NONE, R.string.clear);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_REFRESH_ID:
			refreshCaves(false);
			break;
//		case MENU_CLEAR_ID:
//			mCaves.clear();
//			cavesAdapter.notifyDataSetChanged();
//			break;
//		case MENU_MAP_ID:
//			Intent mapIntent = new Intent(this, CavesMap.class);
//			mapIntent.putExtra("Caves", mCaves);
//			startActivity(mapIntent);
//			break;
//		case MENU_OSM_ID:
//			Intent mapIntent2 = new Intent(this, Osm_Map.class);
//			mapIntent2.putExtra("Caves", mCaves);
//			startActivity(mapIntent2);	
		default:
			break;
		}
		return true;
	}
	
	private class CaveAdapter extends ArrayAdapter<Cave>{
		public CaveAdapter(Context context, int ViewResourceId,
				ArrayList<Cave> mCaves)
		{
			super(context,ViewResourceId,mCaves);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.cave_row, null);
			}
			Cave selectedCave = mCaves.get(position);
			if(selectedCave != null){
				TextView nameTextView = (TextView) v.findViewById(R.id.textViewBig);
				TextView caveIdTextView = (TextView) v.findViewById(R.id.textViewSmall);
				nameTextView.setText(selectedCave.getName());
				caveIdTextView.setText(selectedCave.getCaveId());
			}
			return v;
		}
	
		
	}
}