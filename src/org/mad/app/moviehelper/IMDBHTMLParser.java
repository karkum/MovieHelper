package org.mad.app.moviehelper;

import java.util.ArrayList;

public class IMDBHTMLParser {
	//TODO we are only looking at the top entry to save time, NEED TO FIXXX
	private static final int NUM_RESULTS = 1;

	public ArrayList<IMDBMovie> parseGenreMovieList(String page) {
		StringBuilder htmlPage = new StringBuilder(page);
		ArrayList<IMDBMovie> movies = new ArrayList<IMDBMovie>();
		try {
			for (int i = 1; i <= NUM_RESULTS; ++i) {
				int index = htmlPage.indexOf(">" + i + ".</td>");
				index = htmlPage.indexOf("/title", index);
				htmlPage.delete(0, index + 7);
				index = htmlPage.indexOf("/");
				String id = htmlPage.substring(0, index);

				index = htmlPage.indexOf("title=");
				htmlPage.delete(0, index + 7);
				index = htmlPage.indexOf("\"><img");
				String title = htmlPage.substring(0, index);

				movies.add(new IMDBMovie(id, title));
			}
		} catch (Exception e) {
			// parsing error, not sure what could go wrong, but just in case
			return null;
		}
		return movies;
	}

}