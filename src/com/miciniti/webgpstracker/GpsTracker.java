package com.miciniti.webgpstracker;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.GPRSInfo;

public class GpsTracker
{
	public final static String	appName		= "GPS-tracker";

	public final static String	appLogUrl	= "http://miciniti.com/gps-tracker/blackberry.php";
	public final static String	appAboutUrl	= "http://miciniti.com/gps-tracker/about.php";

	public final static int		LOCATION_RETENTION	= 10000;

	public static void main(String[] args)
	{
		boolean	autostart = false;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("autostart"))
			{
				autostart = true;
			}
		}

		if (autostart)
		{
			while (ApplicationManager.getApplicationManager().inStartup())
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
				}
			}

			GpsTrackerBg engine = new GpsTrackerBg();
			engine.autostartTask();
			engine.enterEventDispatcher();
		}
		else
		{
			GpsTrackerUi ui = new GpsTrackerUi();
			ui.enterEventDispatcher();
		}
	}

	public static String getAppUrl()
	{
		return appLogUrl;
	}

	public static String getAppHelp()
	{
		return appAboutUrl;
	}

	public static String getIMEI()
	{
		byte[] imei = GPRSInfo.getIMEI();
		return GPRSInfo.imeiToString(imei, false);
	}

	public static void checkPermissions()
	{
		ApplicationPermissionsManager apm = ApplicationPermissionsManager.getInstance();
		ApplicationPermissions original = apm.getApplicationPermissions();
		if ((original.getPermission(ApplicationPermissions.PERMISSION_LOCATION_DATA) == ApplicationPermissions.VALUE_ALLOW) && (original.getPermission(ApplicationPermissions.PERMISSION_INTERNET) == ApplicationPermissions.VALUE_ALLOW) && (original.getPermission(ApplicationPermissions.PERMISSION_SERVER_NETWORK) == ApplicationPermissions.VALUE_ALLOW) && (original.getPermission(ApplicationPermissions.PERMISSION_PHONE) == ApplicationPermissions.VALUE_ALLOW))
		{
			return;
		}

		ApplicationPermissions permRequest = new ApplicationPermissions();
		permRequest.addPermission(ApplicationPermissions.PERMISSION_LOCATION_DATA);
		permRequest.addPermission(ApplicationPermissions.PERMISSION_INTERNET);
		permRequest.addPermission(ApplicationPermissions.PERMISSION_SERVER_NETWORK);
		permRequest.addPermission(ApplicationPermissions.PERMISSION_PHONE);

		boolean acceptance = ApplicationPermissionsManager.getInstance().invokePermissionsRequest(permRequest);

		if (acceptance)
		{
			return;
		}
		else
		{
		}
	}

}
