package com.sdbnet.hywy.enterprise.model;

public class VehicleSortModel extends SortModel {
	private String hfmId;
	private String hdiId;
	private String detail;
	private String hdiLocTime;
	private String hdiAddress;
	private int ustatus;

	private String hdiPlate;

	public String getHfmId() {
		return hfmId;
	}

	public void setHfmId(String hfmId) {
		this.hfmId = hfmId;
	}

	public String getHdiId() {
		return hdiId;
	}

	public void setHdiId(String hdiId) {
		this.hdiId = hdiId;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getHdiLocTime() {
		return hdiLocTime;
	}

	public void setHdiLocTime(String hdiLocTime) {
		this.hdiLocTime = hdiLocTime;
	}

	public String getHdiAddress() {
		return hdiAddress;
	}

	public void setHdiAddress(String hdiAddress) {
		this.hdiAddress = hdiAddress;
	}

	public String getHdiPlate() {
		return hdiPlate;
	}

	public void setHdiPlate(String hdiPlate) {
		this.hdiPlate = hdiPlate;
	}

	public int getUstatus() {
		return ustatus;
	}

	public void setUstatus(int ustatus) {
		this.ustatus = ustatus;
	}

}
