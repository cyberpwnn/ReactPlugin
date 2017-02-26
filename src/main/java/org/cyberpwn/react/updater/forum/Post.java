package org.cyberpwn.react.updater.forum;

import org.cyberpwn.react.updater.user.User;

/**
 * Forum thread reply
 *
 * @author Maxim Van de Wynckel
 */
public interface Post {
    /**
     * Get the author of the reply
     *
     * @return Spigot User
     */
    User getAuthor();
}
