package eu.fbk.fse.trentinogrotte.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;
import eu.fbk.fse.trentinogrotte.model.Cave;

public class CavesJSONReader {

	private static File sd = Environment.getExternalStorageDirectory();
	private static File f = new File(sd, "TrentinoGrotte.txt");


	public static List<Cave> getCaves(String cavesUrl, boolean thereisconnection) {
		ArrayList<Cave> caves = new ArrayList<Cave>();
		JSONObject responseJSON;
		Log.d("hallo","refresh");
		try {
			if(thereisconnection){
				Log.d("hallo","connection");
				URL url;
				url = new URL("http://dati.trentino.it/api/action/datastore_search?resource_id=20a688ed-d453-4af3-8082-cb0cc0ccf0ee");

				URLConnection connection;
				connection = url.openConnection();

				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream in = httpConnection.getInputStream();
					InputStreamReader is = new InputStreamReader(in);
					StringBuilder sb=new StringBuilder();
					BufferedReader br = new BufferedReader(is);
					String read = br.readLine();

					while(read != null) {
						System.out.println(read);
						sb.append(read);
						read =br.readLine();
					}
					responseJSON = new JSONObject(sb.toString());
					if(f.exists())
					{
						if(f.delete())
							write(sb.toString());
					}
					else
						write(sb.toString());
				}
			}
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
	    }   
		catch (IOException e) {
			e.printStackTrace();
	    }
		catch (JSONException e) {
			e.printStackTrace();
		}
		try{
			responseJSON = new JSONObject(readSavedData());
	        Log.d("jason", readSavedData());
			JSONArray records = responseJSON.optJSONObject("result").optJSONArray("records");
			for (int i = 0; i < records.length(); i++) {
				JSONObject caveJson= records.getJSONObject(i);
				Cave cave = new Cave();
				cave.setName(caveJson.optString("NOME"));
				cave.setCaveId(caveJson.optString("ID_GROTTA"));
				// TODO set coordinates
				String coordsStr = caveJson.optString("GeoJSON");
				coordsStr.replace("\\", "");
				JSONObject geoJson = new JSONObject(coordsStr);
				JSONArray coordsArray = geoJson.getJSONArray("coordinates");
				cave.setLatitude(coordsArray.getDouble(1));
				cave.setLongitude(coordsArray.getDouble(0));
				caves.add(cave);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return caves;
	}

	//per scrivere su file// 
	public static void write (String Data){
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fw = new FileWriter(f, true);
			bw = new BufferedWriter(fw);
			bw.write(Data);
			bw.close();
			fw.close();
			//Toast.makeText(this, "Settings saved",Toast.LENGTH_SHORT).show(); 
		} 
		catch (IOException e) {       
			e.printStackTrace();
			//Toast.makeText(this, "Settings not saved",Toast.LENGTH_SHORT).show(); 
		}
	}

	// per leggere da file 
	public static String readSavedData () {
		String datax = "" ;
		try {
			FileInputStream fis = new FileInputStream(f.getAbsolutePath());
			InputStreamReader isr = new InputStreamReader ( fis ) ;
			BufferedReader buffreader = new BufferedReader ( isr ) ;

			String readString = buffreader.readLine ( ) ;
			while ( readString != null ) {
				datax = datax + readString ;
				readString = buffreader.readLine ( ) ;
			}
			isr.close ( ) ;
		} catch ( IOException ioe ) {
			ioe.printStackTrace ( ) ;
		}
		return datax ;
	}
}