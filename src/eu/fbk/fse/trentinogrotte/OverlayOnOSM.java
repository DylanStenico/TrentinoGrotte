package eu.fbk.fse.trentinogrotte;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import eu.trentorise.smartcampus.osm.android.bonuspack.overlays.ExtendedOverlayItem;
import eu.trentorise.smartcampus.osm.android.bonuspack.overlays.ItemizedOverlayWithBubble;
import eu.trentorise.smartcampus.osm.android.views.MapView;
import eu.trentorise.smartcampus.osm.android.views.overlay.OverlayItem;

public class OverlayOnOSM {

	public static ItemizedOverlayWithBubble<ExtendedOverlayItem> addOverlayWithBubble(Context mContext, MapView mapView, Drawable markerImage, ArrayList<ExtendedOverlayItem> singleMarker){

		ItemizedOverlayWithBubble<ExtendedOverlayItem> myMarkers = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(mContext, singleMarker, mapView);
		mapView.getOverlays().add(myMarkers);

		for(ExtendedOverlayItem myMarker : singleMarker){
			myMarker.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
			myMarker.setMarker(markerImage);
		}
		return myMarkers;
	}
}
