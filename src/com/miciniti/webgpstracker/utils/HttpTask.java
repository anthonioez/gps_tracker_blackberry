package com.miciniti.webgpstracker.utils;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.WLANInfo;

public class HttpTask extends Thread
{
	public static final String		TAG						= "Http";	

	public static final boolean		USE_MDS_IN_SIMULATOR	= false;
	public static final int 		LOCATION_RETENTION		= 10000;
	
	public static final int		CON_GSM		= 0;
	public static final int		CON_BIS		= 1;
	public static final int		CON_WIFI	= 2;

	public String 	carrierUid	= "";
	public String 	url;
	public boolean	running 	= false;
	
	public HttpTask(String url)
	{		
		this.url = url;
		carrierUid = getCarrierBIBSUid();
	}
		
	private String getConnectionString()
	{
	    String connectionString = "";                
	                    
		int connection = CON_GSM;	    	
		if((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_BIS_B) == CoverageInfo.COVERAGE_BIS_B && getCarrierBIBSUid() != null)
	    {
	    	connection = CON_BIS;
	    }
		else if((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_DIRECT) == CoverageInfo.COVERAGE_DIRECT)
	    {
	    	connection = CON_GSM;	    	
	    }
		else if(WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED)
	    {
	    	connection = CON_WIFI;	    		    	
	    }
	
	    if(DeviceInfo.isSimulator())
	    {
	        if(USE_MDS_IN_SIMULATOR)
	        {
	            connectionString = ";deviceside=false";                 
	        }
	        else
	        {
	            connectionString = ";deviceside=true";
	        }
	    }                               
	    else if(connection == CON_WIFI)
	    {                
	    	Log.i(TAG, "Device is connecting via Wifi.");
	        connectionString = ";interface=wifi";
	    }
	    else if(connection == CON_BIS)
	    {                            
	        if(carrierUid != null) 
	        {
	        	Log.i(TAG, "uid is: " + carrierUid);
	            connectionString = ";deviceside=false;connectionUID="+carrierUid + ";ConnectionType=mds-public";
	        }
	        else 
	        {
	        	Log.i(TAG, "No Uid");
	            connectionString = ";deviceside=true";
	        }
	    }        
	    else if(connection == CON_GSM)
	    {                            
	        connectionString = ";deviceside=true";
	    }
	    else
	    {
	    	connectionString = "";
	    }
	
	    return connectionString;
	}

	private String getCarrierBIBSUid()
	{
	    ServiceRecord[] records = ServiceBook.getSB().getRecords();
	    int currentRecord;
	    
	    for(currentRecord = 0; currentRecord < records.length; currentRecord++)
	    {
	        if(records[currentRecord].getCid().toLowerCase().equals("ippp"))
	        {
	            if(records[currentRecord].getName().toLowerCase().indexOf("bibs") >= 0)
	            {
	                return records[currentRecord].getUid();
	            }
	        }
	    }
	    
	    return null;
	}

	public void run()
	{
		int i = 0;
		
		running  = true;
		try 
		{
			while(i++ < 3 && running)
			{
				boolean ret = load();
				if(ret) 
				{
					break;
				}
				Thread.sleep(500);
			}   			
		} 
		catch (InterruptedException e) 
		{
		}			

		running = false;
	}
	
	public boolean load()
	{
		HttpConnection httpConn = null;
		boolean ret = false;
		try
		{
	        String constring = getConnectionString();
	        if(constring == null) constring = "";

	        String urlstring = url + constring;
	        
	        System.err.println("Url: " + urlstring);
	        
	        httpConn = (HttpConnection) Connector.open(urlstring);
			httpConn.setRequestMethod(HttpConnection.GET);
			
			// Set HTTP values
			httpConn.setRequestProperty("Connection", "close");
			httpConn.setRequestProperty("Content-Length", "0");
			
			// Make the request (e.g. send the data)
			if(httpConn.getResponseCode() == HttpConnection.HTTP_OK)
			{
				ret = true;
			}
			else
			{
				ret = false;
			}
		}
		catch (IOException e)
		{
		}
		catch(Exception e)
		{
		}
		finally
		{
			try
			{
				if(httpConn != null) httpConn.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		
		}
		return ret;
	}
}
