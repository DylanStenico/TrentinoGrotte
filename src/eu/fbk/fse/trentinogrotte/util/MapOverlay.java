package eu.fbk.fse.trentinogrotte.util;

//import org.osmdroid.views.MapView;
//import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import eu.trentorise.smartcampus.osm.android.views.MapView;
import eu.trentorise.smartcampus.osm.android.views.overlay.Overlay;

/**
 * A class to handling the press gesture on the map(you can set the pressing time). You only have to Override the OnLongPress(MotionEvent event) method. 
 * @author Dylan Stenico
 *
 */
public class MapOverlay extends Overlay
{
	private long time;
	private int maxTime;
	private float x, y;
	private final float deltaX = 2;
	private final float deltaY = 2;
	private Context mContext;
	public MapOverlay(Context ctx, int pressTime) {
		super(ctx);
		// TODO Auto-generated constructor stubs
		mContext = ctx;
		maxTime = pressTime;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) 
	{   
		//---when user lifts his finger---
		Log.d("motionEvent", Integer.toString(event.getPointerCount()));
		if (event.getAction() == event.ACTION_DOWN && event.getPointerCount() == 1){
			time = System.currentTimeMillis();
			x = event.getX();
			y = event.getY();
		}
		else if(event.getAction() == event.ACTION_UP && (System.currentTimeMillis() - time >= maxTime)&& event.getPointerCount() == 1)
		{
			if((Math.abs(event.getX() - x) <= deltaX) && (Math.abs(event.getY() - y) <= deltaY))
			{
				onLongPressGesture(event);
				return true;
			}
			else if(event.getPointerCount() > 1)
				time = System.currentTimeMillis();
		}
		return false;
	}
/**
 * What to do when you make a long press on the map.
 * @param mapView
 * @param event
 */
	public void onLongPressGesture(MotionEvent event){
	}

	@Override
	protected void draw(Canvas arg0, MapView arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}
}