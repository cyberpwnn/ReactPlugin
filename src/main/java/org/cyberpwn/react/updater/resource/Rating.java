package org.cyberpwn.react.updater.resource;

import org.cyberpwn.react.updater.user.User;

/**
 * Spigot resource rating
 * 
 * @author Maxim Van de Wynckel
 */
public interface Rating {
	/**
	 * Get resource rating
	 * 
	 * @return Integer between 0 and 5
	 */
	int getRating();

	/**
	 * Get rating author
	 * 
	 * @return Spigot user
	 */
	User getAuthor();
}
