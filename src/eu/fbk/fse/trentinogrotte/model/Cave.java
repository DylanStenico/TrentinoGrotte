package eu.fbk.fse.trentinogrotte.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Cave implements Parcelable{

	private String name;
	private String caveId;
	private double latitude;
	private double longitude;
	private int visited;

	public Cave(String name, String caveId, double latitude, double longitude) {
		super();
		this.name = name;
		this.caveId = caveId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.visited = 0;
	}
	public Cave(){
		super();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCaveId() {
		return caveId;
	}
	public void setCaveId(String caveId) {
		this.caveId = caveId;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	

	public int isVisited() {
		return visited;
	}
	public void setVisited(int visited) {
		this.visited = visited;
	}
	@Override
	public String toString() {
		return "Cave [name=" + name + ", caveId=" + caveId + "]";
	}
	
	// Parcelable Stuff

	public Cave(Parcel source) {
		// Fill here
		name = source.readString();
		caveId = source.readString();
		latitude = source.readDouble();
		longitude = source.readDouble();
		visited = source.readInt();
	}

	public static final Parcelable.Creator<Cave> CREATOR = 
			new Creator<Cave>() {

		@Override
		public Cave[] newArray(int size) {
			return new Cave[size];
		}

		@Override
		public Cave createFromParcel(Parcel source) {
			return new Cave(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// Fill here
		dest.writeString(name);
		dest.writeString(caveId);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeInt(visited);
	}
	
}
