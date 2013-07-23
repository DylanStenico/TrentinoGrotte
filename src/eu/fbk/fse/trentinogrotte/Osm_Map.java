package eu.fbk.fse.trentinogrotte;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import eu.fbk.fse.trentinogrotte.model.Cave;
import eu.fbk.fse.trentinogrotte.util.CustomInfoWindow;
import eu.fbk.fse.trentinogrotte.util.GPSTracker;
import eu.fbk.fse.trentinogrotte.util.MapOverlay;
import eu.trentorise.smartcampus.osm.android.api.IGeoPoint;
import eu.trentorise.smartcampus.osm.android.bonuspack.overlays.ExtendedOverlayItem;
import eu.trentorise.smartcampus.osm.android.bonuspack.overlays.ItemizedOverlayWithBubble;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.MapQuestRoadManager;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.RoadNode;
import eu.trentorise.smartcampus.osm.android.util.BoundingBoxE6;
import eu.trentorise.smartcampus.osm.android.util.GeoPoint;
import eu.trentorise.smartcampus.osm.android.util.Geocoding;
import eu.trentorise.smartcampus.osm.android.util.RoutingTask;
import eu.trentorise.smartcampus.osm.android.views.MapController;
import eu.trentorise.smartcampus.osm.android.views.MapView;
import eu.trentorise.smartcampus.osm.android.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import eu.trentorise.smartcampus.osm.android.views.overlay.ItemizedOverlayWithFocus;
import eu.trentorise.smartcampus.osm.android.views.overlay.MyLocationOverlay;
import eu.trentorise.smartcampus.osm.android.views.overlay.OverlayItem;
import eu.trentorise.smartcampus.osm.android.views.overlay.SimpleLocationOverlay;

//import org.osmdroid.api.IGeoPoint;
//import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
//import org.osmdroid.bonuspack.overlays.ItemizedOverlayWithBubble;
//import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
//import org.osmdroid.util.BoundingBoxE6;
//import org.osmdroid.util.GeoPoint;
//import org.osmdroid.views.MapController;
//import org.osmdroid.views.MapView;
//import org.osmdroid.views.overlay.MyLocationOverlay;
//import org.osmdroid.views.overlay.OverlayItem;
//import org.osmdroid.views.overlay.SimpleLocationOverlay;

public class Osm_Map extends Activity{

	private ArrayList<Cave> mCaves;
	private MapView mapView;
	private MapController mapController;
	private SimpleLocationOverlay mMyLocationOverlay;
	private MyLocationOverlay myLoc;
	private static final int MENU_ROUTING_ID = 0;
	private static final int MENU_SHOW_INPUT = 1;
	EditText input;
	Button conferma;
	Button myLocButton;
	BoundingBoxE6 bbox;
	public static boolean setZoomAtBoundingBox = true;
	private final static String MY_PREFERENCES = "MyPref";

	public static boolean startSelectingForRouting = false;
	public static ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
	public static Drawable imageFormarker;
	public static ItemizedOverlayWithFocus<ExtendedOverlayItem> caveOverlay;
	public static ArrayList<ExtendedOverlayItem> markersFindByGeocoding = new ArrayList<ExtendedOverlayItem>();
	private static ArrayList<ExtendedOverlayItem> caveMarker = new ArrayList<ExtendedOverlayItem>();
	public static ItemizedOverlayWithBubble<ExtendedOverlayItem> markersFindByGeocodingOverlay;
	private static int indexOfMarker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_osm__map);
		imageFormarker = getResources().getDrawable(R.drawable.azure_marker24);
		mCaves = new ArrayList<Cave>();
		ArrayList<Cave> tmpCaves = getIntent().getExtras().getParcelableArrayList("Caves");
		waypoints.clear();
		for(Cave cave : tmpCaves)
		{
			mCaves.add(cave);
			Log.d("cave from intent",cave.toString());
		}
		myLocButton = (Button) findViewById(R.id.btMyLocation);
		myLocButton.setBackgroundResource(R.drawable.ic_menu_mylocation);
		myLocButton.setVisibility(myLocButton.VISIBLE);
		conferma = (Button) findViewById(R.id.btConferma);
		conferma.setVisibility(conferma.GONE);
		conferma.setBackgroundResource(R.drawable.arrow);
		input = (EditText) findViewById(R.id.txtFieldInput);
		input.setVisibility(input.GONE);
		mapView = (MapView) findViewById(R.id.mapview);
		//mapView.setTileSource(TileSourceFactory.MAPNIK);
		mapView.setBuiltInZoomControls(true);
		mapView.setMultiTouchControls(true);
		mapController = mapView.getController();
		mapController.setZoom(updateIntger("zoom"));
		Log.d("destroy", "new center: " + updateIntger("zoom"));
		markersFindByGeocodingOverlay = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(getApplicationContext(), markersFindByGeocoding, mapView, new CustomInfoWindow(mapView, getApplicationContext(), false));
		mapView.getOverlays().add(markersFindByGeocodingOverlay);
		mMyLocationOverlay = new SimpleLocationOverlay(this);                          
		mapView.getOverlays().add(mMyLocationOverlay);
		//mapView.getOverlays().add(mScaleBarOverlay);	
		myLoc = new MyLocationOverlay(getApplicationContext(), mapView);
		myLoc.setEnabled(true);
		myLoc.enableMyLocation();
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_cave_marker);
		//myLoc.draw(new Canvas(bitmap.copy(Bitmap.Config.ARGB_8888, true)),mapView,false);
		myLoc.enableCompass();
		myLoc.enableMyLocation();
		//ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		double minLat = 90;
		double maxLat = 0;
		double minLong = 90;
		double maxLong = 0;
		Drawable markerImage = getResources().getDrawable(R.drawable.ic_cave_marker);
		for (Cave cave: mCaves) {
			//items.add(new OverlayItem(cave.getName(), cave.getCaveId(), new GeoPoint(cave.getLatitude(), cave.getLongitude())));
			caveMarker.add(new ExtendedOverlayItem(cave.getName() + " ", "Grotta", new GeoPoint(cave.getLatitude(), cave.getLongitude())));

			caveMarker.get(caveMarker.size() - 1).setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
			caveMarker.get(caveMarker.size() - 1).setMarker(markerImage);

			if(cave.getLatitude() < minLat) minLat = cave.getLatitude();
			if(cave.getLatitude() > maxLat) maxLat = cave.getLatitude();
			if(cave.getLongitude() < minLong) minLong = cave.getLongitude();
			if(cave.getLongitude() > maxLong) maxLong = cave.getLongitude();
		}
		//caveOverlay = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(getApplicationContext(), caveMarker, mapView, new CustomInfoWindow(mapView, getApplicationContext(), true));
		//mapView.getOverlays().add(caveOverlay);
		final CustomInfoWindow tmp = new CustomInfoWindow(mapView, getApplicationContext(), true);
		caveOverlay = new ItemizedOverlayWithFocus<ExtendedOverlayItem>(getApplicationContext(), caveMarker, new OnItemGestureListener<ExtendedOverlayItem>(){

			@Override
			public boolean onItemLongPress(int arg0, ExtendedOverlayItem arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onItemSingleTapUp(int arg0, ExtendedOverlayItem arg1) {
				// TODO Auto-generated method stub
				indexOfMarker = caveMarker.indexOf(arg1);
				if(tmp.isOpen()) tmp.close();
				else tmp.open(arg1,0, -arg1.getHeight());
				return false;
			}
		});
		mapView.getOverlays().add(caveOverlay);
		mapView.getOverlays().add(myLoc);

		bbox = new BoundingBoxE6(maxLat, maxLong, minLat, minLong);
		//		ItemizedIconOverlay<OverlayItem> currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
		//                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
		//                    	//Toast.makeText(getApplicationContext(), item.getTitle(),Toast.LENGTH_SHORT).show();
		//                        return true;
		//                    }
		//                    public boolean onItemLongPress(final int index, final OverlayItem item) {
		//                    	if(mCaves.size() > 1){
		//                    		Intent detailsintent = new Intent(Osm_Map.this, CaveDetails.class);
		//                    		detailsintent.putExtra("SELECTED_CAVE", mCaves.get(index));
		//                    		startActivity(detailsintent);
		//                    	}
		//                        return true;
		//                    }
		//        }, mapView.getResourceProxy());
		//		mapView.getOverlays().add(currentLocationOverlay);smartcampus. 

		if(mCaves.size() > 1)
		{
			mapController.setCenter(new GeoPoint(updateFloat("lat"), updateFloat("long")));
		}
		else
		{
			mapController.setCenter(new GeoPoint(updateFloat("lat"), updateFloat("long")));
		}

		myLocButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GPSTracker pos = new GPSTracker(getApplicationContext());
				mapController.animateTo(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
				mapController.setZoom(11);
				mapController.animateTo(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
			}
		});
		conferma.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				input.setVisibility(input.GONE);
				myLocButton.setVisibility(myLocButton.VISIBLE);
				conferma.setVisibility(conferma.GONE);

				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
				//due righe qua sopra servono per nascondere la tastiera
				ArrayList<GeoPoint> result = Geocoding.FromAddressToPoint(input.getText().toString(), Osm_Map.this);
				if(result != null){
					markersFindByGeocoding.add(new ExtendedOverlayItem(""+ input.getText().toString(), "", result.get(0)));
					mapController.animateTo(result.get(0));
					mapController.setCenter(result.get(0));
					mapController.setZoom(15);
					setZoomAtBoundingBox = false;
					markersFindByGeocoding.get(markersFindByGeocoding.size() - 1).setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
					markersFindByGeocoding.get(markersFindByGeocoding.size() - 1).setMarker(getResources().getDrawable(R.drawable.azure_marker24));
					markersFindByGeocodingOverlay.addItem(markersFindByGeocoding.get(markersFindByGeocoding.size() - 1));
					mapView.invalidate();
				}
			}
		});
		//per il tocco
		mapView.getOverlays().add(new MyMapOverlay(this, mapView));
		//per tenere lo schermo acceso
		mapView.setKeepScreenOn(true);
		//myLoc.enableFollowLocation();
	}

	//	@Override
	//	public boolean onKeyDown(int keyCode, KeyEvent event) {
	//	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	//	    	startSelectingForRouting = false;
	//	    	waypoints.clear();
	//	        Log.d(this.getClass().getName(), "back button pressed");
	//	    }
	//	    return super.onKeyDown(keyCode, event);
	//	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if(setZoomAtBoundingBox){
			mapView.zoomToBoundingBox(bbox);
			setZoomAtBoundingBox = false;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		Log.d(this.getClass().getName(), "orientation");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override protected void onDestroy() {
		Log.d("destroy", "destroyed");

		//		saveInteger("zoom", mapView.getZoomLevel());
		//		saveFloat("lat",mapView.getBoundingBox().getCenter().getLatitudeE6()/1E6F);
		//		saveFloat("long", mapView.getBoundingBox().getCenter().getLongitudeE6()/1E6F);
		mapView.getOverlays().clear();
		//caveOverlay.removeAllItems();
		markersFindByGeocoding.clear();
		caveMarker.clear();
		super.onDestroy();
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//if(mCaves.size() == 1)
		menu.add(Menu.NONE,MENU_ROUTING_ID,Menu.NONE,R.string.routing);
		menu.add(Menu.NONE, MENU_SHOW_INPUT, Menu.NONE, R.string.input);
		menu.add(Menu.NONE, 2, Menu.NONE, "get instruction");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_ROUTING_ID:
			if(mCaves.size() == 1)
			{
				GPSTracker pos = new GPSTracker(getApplicationContext());
				waypoints.add(new GeoPoint(pos.getLatitude(), pos.getLongitude()));
				waypoints.add(new GeoPoint(mCaves.get(0).getLatitude(), mCaves.get(0).getLongitude())); //end point
				RoutingTask.drawPath(waypoints, mapView, getApplicationContext(), Locale.ITALY, MapQuestRoadManager.FASTEST, true);
			}
			else{
				if(startSelectingForRouting && waypoints.size() > 1){
					RoutingTask.drawPath(waypoints, mapView, getApplicationContext(), Locale.ITALY, MapQuestRoadManager.FASTEST, true);
					startSelectingForRouting = false;
				}
				else{
					startSelectingForRouting = true;
				}
			}
			break;
		case MENU_SHOW_INPUT:
			input.setVisibility(input.VISIBLE);
			myLocButton.setVisibility(myLocButton.GONE);
			conferma.setVisibility(conferma.VISIBLE);
			break;
		case 2:
			if(startSelectingForRouting && waypoints.size() > 1){
				ArrayList<GeoPoint> myPoints = new ArrayList<GeoPoint>();
				myPoints.add(new GeoPoint(46.009064, 10.9357738));
				myPoints.add(new GeoPoint(43.8345267, 10.486450));
				ArrayList<RoadNode> myNodes = RoutingTask.getRoadNodes(waypoints, getApplicationContext(), Locale.ITALY, MapQuestRoadManager.FASTEST, true);
				Intent nodeIntent = new Intent(this, IstructionsActivity.class);
				nodeIntent.putExtra("RoadNode", myNodes);
				startActivity(nodeIntent);
				startSelectingForRouting = false;
			}
			else{
				startSelectingForRouting = true;
			}
			break;
		default:
			break;
		}
		return true;
	}	
	public static int bubbleIndexShown(boolean type){
		if(type)
			return indexOfMarker;
		else
			return markersFindByGeocodingOverlay.getBubbledItemId();
	}	

	public void saveInteger(String key, int data) {
		// Otteniamo il riferimento alle Preferences
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		// Otteniamo il corrispondente Editor
		SharedPreferences.Editor editor = prefs.edit();
		// Modifichiamo il valore con quello inserito nell'EditText
		// Lo salviamo nelle Preferences
		editor.putInt(key, data);
		editor.commit();
		updateIntger(key);
	}
	public void saveString(String key, String data) {
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, data);
		editor.commit();
		updateString(key);
	}
	public void saveFloat(String key, float data) {
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putFloat(key, data);
		editor.commit();
		updateFloat(key);
	}

	public String updateString(String key){
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		String value = prefs.getString(key, "");

		return value;
	}
	public int updateIntger(String key){
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		int value = prefs.getInt(key, 0);
		return value;
	}
	public float updateFloat(String key){

		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		float value = prefs.getFloat(key, 0);
		return value;
	}
}

class MyMapOverlay extends MapOverlay{

	MapView myMapView;
	Context mContext;
	IGeoPoint result = null;

	public MyMapOverlay(Context ctx, MapView mapView) {
		super(ctx, 1000);
		// TODO Auto-generated constructor stub
		myMapView = mapView;
		mContext = ctx;
	}

	@Override
	public void onLongPressGesture(MotionEvent event){
		result = myMapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
		GeoPoint myresult = new GeoPoint(result.getLatitudeE6(), result.getLongitudeE6());
		myMapView.invalidate();
		Osm_Map.markersFindByGeocoding.add(new ExtendedOverlayItem("mymarker", "", myresult));
		Osm_Map.markersFindByGeocoding.get(Osm_Map.markersFindByGeocoding.size() - 1).setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
		Osm_Map.markersFindByGeocoding.get(Osm_Map.markersFindByGeocoding.size() - 1).setMarker(Osm_Map.imageFormarker);
		Osm_Map.markersFindByGeocodingOverlay.addItem(Osm_Map.markersFindByGeocoding.get(Osm_Map.markersFindByGeocoding.size() - 1));
		Log.d("touchEvent", result.getLatitudeE6() / 1E6 + "   " +  result.getLongitudeE6() / 1E6);
	}

	public IGeoPoint getPointSelected(){
		return result;
	}
}