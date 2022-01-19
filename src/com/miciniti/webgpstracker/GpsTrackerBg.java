package com.miciniti.webgpstracker;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.global.Formatter;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.GPSInfo;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.Dialog;

import com.miciniti.webgpstracker.utils.HttpTask;
import com.miciniti.webgpstracker.utils.Log;
import com.miciniti.webgpstracker.utils.Options;

public class GpsTrackerBg extends Application implements GlobalEventListener
{
	private final String	TAG					= "GpsTrackerEngine";

	private LocationProvider	locationGPSProvider;
	private LocationProvider	locationNETProvider;

	private	Location			location;	
	private	Timer				timer;
	private long				lastGpsFix 		= 0;
	private HttpTask	task;

	public GpsTrackerBg()
	{
        GpsTracker.checkPermissions();
	}

	public void autostartTask()
	{
		Log.i(TAG, "autostartTask");
		addGlobalEventListener(this);
			
		Options.load();
		if(!Options.isAutostart()) return;		

		if(!Options.isEnable()) return;
		
		startUpdates();
	    
		startTimer();		
	}

	public void startTask()
	{
		Log.i(TAG, "startTask");
		addGlobalEventListener(this);
			
		Options.load();
		if(!Options.isEnable()) return;
		
		startUpdates();
	    
		startTimer();		
	}

	public void stopTask()
	{
		Log.i(TAG, "stopTask");

		stopUpdates();
		
		stopTimer();
	
	}

	public void startTimer()
	{
		stopTimer();
		
		Options.load();
		long interval = Options.getInterval();
		
		Log.i(TAG, "startTimer: Interval: " + interval + " second" + (interval > 1 ? "s" : ""));

		GpsTrackerTask task = new GpsTrackerTask();
		timer = new Timer();
		timer.schedule(task, interval * 1000);
	}
	
	public void stopTimer()
	{
		if(timer != null) timer.cancel();		
		timer = null;
	}

	public void startUpdates()
	{
		boolean ret = false;
		int interval = (int) Options.getInterval();
		
		try 
		{ 
			Criteria criteria = new Criteria();			
			criteria.setHorizontalAccuracy(50);
			criteria.setVerticalAccuracy(50);
			criteria.setCostAllowed(false);
			
			locationGPSProvider = LocationProvider.getInstance(criteria);
			if (locationGPSProvider != null)
			{
				locationGPSProvider.setLocationListener(new GpsTrackerListener(), interval, 1 , 1);
				
				ret = true;
			}
			else 
			{
				notify("No GPS Receiver is available!");
				ret = false; 
			}			
		}
		catch (LocationException e)
		{
			notify("GPS error");
			Log.i(TAG, "GPS Error: " + e.getMessage());
			ret = false;
		}

		if(!ret) setupCellsite();
	}
	
	public void setupCellsite()
	{
		BlackBerryCriteria bbCriteria = new BlackBerryCriteria(); 		
		if(GPSInfo.isGPSModeAvailable(GPSInfo.GPS_MODE_CELLSITE))
		{
			try
			{
				bbCriteria.setMode(GPSInfo.GPS_MODE_CELLSITE); 		

				Log.i(TAG, "NET Provider available!");				
				locationNETProvider = LocationProvider.getInstance(bbCriteria);

				if (locationNETProvider != null)
				{
					Log.i(TAG, "NET Provider set");
//					getCellSiteLocation();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				notify("Cellsite GPS not supported!");
			}			
		}
		else
		{
			Log.i(TAG, "NET Provider not available!");
		}
	}

	public boolean stopUpdates()
	{
		try
		{
			if (locationGPSProvider != null)
			{
				locationGPSProvider.reset();
				locationGPSProvider.setLocationListener(null, -1, -1, -1);
			}			
		}
		catch (Exception e)
		{
			Log.i(TAG, "SafetyFirst: " + e.getMessage());
		}
		return true;		
		
	}
	
	private void getCellSiteLocation()
	{
		Log.i(TAG, "getCellSiteLocation");
		
		if(locationNETProvider == null) return;
		
		Location loc = null;
		try
		{
			loc = locationNETProvider.getLocation(20);
		}
		catch (InterruptedException ie)
		{
			Log.i(TAG, "InterruptedEx: " + ie.toString());
		}
		catch (LocationException le)
		{
			Log.i(TAG, "LocationEx:" + le.toString());
		}
		catch (Throwable t)
		{
			Log.i(TAG, t.toString());
		}
		
		location = loc;
	}

	private class GpsTrackerListener implements LocationListener
	{
		public void locationUpdated(LocationProvider provider, Location loc)
		{
			if(provider == locationGPSProvider)
			{
				Log.i(TAG, "locationUpdated: GPS");
				lastGpsFix = System.currentTimeMillis();
	
				location = loc;
			}
			else if(provider == locationNETProvider)
			{
				Log.i(TAG, "locationUpdated: NET");
				long now = System.currentTimeMillis();
				if((now - lastGpsFix) > GpsTracker.LOCATION_RETENTION)
				{
					location = loc;
				}
			}
			else
			{
				Log.i(TAG, "locationUpdated: ???");			
			}
		}
	
		public void providerStateChanged(LocationProvider provider, int newState)
		{
			if (newState == LocationProvider.TEMPORARILY_UNAVAILABLE)
			{
				Log.i(TAG, "providerStateChanged: TEMPORARILY_UNAVAILABLE");
			}
			else if(newState == LocationProvider.OUT_OF_SERVICE)		
			{
				Log.i(TAG, "providerStateChanged: OUT_OF_SERVICE");
			}
		}
	}

	private class GpsTrackerTask extends TimerTask
	{
		String	TAG	= "GpsTrackerTask";

		public GpsTrackerTask()
		{

		}

		public void run()
		{
			Options.load();
			
			Log.i(TAG, "Running task... " + location);
			
			if(location != null && location.isValid())
			{
				Log.i(TAG, "Location Valid");
				
				sendGPS(location);
			}
			else
			{
				Log.i(TAG, "Location Invalid");
				
				getCellSiteLocation();
				sendGPS(location);
			}
			location = null;
			startTimer();
		}
	}

	public void sendGPS(Location location)
	{
    	Formatter 	format = new Formatter();    	

    	String url = GpsTracker.getAppUrl();
		if(location != null && location.isValid())
		{			
			double 	lat 	= location.getQualifiedCoordinates().getLatitude();
			double 	lon 	= location.getQualifiedCoordinates().getLongitude();
			float	alt 	= location.getQualifiedCoordinates().getAltitude();
			float 	speed 	= location.getSpeed() * (float)3.6;
			float	angle 	= location.getCourse();
			
//			if(lat == 0 && lon == 0) return;
			
			url += "?op=gpsok";
			url += "&imei=" 		+ GpsTracker.getIMEI();
			url += "&lat=" 			+ format.formatNumber(lat, 4);
			url += "&lng=" 			+ format.formatNumber(lon, 4);
			url += "&altitude=" 	+ format.formatNumber(alt, 4);
			if(angle != Float.NaN)
				url += "&angle=" 	+ format.formatNumber(angle, 2);
			if(speed != Float.NaN)
				url += "&speed=" 	+ format.formatNumber(speed, 2);
			url += "&dt=" 			+ location.getTimestamp();					
			url += "&gsm_signal=5&gps_signal=5";
		}
		else
		{
			url += "?op=gpsno";
			url += "&imei=" + GpsTracker.getIMEI();
			url += "&gsm_signal=5";
		}
		 
		Log.i(TAG, "Post event...");
		ApplicationManager.getApplicationManager().postGlobalEvent(Consts.GUID_UI, 0, 0, location, location);
		
		if(task != null && task.isAlive()) task.interrupt();
		
		task = new HttpTask(url);
		task.start();
	}

	private void notify(final String msg)
	{
		synchronized (Application.getEventLock())
		{
			UiEngine eng = Ui.getUiEngine();
			if (eng != null)
			{
				Dialog dialog = new Dialog(Dialog.D_OK, msg, Dialog.OK, Bitmap.getPredefinedBitmap(Bitmap.INFORMATION), 0);
				eng.pushGlobalScreen(dialog, 0, UiApplication.GLOBAL_QUEUE);
			}
		}
	}

	public void eventOccurred(long guid,int data0, int data1, Object object0, Object object1)
	{
		Log.i(TAG, "bg eventOccurred: guid: " + guid + " data0: " + data0 + " data1: " + data1);
		
		if(guid == Consts.GUID_BG)
		{
			Log.i(TAG, "restart entOccurred: data0: " + data0 + " data1: " + data1);
			
			stopTask();

			Options.load();			
			if(!Options.isEnable()) return;
			
			startTask();
		}
	}
}
