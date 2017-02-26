package org.cyberpwn.react.updater;

public class SpigotResourceRating implements Rating {
	private int rating = 0;
	private User user = null;

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public User getAuthor() {
		return user;
	}

	public void setAuthor(User user) {
		this.user = user;
	}

}
