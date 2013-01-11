package org.mad.app.moviehelper;

public class Trailer implements Comparable<Trailer>{
	private String title, link, category, id;
	private int duration, viewCount;

	public Trailer(String title, String link, int duration, int viewCount,
			String category, String id) {
		this.title = title;
		this.setId(id);
		this.link = link;
		this.duration = duration;
		this.category = category;
		this.viewCount = viewCount;
	}

	private int calculateScore() {
		int score = 0;
		if (category.equals("Movies"))
			score += 3;
		if (duration > 60 && duration < 501)
			score += 1;
		if (title.toLowerCase().contains("official trailer"))
			score += 5;
		return score;
	}

	public String toString() {
		return String
				.format("Title: %s\nlink: %s\nduration: %s\ncategory: %s\nviewcount: %d",
						title, link, duration, category, viewCount);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String id) {
		this.title = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int compareTo(Trailer other) {
		return other.calculateScore() - this.calculateScore();
	}

}
