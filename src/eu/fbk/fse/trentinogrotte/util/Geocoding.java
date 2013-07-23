package eu.fbk.fse.trentinogrotte.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import eu.fbk.fse.trentinogrotte.R;
import eu.trentorise.smartcampus.osm.android.util.GeoPoint;
//import org.osmdroid.util.GeoPoint;
/**
 * This class contains two class doing Geocoding and Reverse Geocoding with the android.location.Geocoder class. 
 * @author dylan
 */
public class Geocoding {
	/**
	 * this class contains an AsyncTask that permits to convert a GeoPoint into an Address
	 * You have to instace a new AsyncTask like this:
	 * Geocoding.FromPointToAddress myTask = new Geocoding.FromPointToAddress(context);
	 * myTask.execute(double arg[0], double arg[1]);
	 * Address result = myTask.get();
	 * @return Address if it works, else null
	 */
	public static class FromPointToAddress extends AsyncTask<Double,Integer,Address> {
		ProgressDialog dialog;
		Context mContext;
		private boolean connectionError = false;
		private boolean addressError = false;
		List<Address> address;

		/**
		 * @param mContext
		 * the Application Context
		 */
		public FromPointToAddress(Context mContext) {
			super();
			this.mContext = mContext;
			dialog = new ProgressDialog(mContext);
		}
		@Override
		protected void onPreExecute() {
			// TODO visualizzare il progress dialog
			dialog.setMessage(mContext.getString(R.string.loading));
			dialog.show();
		}
		@Override
		protected Address doInBackground(Double... params) {
			Geocoder myGeocoder = new Geocoder(mContext,Locale.getDefault());
			try {
				address = myGeocoder.getFromLocation(params[0], params[1], 1);
				Log.d("indirizzo", params[0] + "  " + params[1]);
			}
			catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			}
			catch(IllegalArgumentException e){
				addressError = true;
				e.printStackTrace();
			}
			if((address == null || address.size() == 0) && !connectionError) addressError = true;
			if(!connectionError && !addressError){
				return address.get(0);
			}
			else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(Address result) {
			// TODO togliere il progress dialog e, se andata bene, aggiornare la listView
			try{
				if(dialog.isShowing())
					dialog.dismiss();
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}
			try{
				Log.d("indirizzo", result.getAddressLine(0));
				Log.d("indirizzo", result.getAddressLine(1));
				Log.d("indirizzo", result.getAddressLine(2));
			}
			catch(IllegalArgumentException e){
				e.printStackTrace();
			}
			catch(NullPointerException e){
				e.printStackTrace();
			}
			//if     (connectionError)  Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
			//else if(addressError)     Toast.makeText(mContext, "Address Error",    Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * this class contains an AsyncTask that permits to convert an Address into a GeoPoint
	 * You have to instace a new AsyncTask like this:
	 * Geocoding.FromAddressToPoint myTask = new Geocoding.FromAddressToPoint(context);
	 * myTask.execute(String address);
	 * GeoPoint result = myTask.get();
	 * @return GeoPoint if it works, else null
	 */

	public static class FromAddressToPoint extends AsyncTask<String,Integer,GeoPoint> {
		ProgressDialog dialog;
		Context mContext;
		String myAddress;
		boolean connectionError = false;
		boolean addressError = false;

		/**
		 * @param mContext
		 * the Application context
		 */
		public FromAddressToPoint(Context mContext) {
			super();
			this.mContext = mContext;
			dialog = new ProgressDialog(mContext);
		}

		@Override
		protected void onPreExecute() {
			// TODO visualizzare il progress dialog
			dialog.setMessage(mContext.getString(R.string.loading));
			dialog.show();
		}
		@Override
		protected GeoPoint doInBackground(String... params){
			myAddress = params[0];
			Geocoder myGeocoder = new Geocoder(mContext,Locale.ITALY);
			List<Address> risultati = null;
			Address address = null;
			try {
				risultati = myGeocoder.getFromLocationName(params[0], 5);
			} 
			catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			}
			catch(IllegalArgumentException e){
				addressError = true;
				e.printStackTrace();
			}
			if((risultati == null || risultati.size() == 0) && !connectionError) addressError = true;
			if(connectionError || addressError){
				return null;
			}
			else{
				address = risultati.get(0);
				Log.d("COORDINATES", (double)address.getLatitude() + "    " +(double)address.getLongitude());
				return new GeoPoint((double)address.getLatitude(),(double)address.getLongitude());
			}
		}

		@Override
		protected void onPostExecute(GeoPoint result) {
			// TODO togliere il progress dialog e, se andata bene, aggiornare la listView

			if(dialog.isShowing())
				dialog.dismiss();
			//if     (connectionError)  Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
			//else if(addressError)     Toast.makeText(mContext, "Address Error",    Toast.LENGTH_SHORT).show();
		}
	}

}
