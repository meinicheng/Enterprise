package com.sdbnet.hywy.enterprise.utils;

import java.io.Serializable;
import java.util.Map;

public class SerializableMap implements Serializable {
	
	private static final long serialVersionUID = 8076794256624532224L;
	
	private Map<String, String> map;

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}
}
