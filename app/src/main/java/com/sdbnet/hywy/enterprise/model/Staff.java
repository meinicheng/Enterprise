package com.sdbnet.hywy.enterprise.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Staff implements Parcelable {
	private String pid; // 用户id
	private String status; // 状态

	private String pname; // 用户名
	private String loctel; // 定位电话
	private String truckno; // 车牌号
	private String trucktype; // 车型
	private Double trucklength; // 车长
	private Double truckweight; // 车载重
	private String loctime; // 定位时间
	private String locaddress; // 定位地址
	private Double longitude; // 经度
	private Double latitude; // 纬度
	


	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getLoctel() {
		return loctel;
	}

	public void setLoctel(String loctel) {
		this.loctel = loctel;
	}

	public String getTruckno() {
		return truckno;
	}

	public void setTruckno(String truckno) {
		this.truckno = truckno;
	}

	public String getTrucktype() {
		return trucktype;
	}

	public void setTrucktype(String trucktype) {
		this.trucktype = trucktype;
	}

	public Double getTrucklength() {
		return trucklength;
	}

	public void setTrucklength(Double trucklength) {
		this.trucklength = trucklength;
	}

	public Double getTruckweight() {
		return truckweight;
	}

	public void setTruckweight(Double truckweight) {
		this.truckweight = truckweight;
	}

	public String getLoctime() {
		return loctime;
	}

	public void setLoctime(String loctime) {
		this.loctime = loctime;
	}

	public String getLocaddress() {
		return locaddress;
	}

	public void setLocaddress(String locaddress) {
		this.locaddress = locaddress;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static final Parcelable.Creator<Staff> CREATOR = new Creator<Staff>() {
		@Override
		public Staff createFromParcel(Parcel source) {
			Staff staff = new Staff();
			staff.pid = source.readString();
			staff.pname = source.readString();
			staff.loctel = source.readString();
			staff.truckno = source.readString();
			staff.trucktype = source.readString();
			staff.trucklength = source.readDouble();
			staff.truckweight = source.readDouble();
			staff.loctime = source.readString();
			staff.locaddress = source.readString();
			staff.longitude = source.readDouble();
			staff.latitude = source.readDouble();
			staff.status = source.readString();
			return staff;
		}

		@Override
		public Staff[] newArray(int size) {
			return new Staff[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(pid);
		dest.writeString(pname);
		dest.writeString(loctel);
		dest.writeString(truckno);
		dest.writeString(trucktype);
		dest.writeDouble(trucklength);
		dest.writeDouble(truckweight);
		dest.writeString(loctime);
		dest.writeString(locaddress);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeString(status);
	}

	@Override
	public String toString() {
		return "Staff [pid=" + pid + ", status=" + status + ", pname=" + pname
				+ ", loctel=" + loctel + ", truckno=" + truckno
				+ ", trucktype=" + trucktype + ", trucklength=" + trucklength
				+ ", truckweight=" + truckweight + ", loctime=" + loctime
				+ ", locaddress=" + locaddress + ", longitude=" + longitude
				+ ", latitude=" + latitude + "]";
	}

	
}
