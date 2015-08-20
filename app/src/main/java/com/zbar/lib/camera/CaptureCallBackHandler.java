package com.zbar.lib.camera;

import android.os.Handler;

public interface CaptureCallBackHandler {
	
	public void handleDecode(String result);
	
	public int getX();
	
	public void setX(int x);
	
	public int getY();
	
	public int getCropWidth();
	
	public int getCropHeight();
	
	public boolean isNeedCapture();
	
	public Handler getHandler();
}
