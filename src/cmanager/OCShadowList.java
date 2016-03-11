package cmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

import org.joda.time.DateTime;

import cmanager.FileHelper.InputAction;

public class OCShadowList
{
	private final static String SHADOWLIST_FOLDER = Main.CACHE_FOLDER + "OC.shadowlist";
	private final static String SHADOWLIST_PATH = SHADOWLIST_FOLDER + "/gc2oc.gz"; 

	public static void updateShadowList() throws IOException
	{
		// delete list if it is older than 1 month
		File file = new File(SHADOWLIST_PATH);
		if( file.exists() )
		{
			DateTime fileTime = new DateTime( file.lastModified() );
			DateTime now = new DateTime();
			fileTime = fileTime.plusMonths( 1 );
			if( fileTime.isAfter( now ) )
				return;
				
			file.delete();
		}
		
		new File(SHADOWLIST_FOLDER).mkdirs();
		
		// download list
		URL url = new URL("https://www.opencaching.de/api/gc2oc.php");
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(SHADOWLIST_PATH);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}

	public static OCShadowList loadShadowList() throws Throwable
	{
		final HashMap<String, String> shadowList = new HashMap<>();
		FileHelper.processFiles(SHADOWLIST_PATH, new InputAction() 
		{
			@Override
			public void process(InputStream is) throws Throwable 
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while((line = br.readLine()) != null) 
				{
					String token[] = line.split(",");
					// Column 2 == "1" means verified by a human
					if( token[2].equals("1") )
					{
						// <GC, OC>
						shadowList.put(token[0], token[1]);
					}
				}
			}
		});
		return new OCShadowList(shadowList);
	}
	
	
	//
	//	Member functions
	//
	
	private HashMap<String, String> shadowList;
	
	private OCShadowList(HashMap<String, String> shadowList)
	{
		this.shadowList = shadowList;
	}
	
	public String getMatchingOCCode(String gcCode)
	{
		return shadowList.get(gcCode);
	}
}
