package org.cyberpwn.react.updater.resource;

import java.io.File;
import java.util.List;
import org.cyberpwn.react.updater.user.User;

/**
 * Spigot Resource
 * 
 * @author Maxim Van de Wynckel
 */
public interface Resource {
	/**
	 * Get resource identifier
	 * 
	 * @return Resource identifier
	 */
	public int getResourceId();

	/**
	 * Get resource name
	 * 
	 * @return Resource name
	 */
	public String getResourceName();

	/**
	 * Set resource name
	 * 
	 * @param name
	 *            Resource name
	 */
	public void setResourceName(String name);

	/**
	 * Get last resource version
	 * 
	 * @return Resource version
	 */
	public String getLastVersion();

	/**
	 * Set last resource version
	 * 
	 * @param version
	 *            Resource version
	 */
	public void setLastVersion(String version);

	/**
	 * Get spigot author of resource
	 * 
	 * @return Spigot User
	 */
	public User getAuthor();

	/**
	 * Get resource category
	 * 
	 * @return {@link org.cyberpwn.react.updater.resource.ResourceCategory}
	 */
	public ResourceCategory getResourceCategory();

	/**
	 * Get resource download URL
	 * 
	 * @return Download URL
	 */
	public String getDownloadURL();

	/**
	 * Download the resource
	 * 
	 * @param output
	 *            Download URL
	 * @return Downloaded file
	 */
	public File downloadResource(User user, File output);

	/**
	 * Is the resource deleted
	 * 
	 * @return Deleted resource
	 */
	public boolean isDeleted();

	/**
	 * Get resource average rating
	 * 
	 * @return Resource rating
	 */
	public int getAverageRating();

	/**
	 * Get resource ratings
	 * 
	 * @return List of {@link org.cyberpwn.react.updater.resource.Rating}
	 */
	public List<Rating> getRatings();

	/**
	 * Get rersource updates
	 * 
	 * @return List of
	 *         {@link org.cyberpwn.react.updater.resource.ResourceUpdate}
	 */
	public List<ResourceUpdate> getResourceUpdates();
}
