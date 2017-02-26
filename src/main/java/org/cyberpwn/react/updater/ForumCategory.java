package org.cyberpwn.react.updater;

import java.util.List;

/**
 * Spigot forum category
 * 
 * @author Maxim Van de Wynckel
 */
public interface ForumCategory {
	/**
	 * Get forums inside category
	 * 
	 * @return List of {@link org.cyberpwn.react.updater.Forum}
	 */
	List<Forum> getForums();
}