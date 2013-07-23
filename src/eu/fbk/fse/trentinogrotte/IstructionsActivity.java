package eu.fbk.fse.trentinogrotte;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.RoadNode;

public class IstructionsActivity extends Activity {

	ArrayList<RoadNode> mRoadNode;
	NodeAdapter nodesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_istructions);
		
		ArrayList<Bitmap> myArray= new ArrayList<Bitmap>();
		ListView listView = (ListView) findViewById(R.id.myListView);
		mRoadNode = new ArrayList<RoadNode>();
		ArrayList<RoadNode> tmpNode = getIntent().getExtras().getParcelableArrayList("RoadNode");
		for(RoadNode roadNode : tmpNode)
		{
			mRoadNode.add(roadNode);
			Log.d("RoadNode from intent",roadNode.toString());
//			getImage task = new getImage();
//			task.execute(roadNode.mIconUrl);
//			try {
//				myArray.add(task.get());
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		nodesAdapter = new NodeAdapter(this, android.R.layout.simple_list_item_1, mRoadNode);
		listView.setAdapter(nodesAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		
		});
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//refreshCaves(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.istructions, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
//		case MENU_REFRESH_ID:
//			refreshCaves(false);
//			break;
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
		return false;
	}
	
	private class NodeAdapter extends ArrayAdapter<RoadNode>{
		//ArrayList<Bitmap> mArray;
		public NodeAdapter(Context context, int ViewResourceId, ArrayList<RoadNode> mRoadNode /*ArrayList<Bitmap> myArray*/)
		{
			super(context,ViewResourceId,mRoadNode);
			//mArray = myArray;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if(v == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.node_raw, null);
			}
			RoadNode selectedNode = mRoadNode.get(position);
			if(selectedNode != null){
				TextView nameTextView = (TextView) v.findViewById(R.id.textViewBig);
				TextView caveIdTextView = (TextView) v.findViewById(R.id.textViewSmall);
				nameTextView.setText(selectedNode.mInstructions);
				//caveIdTextView.setText(fromKilometersToMeters(selectedNode.mLength));
				caveIdTextView.setText((fromSecondToString((int)selectedNode.mDuration)) + "  " + fromKilometersToMeters(selectedNode.mLength));
				ImageView mImageView = (ImageView) v.findViewById(R.id.imageView1);
				
				Resources res = getResources();
				int imageId = R.drawable.marker_node;
				switch(selectedNode.mManeuverType){
				case 99:
					imageId = R.drawable.id99;
					break;
				case 0:
					imageId = R.drawable.id0;
					break;
				case 1:
					imageId = R.drawable.id1;
					break;
				case 2:
					imageId = R.drawable.id2;
					break;
				case 3:
					imageId = R.drawable.id3;
					break;
				case 4:
					//imageId = R.drawable.id4;
					break;
				case 5:
					imageId = R.drawable.id5;
					break;
				case 6:
					imageId = R.drawable.id6;
					break;
				case 7:
					imageId = R.drawable.id7;
					break;
				case 8:
					//imageId = R.drawable.id8;
					break;
				case 9:
					//imageId = R.drawable.id9;
					break;
				case 10:
					imageId = R.drawable.id10;
					break;
				case 11:
					imageId = R.drawable.id11;
					break;
				case 12:
					imageId = R.drawable.id12;
					break;
				case 13:
					//imageId = R.drawable.id13;
					break;
				case 14:
					imageId = R.drawable.id14;
					break;
				case 15:
					imageId = R.drawable.id15;
					break;
				case 16:
					imageId = R.drawable.id16;
					break;
				case 17:
					imageId = R.drawable.id17;
					break;
				case 18:
					//imageId = R.drawable.id18;
					break;					
				}
				Bitmap bitmap = BitmapFactory.decodeResource(res, imageId);
				setImage(bitmap, mImageView);
			}
			return v;
		}
		private String fromKilometersToMeters(double kilometers){
			String toReturn = "";
			int km = (int) Math.floor(kilometers);
			int m = (int) ((kilometers - km) * 1000);
			if(km > 0) toReturn += km + "km ";
			return toReturn + m + "m";
		}
		private String fromSecondToString(int second){
			String toReturn = "";
			int sec = second % 60;
			int min = ((second - sec) / 60) % 60;
			int hour =  (int) Math.floor(second / 3600);
			if(hour > 0) toReturn += hour +"h ";
			if(min > 0)  toReturn += min +"m ";
			return toReturn + sec + "s";
		}
	}
	private class getImage extends AsyncTask<String, Integer, Bitmap>{
		
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap myBit = null;
			try {
				myBit = (BitmapFactory.decodeStream(new URL(params[0]).openConnection().getInputStream()));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return myBit;
		}
	}
	
	private void setImage(Bitmap image2,ImageView mImageView) {
		// TODO Auto-generated method stub
		mImageView.setImageBitmap(image2);
	}
}