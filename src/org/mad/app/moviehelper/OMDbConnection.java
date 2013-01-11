package org.mad.app.moviehelper;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Represents a connection to OMDB. 
 * @author karthik
 *
 */
@SuppressWarnings("unused")
public class OMDbConnection {
	private URL url = null;
	private HttpURLConnection conn;
	private BufferedReader buffer;
	private OMDbJSONParser parser = new OMDbJSONParser();
	private final String PREFIX = "http://www.omdbapi.com/?i=";
	//private final String SUFFIX = "&tomatoes=true&plot=short";//tooslow
	String line;
	static Context context;
	
	public OMDbConnection(Context cntxt) {
		context = cntxt;
	}
	
	public Movie getMovieInfo(String movieId)
	{
		Movie result = null;
		String request = PREFIX + movieId;// + SUFFIX;
		GetTask task = new GetTask("Getting videos...");
		task.execute(request);
		JSONObject JSONResult = null;
		try {
			JSONResult = new JSONObject(task.get(15, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			JSONResult = null;
			e.printStackTrace();
		} catch (ExecutionException e) {
			JSONResult = null;
			e.printStackTrace();
		} catch (TimeoutException e) {
			JSONResult = null;
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (JSONResult != null) {
			result = parser.parseMovieInfo(JSONResult);
		} else {
			return null;
		}
		return result;
	}
	public static Bitmap getImage(String imageLink) {
		GetImageTask task = new GetImageTask("Getting image...");
		task.execute(imageLink);
		Bitmap result = null;
		try {
			result = (Bitmap) task.get(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			result = null;
			e.printStackTrace();
		} catch (ExecutionException e) {
			result = null;
			e.printStackTrace();
		} catch (TimeoutException e) {
			result = null;
			e.printStackTrace();
		}

		return result;
	}
	public Movie searchMovie(String movie) {
		Movie result = null;
		String request = "http://www.omdbapi.com/?s=" + movie + "&r=JSON";
		request = request.replace(" ", "%20");
		GetTask task = new GetTask("Searching movies...");
		task.execute(request);
		JSONObject JSONResult = null;
		try {
			JSONResult = new JSONObject(task.get(15, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			JSONResult = null;
			e.printStackTrace();
		} catch (ExecutionException e) {
			JSONResult = null;
			e.printStackTrace();
		} catch (TimeoutException e) {
			JSONResult = null;
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (JSONResult != null) {
			String id = parser.parseMovieSearch(JSONResult);
			result = getMovieInfo(id);
		} else {
			return null;
		}
		return result;
	}
	
	private class GetTask extends AsyncTask <String, String, String>
	{
		private String task;
		public GetTask(String t) {
			task = t;
		}
		private String get(final String request) {
			String JSONResult = "";
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
				JSONResult = buff.toString();
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
			return JSONResult;
		}
		protected String doInBackground(String... arg0) {
			return get((String)arg0[0]);
		}
	}
	private static class GetImageTask extends AsyncTask<String, String, Bitmap> {
		private String task;

		public GetImageTask(String t) {
			task = t;
		}

		private Bitmap get(final String link) {
			Bitmap unknown = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.unknown);
			Bitmap bm = null;
			URL url;
			try {
				url = new URL(link);

				URLConnection conn = url.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();

				BufferedInputStream bis = new BufferedInputStream(is);

				bm = BitmapFactory.decodeStream(bis);

				bis.close();
				is.close();
			} catch (MalformedURLException e) {
				bm = unknown;
			} catch (IOException e) {
				bm = unknown;
			}
			return bm;
		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			return get((String) arg0[0]);
		}
	}
}