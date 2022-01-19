package com.miciniti.webgpstracker.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import com.miciniti.webgpstracker.Consts;

public class Options
{
	private static String 	rsName 		= "webgpstracker";

	private static boolean	enable			= false;
	private static boolean	autostart		= false;
	private static boolean	protect			= false;

    private static long		interval 		= 10;
    private static String	password	 	= "";
     
    public Options() 
    {    
    	
    }

    public static boolean isEnable()
	{
		return enable;
	}

	public static boolean isAutostart()
	{
		return autostart;
	}

	public static boolean isProtected()
	{
		return protect;
	}

	public static String getPassword()
	{
		return password;
	}

	public static long getInterval()
	{		
		if(interval <= Consts.DEFAULT_INTERVAL) interval = Consts.DEFAULT_INTERVAL;
		return interval;
	}

	public static void setEnable(boolean en)
	{
		enable = en;
	}

	public static void setAutostart(boolean autostart)
	{
		Options.autostart = autostart;
	}

	public static void setProtected(boolean protect)
	{
		Options.protect = protect;
	}

	public static void setPassword(String password)
	{
		Options.password = password;
	}

	public static void setInterval(long ind)
	{		
		interval = ind;
	}

	public static void fromByteArray( byte[] data ) throws IOException 
	{
	    ByteArrayInputStream bin = new ByteArrayInputStream(data);
	    DataInputStream din = new DataInputStream( bin );
	
	    setEnable 		( din.readBoolean() );
	    setAutostart	( din.readBoolean() );
	    setProtected	( din.readBoolean() );
	    setInterval		( din.readLong());
	    setPassword		( din.readUTF());
	
	    din.close();
	}


	public static byte[] toByteArray() throws IOException 
	{
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    DataOutputStream dout = new DataOutputStream( bout );
	
	    dout.writeBoolean	( isEnable() );
	    dout.writeBoolean	( isAutostart() );
	    dout.writeBoolean	( isProtected() );
	    dout.writeLong		( getInterval() );
	    dout.writeUTF		( getPassword() );

	    dout.close();
	
	    return bout.toByteArray();
	}


	public static void load()
	{
	    int recordID = 1; // the record ID to read from
	    
	    try 
	    {
	        RecordStore rs = RecordStore.openRecordStore( rsName, true );
	        if (rs.getNumRecords() > 0) 
	        {
	            byte[] data = rs.getRecord( recordID );
	            fromByteArray( data );
	        }
	    }
	    catch( RecordStoreException e )
	    {
	        // handle the RMS error here
	    }
	    catch( IOException e )
	    {
	        // handle the IO error here
	    }
	}

	public static void save()
	{
        try 
        {
            RecordStore rs = RecordStore.openRecordStore( rsName, true );
            byte[] data = toByteArray();
            if (rs.getNumRecords() == 0) 
            {
                rs.addRecord( data, 0, data.length );
            } 
            else 
            {
                rs.setRecord( 1, data, 0, data.length );
            }
        }
        catch( RecordStoreException e )
        {
            // handle the RMS error here
        }
        catch( IOException e )
        {
            // handle the IO error here
        }

    }
    
} 
