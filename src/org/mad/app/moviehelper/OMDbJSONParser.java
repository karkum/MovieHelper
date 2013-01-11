package org.mad.app.moviehelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

/**
 * Simple JSON parser for IMDB Json results
 * @author jared pepin
 *
 */
public class OMDbJSONParser 
{
	public Movie parseMovieInfo(JSONObject json)
	{
		Movie info = new Movie();
		try 
		{
			info.setActors(json.getString("Actors"));
			info.setDirector(json.getString("Director"));
			info.setTitle(json.getString("Title"));
			info.setGenre(json.getString("Genre"));
			info.setId(json.getString("imdbID"));
			info.setVotes(json.getString("imdbVotes"));
			info.setPlot(json.getString("Plot"));
			String iconlink = json.getString("Poster");
			info.setPoster(iconlink);
			Bitmap icon = OMDbConnection.getImage(iconlink);
			info.setIcon(icon);
			info.setReleased(json.getString("Released"));
			info.setYear(json.getString("Year"));
			info.setRating(json.getString("imdbRating"));
			info.setWriters(json.getString("Writer"));
			info.setRuntime(json.getString("Runtime"));
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return info;
	}

	public String parseMovieSearch(JSONObject jSONResult) {
		String id = "";
		try {
			JSONArray arr = jSONResult.getJSONArray("Search");
			id = arr.getJSONObject(0).getString("imdbID");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	
}