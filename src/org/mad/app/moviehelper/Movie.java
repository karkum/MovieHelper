package org.mad.app.moviehelper;

import android.graphics.Bitmap;
/**
 * Represents a real movie object, with all the info we get from OMDb
 * @author karthik
 *
 */
public class Movie
{
	String title, year, director, writers, actors, plot, poster, runtime, rating, votes, genre, released, id;
	Bitmap icon;
	
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




	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}




	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}




	/**
	 * @return the director
	 */
	public String getDirector() {
		return director;
	}




	/**
	 * @param director the director to set
	 */
	public void setDirector(String director) {
		this.director = director;
	}




	/**
	 * @return the writers
	 */
	public String getWriters() {
		return writers;
	}




	/**
	 * @param writers the writers to set
	 */
	public void setWriters(String writers) {
		this.writers = writers;
	}




	/**
	 * @return the actors
	 */
	public String getActors() {
		return actors;
	}




	/**
	 * @param actors the actors to set
	 */
	public void setActors(String actors) {
		this.actors = actors;
	}




	/**
	 * @return the plot
	 */
	public String getPlot() {
		return plot;
	}




	/**
	 * @param plot the plot to set
	 */
	public void setPlot(String plot) {
		this.plot = plot;
	}




	/**
	 * @return the poster
	 */
	public String getPoster() {
		return poster;
	}




	/**
	 * @param poster the poster to set
	 */
	public void setPoster(String poster) {
		this.poster = poster;
	}




	/**
	 * @return the runtime
	 */
	public String getRuntime() {
		return runtime;
	}




	/**
	 * @param runtime the runtime to set
	 */
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}




	/**
	 * @return the rating
	 */
	public String getRating() {
		return rating;
	}




	/**
	 * @param rating the rating to set
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}




	/**
	 * @return the votes
	 */
	public String getVotes() {
		return votes;
	}




	/**
	 * @param votes the votes to set
	 */
	public void setVotes(String votes) {
		this.votes = votes;
	}




	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}




	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}




	/**
	 * @return the released
	 */
	public String getReleased() {
		return released;
	}




	/**
	 * @param released the released to set
	 */
	public void setReleased(String released) {
		this.released = released;
	}




	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}




	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}




	/**
	 * @return the icon
	 */
	public Bitmap getIcon() {
		return icon;
	}




	/**
	 * @param icon the icon to set
	 */
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}




	public String toString()
	{
		return String
				.format("Title: %s\nDirector: %s\nWriter: %s\nActors: %s\nPlot: %s\nRuntime: %s\nRating: %s\nVotes: %s\nGenre: %s\nReleased: %s\nID: %s\n", 
						title, director, writers, actors, plot, runtime, rating, votes, genre, released, id);
	}
	
	
	
}