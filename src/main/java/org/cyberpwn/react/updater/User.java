package org.cyberpwn.react.updater;

import java.util.List;

/**
 * Spigot User
 *
 * @author Maxim Van de Wynckel
 */
public interface User {
    /**
     * Get spigot user identifier
     *
     * @return Spigot user identifier
     */
    int getUserId();

    /**
     * Get spigot username
     *
     * @return Spigot username
     */
    String getUsername();

    /**
     * Determine if the spigot user is authenticated and can be used to perform
     * private actions.
     *
     * @return is Authenticated
     */
    boolean isAuthenticated();

    /**
     * Get purchased resources
     *
     * @return List of {@link org.cyberpwn.react.updater.Resource}
     * @throws ConnectionFailedException internet connection failed
     */
    List<Resource> getPurchasedResources()
            throws ConnectionFailedException;

    /**
     * Get created resources
     *
     * @return List of {@link org.cyberpwn.react.updater.Resource}
     */
    List<Resource> getCreatedResources() throws ConnectionFailedException;

    /**
     * Get spigot user statistics
     *
     * @return {@link org.cyberpwn.react.updater.UserStatistics}
     */
    UserStatistics getUserStatistics() throws ConnectionFailedException;

    /**
     * Get spigot user private conversations
     *
     * @return List of conversation
     */
    List<Conversation> getConversations() throws ConnectionFailedException;

    /**
     * Get users last activity
     *
     * @return Last activity string
     */
    String getLastActivity();

    /**
     * Check if the user has two factory authentication
     *
     * @return two factor auth
     */
    boolean hasTwoFactorAuthentication();
}
