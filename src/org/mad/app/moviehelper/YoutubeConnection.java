package org.mad.app.moviehelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.os.AsyncTask;

/**
 * An API for getting information about trailers
 * 
 * @author karthik
 * 
 */
public class YoutubeConnection {

	private URL url = null;
	@SuppressWarnings("unused")
	private HttpURLConnection conn;
	private BufferedReader buffer;
	private YoutubeXMLParser parser = new YoutubeXMLParser();
	private final String PREFIX = "http://gdata.youtube.com/feeds/api/videos?max-results=10&format=1&q=";
	String line;
	@SuppressWarnings("unused")
	private Context mContext;
	public YoutubeConnection (Context cont) {
		mContext = cont;
	}
	public ArrayList<Trailer> getVideo(String movieTitle) {
		ArrayList<Trailer> result = new ArrayList<Trailer>();
		String trailerTitle = movieTitle + " Trailer";
		String request = PREFIX + trailerTitle.replace(" ", "%20");
		GetTask task = new GetTask("Getting videos...");
		task.execute(request);
		String XMLResult;
		try {
			XMLResult = (String) task.get(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			XMLResult = null;
			e.printStackTrace();
		} catch (ExecutionException e) {
			XMLResult = null;
			e.printStackTrace();
		} catch (TimeoutException e) {
			XMLResult = null;
			e.printStackTrace();
		}
		if (XMLResult != null) {
			result = parser.parseVideo(XMLResult);
		} else {
			return null;
		}
		//for (Bus_Bus b : result) {
			//    System.out.println(b);
			//}
		return result;
	}

	/**
	 * Task that perform the network transaction
	 * @author karthik
	 *
	 */
	@SuppressWarnings({ })
	private class GetTask extends AsyncTask <String, String, String>
	{
		@SuppressWarnings("unused")
		private String task;
		public GetTask(String t) {
			task = t;
		}
		private String get(final String request) {
			String XMLResult = "";
			try {
				StringBuffer buff = new StringBuffer();
				url = new URL(request);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				buffer = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				while ((line = buffer.readLine()) != null) {
					buff.append(line);
				}
				XMLResult = buff.toString();
				buffer.close();
			} catch (MalformedURLException e) {
				System.out.println("BAD URL");
				e.printStackTrace();
			} catch (ProtocolException e) {
				System.out.println("Could not connect");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Could not read");
				e.printStackTrace();
			}
			return XMLResult;
		}
		@Override
		protected String doInBackground(String... arg0) {
			return get((String)arg0[0]);
		}
	}
}
