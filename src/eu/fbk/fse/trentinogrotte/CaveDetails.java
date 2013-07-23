package eu.fbk.fse.trentinogrotte;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import eu.fbk.fse.trentinogrotte.model.Cave;
import eu.fbk.fse.trentinogrotte.util.MapOverlay;
import eu.trentorise.smartcampus.osm.android.tileprovider.tilesource.TileSourceFactory;
import eu.trentorise.smartcampus.osm.android.util.GeoPoint;
import eu.trentorise.smartcampus.osm.android.util.OSMGeocoding;
import eu.trentorise.smartcampus.osm.android.views.MapController;
import eu.trentorise.smartcampus.osm.android.views.MapView;
import eu.trentorise.smartcampus.osm.android.views.overlay.ItemizedIconOverlay;
import eu.trentorise.smartcampus.osm.android.views.overlay.MyLocationOverlay;
import eu.trentorise.smartcampus.osm.android.views.overlay.OverlayItem;
import eu.trentorise.smartcampus.osm.android.views.overlay.ScaleBarOverlay;
import eu.trentorise.smartcampus.osm.android.views.overlay.SimpleLocationOverlay;


public class CaveDetails extends Activity {

	Cave mCave;
	private MapView mapView;
	private MapController mapController;
	private SimpleLocationOverlay mMyLocationOverlay;
	private ScaleBarOverlay mScaleBarOverlay;
	private MyLocationOverlay myLoc;
	ArrayList<Cave> mCaves = new ArrayList<Cave>();
	
	//private static final int MENU_SHOW_ON_MAP = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cave_details);
		TextView idTextView = (TextView) findViewById(R.id.textViewCaveID);
		TextView nameTextView = (TextView) findViewById(R.id.textViewName);
		TextView latTextView = (TextView) findViewById(R.id.textViewLatitude);
		TextView longTextView = (TextView) findViewById(R.id.textViewLongitude);
		mCave = (Cave) getIntent().getExtras().getParcelable("SELECTED_CAVE");
		
		nameTextView.setText(mCave.getName());
		idTextView.setText("ID: " + mCave.getCaveId());
		latTextView.setText("Lat: " + Double.toString(mCave.getLatitude()));		
		longTextView.setText("Long: " + Double.toString(mCave.getLongitude()));
	
		mapView = (MapView) findViewById(R.id.mapview);  
		mapView.setTileSource(TileSourceFactory.MAPNIK);
		//mapView.setBuiltInZoomControls(false);
		//mapView.setMultiTouchControls(false);
		//mapView.setMultiTouchControls(true);
		mapController = mapView.getController();
		mapController.setZoom(12);
		
		mMyLocationOverlay = new SimpleLocationOverlay(this);                          
		mapView.getOverlays().add(mMyLocationOverlay);
		mapView.setScrollable(false);
		mScaleBarOverlay = new ScaleBarOverlay(this);                          
		mapView.getOverlays().add(mScaleBarOverlay);
		
		myLoc = new MyLocationOverlay(getApplicationContext(), mapView);
		myLoc.setEnabled(false);
		myLoc.enableMyLocation();
		//myLoc.enableCompass();
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_cave_marker);
		myLoc.draw(new Canvas(bitmap.copy(Bitmap.Config.ARGB_8888, true)),mapView,true);
		//myLoc.enableCompass();
		
		mapView.getOverlays().add(myLoc);
		
		Drawable image = this.getResources().getDrawable(R.drawable.ic_cave_marker);
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		items.add(new OverlayItem(mCave.getName(), mCave.getCaveId(), new GeoPoint(mCave.getLatitude(), mCave.getLongitude())));
		items.get(0).setMarker(image);
		
		
		ItemizedIconOverlay<OverlayItem> currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
            	//Toast.makeText(getApplicationContext(), item.getTitle(),Toast.LENGTH_SHORT).show();
            	Toast.makeText(getApplicationContext(),item.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                return true;
            }
		}, mapView.getResourceProxy());
		mapView.getOverlays().add(currentLocationOverlay);
		mapController.setCenter(new GeoPoint(mCave.getLatitude(), mCave.getLongitude()));
		new searchForAddress(this).execute(mCave);
		MapGesture a =new MapGesture(getApplicationContext(), this);
		mapView.getOverlays().add(a);
	}
	public void show_on_map(){
		Osm_Map.setZoomAtBoundingBox = true;
		Intent mapIntent = new Intent(this, Osm_Map.class);
		mCaves.clear();
		mCaves.add(mCave);
		mapIntent.putExtra("Caves", mCaves);
		startActivity(mapIntent);
	}
	public class searchForAddress extends AsyncTask<Cave, Integer,Address> {

		Context mContext;

		public searchForAddress(Context mContext) {
			super();
			this.mContext = mContext;
		}

		@Override
		protected void onPreExecute() {
			// TODO visualizzare il progress dialog
		}

		@Override
		protected Address doInBackground(Cave... params) {
			// TODO network connection
			List<Address> mAddress = OSMGeocoding.FromPointToAddress(new GeoPoint(params[0].getLatitude(), params[0].getLongitude()), mContext);
			if(mAddress != null)
				return mAddress.get(0);
			else
				return null;
			
		}
		@Override
		protected void onPostExecute(Address result) {
			// TODO togliere il progress dialog e, se andata bene, aggiornare la listView
			TextView countryTextView = (TextView) findViewById(R.id.textViewCountry);
			TextView cityTextView = (TextView) findViewById(R.id.textViewCity);
			if(result != null){
				if(result.getAddressLine(0) != null)
					cityTextView.setText(result.getAddressLine(0));
				if(result.getLocality() != null)
					countryTextView.setText(result.getLocality());
				else if(result.getAddressLine(result.getMaxAddressLineIndex() - 1) != null)
					countryTextView.setText(result.getAddressLine(result.getMaxAddressLineIndex() - 1));
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.cave_details, menu);
		//menu.add(Menu.NONE,MENU_SHOW_ON_MAP,Menu.NONE,R.string.show_on_map);
		return false;
	}
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		
//		super.onOptionsItemSelected(item);
//		switch (item.getItemId()) {
//		case MENU_SHOW_ON_MAP:
//			break;
//		default:
//			break;
//		}
//		return true;
//	}
}
//
class MapGesture extends MapOverlay{
	
	CaveDetails myObj;
	public MapGesture(Context ctx, CaveDetails myObj ) {
		super(ctx, 1);
		// TODO Auto-generated constructor stub
		this.myObj = myObj;
	}

	@Override
	public void onLongPressGesture(MotionEvent event){
		myObj.show_on_map();
	}
}