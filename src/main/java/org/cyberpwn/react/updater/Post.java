package org.cyberpwn.react.updater;

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
