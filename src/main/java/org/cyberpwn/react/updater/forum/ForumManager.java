package org.cyberpwn.react.updater.forum;

import org.cyberpwn.react.updater.ConnectionFailedException;

/**
 * Spigot forum manager
 *
 * @author Maxim Van de Wynckel
 */
public interface ForumManager {
    /**
     * Get forum by identifier
     *
     * @param id Forum id
     * @return {@link org.cyberpwn.react.updater.forum.Forum}
     * @throws ConnectionFailedException Connection to Spigot failed
     */
    Forum getForumById(int id) throws ConnectionFailedException;
}
