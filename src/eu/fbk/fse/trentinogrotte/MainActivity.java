package eu.fbk.fse.trentinogrotte;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.admob.AdMobAdapterExtras;
import com.google.ads.mediation.admob.AdMobAdapterServerParameters;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import eu.fbk.fse.trentinogrotte.model.Cave;
import eu.fbk.fse.trentinogrotte.util.CavesJSONReader;

public class MainActivity extends Activity {
	private static final int MENU_REFRES_ID = 0;
//	private static final int MENU_OSM_ID = 3;
	File sd = Environment.getExternalStorageDirectory();
	File f = new File(sd, "TrentinoGrotte.txt");
	public static ArrayList<Cave> mCaves;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btList = (Button) findViewById(R.id.btToList);
		Button btGMap = (Button) findViewById(R.id.btGMap);
		Button btOsMap = (Button) findViewById(R.id.btOSMap);
		mCaves = new ArrayList<Cave>();
		refreshCaves(false);
		btList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startListActivity();
			}
		});
		
		btGMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startGoogleMap();
			}
		});
		btOsMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Osm_Map.setZoomAtBoundingBox = true;
				startOpenStreetMap();
			}
		});
	}
	 protected void startListActivity()
	 {
		    if(!f.exists())
		    {
		    	refreshCaves(true);
		        Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
		    }
		    else
		    {
			    Intent listIntent = new Intent(this, ListActivity.class);
				listIntent.putExtra("Caves", mCaves);
				startActivity(listIntent);
		    }
//		    else
//		    	Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
		    
		    
	 }
	 protected void startGoogleMap()
	 {
		    if(!f.exists() && mCaves.size() < 1)
		    	refreshCaves(true);
		    else{
			    Intent mapIntent = new Intent(this, CavesMap.class);
				mapIntent.putExtra("Caves", mCaves);
				startActivity(mapIntent);
		    }
	 }
	 protected void startOpenStreetMap()
	 {
		    if(!f.exists() && mCaves.size() < 1)
		    	refreshCaves(true);
		    else{
			    Intent mapIntent2 = new Intent(this, Osm_Map.class);
				mapIntent2.putExtra("Caves", mCaves);
				startActivity(mapIntent2);
		    }
	 }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//refreshCaves(false);
	}
	
	private void deleteRecursive(File f){
		if(f.isDirectory())
			for(File child: f.listFiles())
				deleteRecursive(child);
		f.delete();
	}
	private void refreshCaves(boolean data)
	{
		mCaves.clear();
		new GetCavesTask(MainActivity.this, data).execute();
	}
	
	public class GetCavesTask extends AsyncTask<String,Integer,Boolean> {

		ProgressDialog dialog;
		Context mContext;
        boolean online;


		public GetCavesTask(Context mContext, boolean online) {
			super();
			this.mContext = mContext;
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
			if(!result){
				File sd = Environment.getExternalStorageDirectory();
				File f = new File(sd, "TrentinoGrotte.txt");
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		menu.add(Menu.NONE,MENU_REFRES_ID,Menu.NONE,R.string.refresh);
//		menu.add(Menu.NONE, MENU_OSM_ID, Menu.NONE, R.string.osm);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_REFRES_ID:
			refreshCaves(true);
			break;
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