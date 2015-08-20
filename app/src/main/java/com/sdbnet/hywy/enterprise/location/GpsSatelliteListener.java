package com.sdbnet.hywy.enterprise.location;

import android.location.GpsStatus;
import android.location.GpsStatus.Listener;

public class GpsSatelliteListener implements Listener {

	@Override
	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		switch (event) {

		case GpsStatus.GPS_EVENT_FIRST_FIX:
			break;

		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			break;

		case GpsStatus.GPS_EVENT_STARTED:
			break;

		case GpsStatus.GPS_EVENT_STOPPED:
			break;
		}
	}

}
