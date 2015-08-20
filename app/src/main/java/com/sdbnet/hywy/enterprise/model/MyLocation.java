package com.sdbnet.hywy.enterprise.model;

public class MyLocation {
	public MyLocation() {

	}

	public MyLocation(double longitude, double latitude, String address) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}

	public MyLocation(double longitude, double latitude, String address,
			long time) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.time = time;
	}

	public String locType;
	public double latitude;
	public double longitude;
	public String address;
	public String city;
	public long time;
	public int errorCode;

	@Override
	public String toString() {
		return "MyLocation [locType=" + locType + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", address=" + address
				+ ", city=" + city + ", time=" + time + ", errorCode="
				+ errorCode + "]";
	}

}
