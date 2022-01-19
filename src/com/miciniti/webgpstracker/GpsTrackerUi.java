package com.miciniti.webgpstracker;

import javax.microedition.location.Location;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.UiApplication;

import com.miciniti.webgpstracker.utils.Log;

public class GpsTrackerUi extends UiApplication implements GlobalEventListener
{
	private static final String	TAG	= "GpsTrackerUi";
	private GpsTrackerScreen	screen;

	public GpsTrackerUi()
	{
        GpsTracker.checkPermissions();
        
        // Push a screen onto the UI stack for rendering.
		screen = new GpsTrackerScreen(this);
		pushScreen(screen);
	}

	public void eventOccurred(long guid,int data0, int data1, Object object0, Object object1)
	{
		Log.i(TAG, "ui eventOccurred: guid: " + guid + " data0: " + data0 + " data1: " + data1);
		
		if(guid == Consts.GUID_UI)
		{
			Log.i(TAG, "location entOccurred: data0: " + data0 + " data1: " + data1);
			if(screen != null) 
				screen.updateLocation((Location)object0);
		}
	}

}
