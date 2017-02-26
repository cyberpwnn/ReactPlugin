package org.cyberpwn.react.updater;

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
	 * @return List of {{@link org.cyberpwn.react.updater.Forum}
	 */
	List<Forum> getSubForums();
}
