package org.cyberpwn.react.updater.forum;

import java.util.List;

/**
 * Spigot forum
 * 
 * @author Maxim Van de Wynckel
 */
public interface Forum {
	/**
	 * Get sub forums
	 * 
	 * @return List of {{@link org.cyberpwn.react.updater.forum.Forum}
	 */
	List<Forum> getSubForums();
}
