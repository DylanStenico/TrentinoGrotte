package eu.fbk.fse.trentinogrotte.util;

//import org.osmdroid.bonuspack.overlays.DefaultInfoWindow;
//import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
//import org.osmdroid.util.GeoPoint;
//import org.osmdroid.views.MapView;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import eu.fbk.fse.trentinogrotte.CaveDetails;
import eu.fbk.fse.trentinogrotte.MainActivity;
import eu.fbk.fse.trentinogrotte.Osm_Map;
import eu.fbk.fse.trentinogrotte.R;
import eu.trentorise.smartcampus.osm.android.bonuspack.overlays.DefaultInfoWindow;
import eu.trentorise.smartcampus.osm.android.bonuspack.overlays.ExtendedOverlayItem;
import eu.trentorise.smartcampus.osm.android.util.GeoPoint;
import eu.trentorise.smartcampus.osm.android.views.MapView;

public class CustomInfoWindow extends DefaultInfoWindow {
	Context mContext;
	boolean isACavemarker;
	MapView myMapView;
	public CustomInfoWindow(MapView mapView, Context context, boolean type) {
		super(R.layout.bonuspack_bubble, mapView);
		mContext = context;
		isACavemarker = type;
		myMapView = mapView;
		Button btn = (Button)(mView.findViewById(R.id.bubble_moreinfo));

		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//                  Toast.makeText(v.getContext(), "Button clicked", Toast.LENGTH_LONG).show();
				if(isACavemarker){
					if(Osm_Map.startSelectingForRouting){
						Toast.makeText(v.getContext(), "added", Toast.LENGTH_LONG).show();
						Osm_Map.waypoints.add(new GeoPoint(MainActivity.mCaves.get(Osm_Map.bubbleIndexShown(isACavemarker)).getLatitude(), MainActivity.mCaves.get(Osm_Map.bubbleIndexShown(isACavemarker)).getLongitude()));
					}
					else{
						Osm_Map.waypoints.clear();
						Intent detailsintent = new Intent(mContext, CaveDetails.class);
						detailsintent.putExtra("SELECTED_CAVE", MainActivity.mCaves.get(Osm_Map.bubbleIndexShown(isACavemarker)));
						detailsintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
						mContext.startActivity(detailsintent);
					}
				}
				else{
				if(Osm_Map.startSelectingForRouting){
						Toast.makeText(v.getContext(), "added", Toast.LENGTH_LONG).show();
				        Osm_Map.waypoints.add(new GeoPoint(Osm_Map.markersFindByGeocodingOverlay.getItem(Osm_Map.bubbleIndexShown(isACavemarker)).mGeoPoint.getLatitudeE6()/1E6, Osm_Map.markersFindByGeocodingOverlay.getItem(Osm_Map.bubbleIndexShown(isACavemarker)).mGeoPoint.getLongitudeE6()/1E6));
					}
					else
						Osm_Map.waypoints.clear();
				}
			}
		});
	}
	@Override
	public void onOpen(ExtendedOverlayItem item){
		super.onOpen(item);
		mView.findViewById(R.id.bubble_moreinfo).setVisibility(View.VISIBLE);
	}
}