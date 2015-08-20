package com.sdbnet.hywy.enterprise.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class WorkGroup implements Parcelable {
	private String grpid; // 工作组id
	private String grpname; // 工作组名称
	private int location; // 定位权限
	private List<Staff> staffs = new ArrayList<Staff>(); // 工作组所属成员

	public String getGrpid() {
		return grpid;
	}

	public void setGrpid(String grpid) {
		this.grpid = grpid;
	}

	public String getGrpname() {
		return grpname;
	}

	public void setGrpname(String grpname) {
		this.grpname = grpname;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public List<Staff> getStaffs() {
		return staffs;
	}

	public void setStaffs(List<Staff> staffs) {
		this.staffs = staffs;
	}

	public WorkGroup() {
		staffs = new ArrayList<Staff>();
	}

	public WorkGroup(Parcel in) {
		grpid = in.readString();
		grpname = in.readString();
		location = in.readInt();
		in.readTypedList(staffs, Staff.CREATOR);
	}

	public static final Parcelable.Creator<WorkGroup> CREATOR = new Creator<WorkGroup>() {
		@Override
		public WorkGroup createFromParcel(Parcel source) {
			return new WorkGroup(source);
		}

		@Override
		public WorkGroup[] newArray(int size) {
			return new WorkGroup[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(grpid);
		dest.writeString(grpname);
		dest.writeInt(location);
		dest.writeTypedList(staffs);
	}
}
