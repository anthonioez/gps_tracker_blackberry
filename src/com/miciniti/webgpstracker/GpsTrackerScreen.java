package com.miciniti.webgpstracker;

import javax.microedition.global.Formatter;
import javax.microedition.location.Location;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.BorderFactory;

import com.miciniti.webgpstracker.fields.EvenlySpacedHorizontalFieldManager;
import com.miciniti.webgpstracker.fields.MCheck;
import com.miciniti.webgpstracker.fields.MEdit;
import com.miciniti.webgpstracker.fields.MLabel;
import com.miciniti.webgpstracker.utils.Log;
import com.miciniti.webgpstracker.utils.Options;
import com.miciniti.webgpstracker.utils.Utils;

public class GpsTrackerScreen extends MainScreen
{
	public static final String	TAG	= "GpsTrackerScreen";

	private VerticalFieldManager	vfmContainer;
	private VerticalFieldManager	vfm;
	private MLabel					label;
	private MCheck					enable;
//TODO	private MCheck					notify;
	private MCheck					autostart;
	private MCheck					protect;
	private MLabel					imei;
	private BasicEditField			interval;

	private MLabel					latitude;
	private MLabel					longitude;
	private MLabel					speed;
	private MEdit					password;
	private BitmapField				logo;

	private GpsTrackerUi ui = null;
	private Font					smallFont;

	private String	locLat	= "0";
	private String	locLon	= "0";
	private String	locSpd 	= "0";

	public GpsTrackerScreen(GpsTrackerUi ui)
	{
		super(Manager.NO_VERTICAL_SCROLL);
		
		this.ui = ui;

		Font font = getFont();
		smallFont = font.derive(Font.PLAIN, (int) (font.getHeight() * 0.85));
		
	}
	
	public void setupUI()
	{
		Log.i(TAG, "Add event listener...");
		ui.addGlobalEventListener(ui);

		deleteAll();
			
		vfmContainer = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL | Manager.USE_ALL_HEIGHT | Manager.USE_ALL_WIDTH);
		vfmContainer.setBackground(BackgroundFactory.createSolidBackground(Consts.bgColor));
		vfmContainer.setPadding(new XYEdges(5, 5, 5, 5));

		vfm = new VerticalFieldManager(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		vfm.setBackground(BackgroundFactory.createSolidBackground(Consts.bgColor));

		Bitmap bmp = Utils.getFitBitmapImage("logo.png", Display.getWidth()-20, Display.getHeight() / 4);
		logo = new BitmapField(bmp, Field.FIELD_HCENTER);
		vfm.add(logo);

		label = new MLabel("Convert your mobile phone to GPS tracking device", Field.FIELD_HCENTER);
		label.setFont(smallFont);
		vfm.add(label);

		vfm.add(new NullField());

		imei = new MLabel("Your phone's IMEI: " + GpsTracker.getIMEI(), Field.FIELD_HCENTER | Field.FIELD_VCENTER);
		imei.setFont(smallFont);
		imei.setTextColor(Color.DARKRED);
		vfm.add(imei);

		vfm.add(new NullField());

		EvenlySpacedHorizontalFieldManager ehfm = new EvenlySpacedHorizontalFieldManager(Manager.FIELD_LEFT | Manager.FIELD_VCENTER | Manager.USE_ALL_WIDTH);

		label = new MLabel("Lat:", Field.FIELD_LEFT | Field.FIELD_VCENTER);
		label.setFont(smallFont);
		ehfm.add(label);

		latitude = new MLabel(getLatitude(), Field.FIELD_LEFT | Field.FIELD_VCENTER);
		latitude.setFont(smallFont);
		latitude.setTextColor(Color.DARKRED);
		ehfm.add(latitude);

		label = new MLabel("Lon:", Field.FIELD_LEFT | Field.FIELD_VCENTER);
		label.setFont(smallFont);
		ehfm.add(label);

		longitude = new MLabel(getLongitude(), Field.FIELD_LEFT | Field.FIELD_VCENTER);
		longitude.setFont(smallFont);
		longitude.setTextColor(Color.DARKRED);
		ehfm.add(longitude);

		label = new MLabel("Speed:", Field.FIELD_LEFT | Field.FIELD_VCENTER);
		label.setFont(smallFont);
		ehfm.add(label);

		speed = new MLabel(getSpeed(), Field.FIELD_LEFT | Field.FIELD_VCENTER);
		speed.setFont(smallFont);
		speed.setTextColor(Color.DARKRED);
		ehfm.add(speed);

		vfm.add(ehfm);

		label = new MLabel("Interval(s):", Field.FIELD_LEFT | Field.FIELD_VCENTER);
		label.setFont(smallFont);

		interval = new MEdit("", String.valueOf(Options.getInterval()), 5, Field.FIELD_RIGHT | Field.FIELD_VCENTER | BasicEditField.EDITABLE | BasicEditField.FILTER_INTEGER);
		interval.setBorder(BorderFactory.createBevelBorder(new XYEdges(1, 1, 1, 1)));

		vfm.add(label);
		vfm.add(interval);

		enable = new MCheck("Enable tracking", Options.isEnable(), MCheck.FIELD_LEFT | MCheck.USE_ALL_WIDTH);
//TODO		notify = new MCheck("Show notification", Options.isNotification(), MCheck.FIELD_LEFT | MCheck.USE_ALL_WIDTH);
		autostart = new MCheck("Automatic startup", Options.isAutostart(), MCheck.FIELD_LEFT | MCheck.USE_ALL_WIDTH);
		protect = new MCheck("Protect with password", Options.isProtected(), MCheck.FIELD_LEFT | MCheck.USE_ALL_WIDTH);

		vfm.add(enable);
//TODO	vfm.add(notify);
		vfm.add(autostart);
		vfm.add(protect);

		label = new MLabel("Password:", Field.FIELD_LEFT | Field.FIELD_VCENTER);
		label.setFont(smallFont);

		password = new MEdit("", Options.getPassword(), 32, Field.FIELD_RIGHT | Field.FIELD_VCENTER | BasicEditField.EDITABLE | BasicEditField.FILTER_INTEGER);
		password.setBorder(BorderFactory.createBevelBorder(new XYEdges(1, 1, 1, 1)));

		vfm.add(label);
		vfm.add(password);

		vfmContainer.add(vfm);
		add(vfmContainer);
	}

	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);

		menu.add(new saveMenuItem());
		menu.add(new helpMenuItem());
		menu.add(new closeMenuItem());
	}

	protected void onUiEngineAttached(boolean attached)
	{
		super.onUiEngineAttached(attached);

		if (attached)
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
			{
				public void run()
				{
					login();
				}
			});
		}
	}


	public boolean onClose()
	{
		Log.i(TAG, "Removing event listener...");
		ui.removeGlobalEventListener(ui);		

		latitude = null;
		longitude = null;
		speed = null;
		
		this.close();
		return true;
	}

	private class saveMenuItem extends MenuItem
	{
		public saveMenuItem()
		{
			super("Save", 1, 1);
		}

		public void run()
		{
			saveSettings();
		}
	}

	private class helpMenuItem extends MenuItem
	{
		public helpMenuItem()
		{
			super("How to", 2, 2);
		}

		public void run()
		{
			BrowserSession browser = Browser.getDefaultSession();
			browser.displayPage(GpsTracker.getAppHelp());
		}
	}

	private class closeMenuItem extends MenuItem
	{
		public closeMenuItem()
		{
			super("Exit", 3, 3);
		}
	
		public void run()
		{
			onClose();
		}
	}

	private void saveSettings()
	{
		String str = interval.getText().trim();
		if (str.length() == 0)
		{
			Dialog.inform("Please enter a valid interval in seconds!");
			interval.setFocus();
			return;
		}

		long value = Utils.getInt(str);
		if(value < Consts.DEFAULT_INTERVAL)
		{
			interval.setText("10");
			Dialog.inform("The minimum interval is 10 seconds!");
			interval.setFocus();
			return;			
		}
		if (value < Consts.DEFAULT_INTERVAL)
			value = Consts.DEFAULT_INTERVAL;

		str = password.getText().trim();

		boolean en = Options.isEnable();
		boolean auto = Options.isAutostart();
		
		Options.setInterval(value);
		Options.setEnable(enable.getChecked());
		Options.setAutostart(autostart.getChecked());
		Options.setProtected(protect.getChecked());	
		Options.setPassword(str);
		Options.save();
		
		if(Options.isAutostart() && auto != Options.isAutostart()) 
			startBackground();
		else if(Options.isEnable() && en != Options.isEnable()) 
			startBackground();
		
		ApplicationManager.getApplicationManager().postGlobalEvent(Consts.GUID_BG, 0, 0, null, null);		

		Dialog.inform("Settings saved!");
	}

	public void startBackground()
	{
    	Log.i(TAG, "startBackground!");
    	
	    ApplicationManager appMan = ApplicationManager.getApplicationManager();
	    ApplicationDescriptor myApp = new ApplicationDescriptor(ApplicationDescriptor.currentApplicationDescriptor(), new String[]{"autostart"});	    
		
	    try
		{
			appMan.runApplication(myApp);
		}
		catch (ApplicationManagerException e)
		{
	    	Log.i(TAG, "startBackground exception: " + e.toString());
		} 
	}

/*	
	public String getNewPassword(String oldpass)
	{
		PasswordDialog dialog;
		int res;
		
		if(oldpass != null && oldpass.length() != 0)
		{
			dialog = new PasswordDialog("Enter your old password:");
			res = dialog.doModal();
			if (res != Dialog.D_OK)
			{
				return null;
			}
			String oldpass2 = dialog.getPassword();
			if(oldpass2.length() == 0 || !oldpass.equals(oldpass2))
			{
				Dialog.inform("Password is not correct!");
				return null;
			}

		}

		dialog = new PasswordDialog("Enter your new password:");
		res = dialog.doModal();
		if (res != Dialog.D_OK)
		{
			return null;
		}
		
		String newpass = dialog.getPassword();
		if(newpass.length() == 0)
		{
			Dialog.inform("Password is not correct!");
			return null;
		}
		
		dialog = new PasswordDialog("Enter your new password again:");
		res = dialog.doModal();
		if (res != Dialog.D_OK)
		{
			return null;
		}
		
		String newpass2 = dialog.getPassword();
		if(newpass2.length() == 0 || !newpass.equals(newpass2))
		{
			Dialog.inform("Password did not match!");
			return null;
		}
	
		return newpass;
	}
*/	
	public void login()
	{
		Options.load();
		String password = Options.getPassword();
		if (Options.isProtected() && password != null && password.length() != 0)
		{
			PasswordDialog dialog = new PasswordDialog("Enter your login password:");
			int res = dialog.doModal();
			if (res == Dialog.D_OK)
			{
				String pass = dialog.getPassword();
				if(pass.equals(password))
				{
					setupUI();
					return;
				}
				else
				{
					Dialog.inform("Invalid password");
				}
			}
			Status.show("Exiting application...");
			close();
		}
		else
		{
			setupUI();
			return;
		}
	}

	public String getLatitude()
	{
		return locLat;
	}

	public String getLongitude()
	{
		return locLon;
	}

	public String getSpeed()
	{
		return locSpd + "km/h";
	}

	public void updateLocation(final Location location)
	{
    	Formatter 	format = new Formatter();    
    	if(location != null && location.isValid())
    	{
			double lat	= location.getQualifiedCoordinates().getLatitude();
			double lon	= location.getQualifiedCoordinates().getLongitude();
			float spd 	= location.getSpeed() * (float)3.6;
			
			if(spd == Float.NaN) spd = 0;
			
			locLat = format.formatNumber(lat, 2);
			locLon = format.formatNumber(lon, 2);
			locSpd = format.formatNumber(spd, 2);
    	}
    	else
    	{
    		locLat = "0";
    		locLon = "0";
    		locSpd = "0";
    	}
    	
		UiApplication.getUiApplication().invokeLater(new Runnable()
		{
			public void run()
			{
				synchronized(Application.getEventLock())
				{
					if(latitude != null)
						latitude.setText(getLatitude());
					if(longitude != null)
						longitude.setText(getLongitude());
					if(speed != null)
						speed.setText(getSpeed());
				}
			}
		});
	}
}
