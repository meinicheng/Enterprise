package com.sdbnet.hywy.enterprise.model;

import com.sdbnet.hywy.enterprise.album.AlbumHelper.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;

public class ReportModel implements Serializable {
	// public String title;
	// public String theme;
	public String date="";
	public String place="";
	public String explain="";
	public String orders="";
	// public ArrayList<String> imgList;
	public ArrayList<ImageItem> imgList;

	public String accid="";
	public String pname="";
	public String loctel="";
	public String sourceby="";
	@Override
	public String toString() {
		return "ReportModel [date=" + date + ", place=" + place + ", explain="
				+ explain + ", orders=" + orders + ", imgList=" + imgList
				+ ", accid=" + accid + ", pname=" + pname + ", loctel="
				+ loctel + ", sourceby=" + sourceby + "]";
	}



}
