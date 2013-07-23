package eu.fbk.fse.trentinogrotte.util;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import eu.fbk.fse.trentinogrotte.Osm_Map;
import eu.fbk.fse.trentinogrotte.R;
import eu.trentorise.smartcampus.osm.android.bonuspack.overlays.ExtendedOverlayItem;
import eu.trentorise.smartcampus.osm.android.bonuspack.overlays.ItemizedOverlayWithBubble;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.MapQuestRoadManager;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.Road;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.RoadManager;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.RoadNode;
import eu.trentorise.smartcampus.osm.android.util.GeoPoint;
import eu.trentorise.smartcampus.osm.android.views.MapView;
import eu.trentorise.smartcampus.osm.android.views.overlay.OverlayItem;
import eu.trentorise.smartcampus.osm.android.views.overlay.PathOverlay;
//import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
//import org.osmdroid.bonuspack.overlays.ItemizedOverlayWithBubble;
//import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
//import org.osmdroid.bonuspack.routing.OSRMRoadManager;
//import org.osmdroid.bonuspack.routing.Road;
//import org.osmdroid.bonuspack.routing.RoadManager;
//import org.osmdroid.bonuspack.routing.RoadNode;
//import org.osmdroid.util.GeoPoint;
//import org.osmdroid.views.MapView;
//import org.osmdroid.views.overlay.OverlayItem;
//import org.osmdroid.views.overlay.PathOverlay;

public class RoutingTask extends AsyncTask<ArrayList<GeoPoint>,Integer,PathOverlay> {

	ProgressDialog dialog;
	Context mContext;
	Road road;
	MapView mapView;
	static boolean stop = false;
	private Locale mLocale;

	public RoutingTask(Context mContext, MapView mMapView, Locale locale) {
		super();
		this.mContext = mContext;
		dialog = new ProgressDialog(mContext);
		mapView = mMapView;
	    mLocale = locale;
	}

	@Override
	protected void onPreExecute() {
		// TODO visualizzare il progress dialog
		dialog.setMessage(mContext.getString(R.string.loading));
		dialog.show();
	}

	@Override
	protected PathOverlay doInBackground(ArrayList<GeoPoint>... params) {
		// TODO network connection
		//		GPSTracker pos = new GPSTracker(mContext);
		RoadManager roadManager = new MapQuestRoadManager(mLocale, MapQuestRoadManager.PEDESTRIAN, mContext, true);//MapQuestRoadManager();
		//		ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
		//		waypoints.add(new GeoPoint(48.13, -1.63));
		//		waypoints.add(new GeoPoint(48.4, -1.9)); //end point
		road = roadManager.getRoad(params[0]);
		//only to a step path
		roadManager.addRequestOption("routeType=pedestrian");
		return RoadManager.buildRoadOverlay(road, mapView.getContext());
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
	@Override
	protected void onPostExecute(PathOverlay result) {
		// TODO togliere il progress dialog e, se andata bene, aggiornare la listView
		if(road.mNodes.size() > 0){
			try{
				mapView.getOverlays().add(result);
				mapView.invalidate();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			final ArrayList<ExtendedOverlayItem> roadItems = new ArrayList<ExtendedOverlayItem>();
			ItemizedOverlayWithBubble<ExtendedOverlayItem> roadNodes = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(mContext, roadItems, mapView);
			mapView.getOverlays().add(roadNodes);
			Drawable marker = mContext.getResources().getDrawable(R.drawable.marker_node);
			Osm_Map.waypoints.clear();
			for (int i=0; i<road.mNodes.size(); i++){
				RoadNode node = road.mNodes.get(i);
				Log.d("time", Double.toString(node.mDuration));
				Log.d("time", Integer.toString(i));
				ExtendedOverlayItem nodeMarker = new ExtendedOverlayItem(node.mInstructions, "Time: " +fromSecondToString((int)node.mDuration)+ "\nLenght: " + fromKilometersToMeters(node.mLength), node.mLocation);
				nodeMarker.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
				nodeMarker.setMarker(marker);
				roadNodes.addItem(nodeMarker);
			}
		}
		else{
			if(!stop){
				RoutingTask route = new RoutingTask(mContext, mapView, mLocale);
				route.execute(Osm_Map.waypoints);
				stop = true;
			}
			else
				stop = false;
		}
		if(dialog.isShowing())
			dialog.dismiss();
	}
}