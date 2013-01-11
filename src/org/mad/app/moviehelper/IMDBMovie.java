package org.mad.app.moviehelper;

/**
 * Represents an IMDBMovie, which is only the id and title
 * @author karthik
 *
 */
public class IMDBMovie {
	private String imdbid;
	private String title;
	
	public IMDBMovie(String id, String title) {
		imdbid = id;
		this.title = title;
	}
	
	/**
	 * @return the imdbid
	 */
	public String getImdbid() {
		return imdbid;
	}

	/**
	 * @param imdbid the imdbid to set
	 */
	public void setImdbid(String imdbid) {
		this.imdbid = imdbid;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public String toString() {
		return imdbid + " " + title;
	}
}
