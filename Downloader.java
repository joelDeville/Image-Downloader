import javafx.concurrent.Task;

import java.net.URL;
import java.net.URLConnection;

import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Downloader extends Task<Void>
{
	//ext for extensions, and main url
	private String url;
	private static String ext;
	
	public Downloader(String url)
	{
		this.url = url;
	}
	
	@Override
	public Void call() throws Exception
	{
		//creating the ext and getting access to internet URL
		ext = url.substring(url.lastIndexOf("."), url.lastIndexOf(".") + 4);
		URLConnection link = new URL(url).openConnection();
		long length = link.getContentLength();
		
		//try with resources which downloads image to local machine
		try (InputStream in = link.getInputStream(); OutputStream out = Files.newOutputStream(Paths.get("DownloadedFile" + ext)))
		{
			byte[] bar = new byte[8192];
			long nread = 0L;
			int n;
			
			while((n = in.read(bar)) > 0)
			{
				out.write(bar, 0, n);
				nread += n;
				this.updateProgress(nread, length);
			}
		}
		
		return null;
	}
	
	public static String getExt()
	{
		return ext;
	}
}