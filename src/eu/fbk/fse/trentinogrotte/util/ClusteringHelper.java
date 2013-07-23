//package eu.fbk.fse.trentinogrotte.util;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.ConcurrentModificationException;
//import java.util.List;
//
//import org.osmdroid.bonuspack.overlays.ItemizedOverlayWithBubble;
//import org.osmdroid.util.BoundingBoxE6;
//import org.osmdroid.util.GeoPoint;
//import org.osmdroid.views.MapView;
//import org.osmdroid.views.overlay.OverlayItem;
//
//import eu.fbk.fse.trentinogrotte.Osm_Map;
//
//import android.R;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Paint.Align;
//import android.graphics.Rect;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.location.Location;
//import android.util.Log;
//import android.util.SparseArray;
//
//public class ClusteringHelper {
//
//	private static final String TAG = "MapManager.ClusteringHelper";
//
//	Context context;
//	private static Context mContext;
//	private static final int DENSITY_X = 10;
//
//	private static final int DENSITY_Y = 10;
//
//
//	public static final String TITLE_CLUSTERED = "clusteredmarker";
//
//
//	private static List<List<List<OverlayItem>>> grid = new ArrayList<List<List<OverlayItem>>>();
//
//	private static SparseArray<int[]> item2group = new SparseArray<int[]>();
//
//	public synchronized static <T extends OverlayItem> List<OverlayItem> cluster(Context mContext, MapView map,
//
//			Collection<T> objects) {
//
//		item2group.clear();
//
//		// 2D array with some configurable, fixed density
//
//		grid.clear();
//
//
//		for (int i = 0; i <= DENSITY_X; i++) {
//
//			ArrayList<List<OverlayItem>> column = new ArrayList<List<OverlayItem>>(DENSITY_Y + 1);
//
//			for (int j = 0; j <= DENSITY_Y; j++) {
//
//				column.add(new ArrayList<OverlayItem>());
//
//			}
//			grid.add(column);
//
//		}
//
//		BoundingBoxE6 bb = map.getProjection().getBoundingBox();
//		GeoPoint lu = new GeoPoint(bb.getLatNorthE6(),bb.getLonWestE6());
//
//		GeoPoint rd = new GeoPoint(bb.getLatSouthE6(),bb.getLonEastE6());
//
//		int step = (int) (Math.abs((lu.getLongitudeE6()) - (rd.getLongitudeE6())) / DENSITY_X);
//
//
//		// compute leftmost bound of the affected grid:
//
//		// this is the bound of the leftmost grid cell that intersects
//
//		// with the visible part
//		int startX = (int) ((lu.getLongitudeE6()) - (lu.getLongitudeE6() % step));
//
//		if (lu.getLongitudeE6() < 0) {
//
//			startX -= step;
//
//		}
//		// compute bottom bound of the affected grid
//
//		int startY = (int) ((rd.getLatitudeE6()) - ((rd.getLatitudeE6()) % step));
//
//		if (lu.getLatitudeE6() < 0) {
//
//			startY -= step;
//
//		}
//		int endX = startX + (DENSITY_X + 1) * step;
//
//		int endY = startY + (DENSITY_Y + 1) * step;
//
//
//		int idx = 0;
//
//		try {
//			for (OverlayItem basicObject : objects) {
//
//				GeoPoint objLatLng = getGeoPointFromBasicObject(basicObject);
//
//
//				if (objLatLng != null && (objLatLng.getLongitudeE6()) >= startX && (objLatLng.getLongitudeE6()) <= endX
//
//						&& (objLatLng.getLatitudeE6()) >= startY && (objLatLng.getLatitudeE6()) <= endY) {
//
//					int binX = (int) (Math.abs((objLatLng.getLongitudeE6()) - startX) / step);
//
//					int binY = (int) (Math.abs((objLatLng.getLatitudeE6()) - startY) / step);
//
//
//					item2group.put(idx, new int[] { binX, binY });
//
//					// just push the reference
//					grid.get(binX).get(binY).add(basicObject);
//
//				}
//				idx++;
//
//			}
//		} catch (ConcurrentModificationException ex) {
//
//			Log.e(TAG, ex.toString());
//
//		}
//
//		if (map.getZoomLevel() == map.getMaxZoomLevel()) {
//
//			for (int i = 0; i < grid.size(); i++) {
//
//				for (int j = 0; j < grid.get(0).size(); j++) {
//
//					List<OverlayItem> curr = grid.get(i).get(j);
//
//					if (curr.size() == 0)
//
//						continue;
//
//
//					if (i > 0) {
//
//						if (checkDistanceAndMerge(i - 1, j, curr))
//
//							continue;
//					}
//
//					if (j > 0) {
//
//						if (checkDistanceAndMerge(i, j - 1, curr))
//
//							continue;
//					}
//
//					if (i > 0 && j > 0) {
//
//						if (checkDistanceAndMerge(i - 1, j - 1, curr))
//
//							continue;
//					}
//
//				}
//			}
//
//		}
//
//		// generate markers
//
//		List<OverlayItem> markers = new ArrayList<OverlayItem>();
//
//
//		for (int i = 0; i < grid.size(); i++) {
//
//			for (int j = 0; j < grid.get(i).size(); j++) {
//
//				List<OverlayItem> markerList = grid.get(i).get(j);
//
//				if (markerList.size() > 1) {
//
//					markers.add(createGroupMarker(mContext, map, markerList, i, j));
//
//				} else if (markerList.size() == 1) {
//
//					// draw single marker
//					markers.add(createSingleMarker(mContext,markerList.get(0), i, j));
//
//				}
//			}
//
//		}
//
//		return markers;
//
//	}
//
//	public static void render(MapView map, List<OverlayItem> markers) {
//		ItemizedOverlayWithBubble<OverlayItem> currentOverlay = new ItemizedOverlayWithBubble<OverlayItem>(mContext, markers, map);
//		map.getOverlays().add(currentOverlay);
//
//	}
//
//
//	private static OverlayItem createSingleMarker(Context mContext,OverlayItem item, int x, int y) {
//
//		GeoPoint latLng = getGeoPointFromBasicObject(item);
//
//		OverlayItem marker = new OverlayItem(x + ":" + y,"",latLng);
//		marker.setMarker(Osm_Map.imageFormarker);
//
//		Log.d("qwerty","single");
//		return marker;
//	}
//
//	private static OverlayItem createGroupMarker(Context mContext, MapView map, List<OverlayItem> markerList, int x,int y) {
//		OverlayItem item = markerList.get(0);
//		GeoPoint latLng = getGeoPointFromBasicObject(item);
////		int markerIcon = R.drawable.marker_poi_generic;
////		Drawable bd = new BitmapDrawable(writeOnMarker(mContext, markerIcon,Integer.toString(markerList.size())));
//		OverlayItem marker = new OverlayItem(x + ":" + y,"",latLng);
//		marker.setMarker(Osm_Map.imagePluriMarker);
//
//		Log.d("qwerty","group");
//		return marker;
//
//	}
//
//	private static Bitmap writeOnMarker(Context mContext, int drawableId, String text) {
//
//		float scale = mContext.getResources().getDisplayMetrics().density;
//
//
//		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888,true);
//
//
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//
//		paint.setTextAlign(Align.CENTER);
//
//		paint.setTextSize(scale * 14);
//
//		paint.setAntiAlias(true);
//
//		paint.setARGB(255, 255, 255, 255);
//
//
//		Canvas canvas = new Canvas(bitmap);
//
//		Rect bounds = new Rect();
//
//		paint.getTextBounds(text, 0, text.length(), bounds);
//
//		float x = bitmap.getWidth() / 2;
//
//		float y = bitmap.getHeight() / 2;
//
//		canvas.drawText(text, x, y, paint);
//
//
//		return bitmap;
//
//	}
//
//	public static List<OverlayItem> getFromGridId(String id) {
//		try {
//			String[] parsed = id.split(":");
//			int x = Integer.parseInt(parsed[0]);
//			int y = Integer.parseInt(parsed[1]);
//			return grid.get(x).get(y);
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//
//	private static boolean checkDistanceAndMerge(int i, int j, List<OverlayItem> curr) {
//		List<OverlayItem> src = grid.get(i).get(j);
//		if (src.size() == 0) {
//			return false;
//		}
//		GeoPoint srcLatLng = getGeoPointFromBasicObject(src.get(0));
//		GeoPoint currLatLng = getGeoPointFromBasicObject(curr.get(0));
//
//		if (srcLatLng != null && currLatLng != null) {
//			float[] dist = new float[3];
//			Location.distanceBetween(srcLatLng.getLatitudeE6() / 1E6, srcLatLng.getLongitudeE6() / 1E6, currLatLng.getLatitudeE6() / 1E6, currLatLng.getLongitudeE6() / 1E6,dist);
//
//			if (dist[0] < 20) {
//				src.addAll(curr);
//				curr.clear();
//				return true;
//			}
//		}
//		return false;
//
//	}
//
//	private static GeoPoint getGeoPointFromBasicObject(OverlayItem object) {
//
//		GeoPoint geoPoint = null;
//		geoPoint = object.getPoint();
//		return geoPoint;
//	}
//
//}