package com.mobbeel.mobbscan.simple.tools;

import android.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MorphoTools
{
	private static final Map<Pair<Integer, Integer>, String> supportedDevices	= new HashMap<Pair<Integer, Integer>, String>();
	public static final String		SOFTWAREID_MSO100		= "MSO100";
	public static final String		SOFTWAREID_MSO300		= "MSO300";
	public static final String		SOFTWAREID_MSO350		= "MSO350";

	public static final String		SOFTWAREID_CBM			= "CBM";
	public static final String		SOFTWAREID_MSO1350		= "MSO1350";

	public static final String		SOFTWAREID_FVP			= "MSO FVP";
	public static final String		SOFTWAREID_FVP_C		= "MSO FVP_C";
	public static final String		SOFTWAREID_FVP_CL		= "MSO FVP_CL";
	public static final String		SOFTWAREID_MEP			= "MEPUSB";

	public static final String		SOFTWAREID_CBME3		= "CBM-E3" ;
	public static final String		SOFTWAREID_CBMV3		= "CBM-V3"	;
	public static final String		SOFTWAREID_MSO1300E3	= "MSO1300-E3";
	public static final String		SOFTWAREID_MSO1300V3	= "MSO1300-V3";
	public static final String		SOFTWAREID_MSO1350E3	= "MSO1350-E3";
	public static final String		SOFTWAREID_MSO1350V3	= "MSO1350-V3";

	public static final String		SOFTWAREID_MASIGMA		= "MA SIGMA";

	static
	{
		supportedDevices.put(new Pair<Integer, Integer>(0x079b, 0x0023), SOFTWAREID_MSO100);
		supportedDevices.put(new Pair<Integer, Integer>(0x079b, 0x0024), SOFTWAREID_MSO300);
		supportedDevices.put(new Pair<Integer, Integer>(0x079b, 0x0026), SOFTWAREID_MSO350);

		supportedDevices.put(new Pair<Integer, Integer>(0x079b, 0x0047), SOFTWAREID_CBM);
		supportedDevices.put(new Pair<Integer, Integer>(0x079b, 0x0052), SOFTWAREID_MSO1350);
		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x0001), SOFTWAREID_FVP);
		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x0002), SOFTWAREID_FVP_C);
		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x0003), SOFTWAREID_FVP_CL);
		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x0007), SOFTWAREID_MEP);

		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x0008), SOFTWAREID_CBME3);
		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x0009), SOFTWAREID_CBMV3);
		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x000A), SOFTWAREID_MSO1300E3);
		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x000B), SOFTWAREID_MSO1300V3);
		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x000C), SOFTWAREID_MSO1350E3);
		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x000D), SOFTWAREID_MSO1350V3);
		supportedDevices.put(new Pair<Integer, Integer>(0x225D, 0x000E), SOFTWAREID_MASIGMA);
	}

	public static synchronized boolean isSupported(int vid, int pid)
	{
		for (Pair<Integer, Integer> supportedAttribs : supportedDevices.keySet())
		{
			//Log.i("MORPHO_USB", "Supported device : Vendor Id = " + supportedAttribs.getVendorId() + ", product Id = " + supportedAttribs.getProductId());
			//Log.i("MORPHO_USB", "Vendor Id = " + attribs.getVendorId() + ", product Id = " + attribs.getProductId());
			if (supportedAttribs.first == vid && supportedAttribs.second == pid)
			{
				return true;
			}
		}

		return false;
	}

	public static ByteArrayOutputStream ReadFile(File file) throws IOException
	{

		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try
		{
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(file);
			int read = 0;
			while ((read = ios.read(buffer)) != -1)
			{
				ous.write(buffer, 0, read);
			}
		}
		finally
		{
			try
			{
				if (ous != null)
					ous.close();
			}
			catch (IOException e)
			{
			}

			try
			{
				if (ios != null)
					ios.close();
			}
			catch (IOException e)
			{
			}
		}
		return ous;
	}	
	
	public static String checkfield(String field, boolean isUpdateTemplate)
	{
		if(isUpdateTemplate)
		{
			return field;
		}
		else
		{		
			if (field.equalsIgnoreCase(""))
			{
				return "<None>";
			}
			else
			{	
				return field;
			}
		}
	}
}
