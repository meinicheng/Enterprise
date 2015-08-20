package com.sdbnet.hywy.enterprise.model;

import java.util.Map;

public class Statistical {

	private String date;
	private Map<Integer, Integer> workflowMap;
	
	public Statistical(String date, Map<Integer, Integer> workflowMap) {
		this.date = date;
		this.workflowMap = workflowMap;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Map<Integer, Integer> getWorkflowMap() {
		return workflowMap;
	}

	public void setWorkflowMap(Map<Integer, Integer> workflowMap) {
		this.workflowMap = workflowMap;
	}

	@Override
	public String toString() {
		return "Statistical [date=" + date + ", workflowMap=" + workflowMap
				+ "]";
	}
	
	
}
