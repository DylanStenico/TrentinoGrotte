package eu.fbk.fse.trentinogrotte;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import eu.fbk.fse.trentinogrotte.model.Cave;

public class CavesMap extends FragmentActivity {
	
	SupportMapFragment mMapFragment;
	ArrayList<Cave> mCaves;
	private GoogleMap mMap;
	private UiSettings mUiSettings;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.caves_map);
		mCaves = new ArrayList<Cave>();
		ArrayList<Cave> tmpCaves = getIntent().getExtras().getParcelableArrayList("Caves");
		for(Cave cave : tmpCaves)
		{
			mCaves.add(cave);
			Log.d("cave from intent",cave.toString());
		}
		mMapFragment = SupportMapFragment.newInstance();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.MapContainer, mMapFragment);
		fragmentTransaction.commit();
		setUpMapIfNeeded();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		if(mMap == null) {
			mMap = mMapFragment.getMap();
			if(mMap != null) {
				setUpMap();
			}
		}
	}
	
	private void setUpMap() {
		// TODO Auto-generated method stub
		mMap.setMyLocationEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		mUiSettings = mMap.getUiSettings();
		mUiSettings.setMyLocationButtonEnabled(true);
		
		final LatLngBounds.Builder builder = new LatLngBounds.Builder();
		
		mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			
			@Override
			public void onCameraChange(CameraPosition arg0) {
				mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
				mMap.setOnCameraChangeListener(null);
			}
		});
		
		for (Cave cave : mCaves) {
			LatLng caveLatLng = new LatLng(cave.getLatitude(),cave.getLongitude());
			mMap.addMarker(new MarkerOptions()
				.position(caveLatLng)
				.title(cave.getName())
				.snippet("Cave ID: "+cave.getCaveId())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cave_marker)));
			builder.include(caveLatLng);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.caves_map, menu);
		return false;
	}
}
