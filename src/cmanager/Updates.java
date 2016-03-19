package cmanager;

public class Updates 
{

	private static Boolean updateAvailable = null;
	
	public static synchronized boolean updateAvailable_block()
	{
		if( updateAvailable == null )
		{
			try
			{
				String url = "https://github.com/RoffelKartoffel/cmanager/releases.atom";
				String http = HTTP.get( url );
				
				XMLElement root = XMLParser.parse(http);

				XMLElement child = root.getChild("feed").getChild("entry").getChild("title");
				String version = child.getUnescapedBody();
				
				updateAvailable = version.equals(Version.VERSION);
			}
			catch(Throwable t){
				// Errors might be due to missing internet connection.
				updateAvailable = false;
			}
		}
		
		return updateAvailable;
	}
}
