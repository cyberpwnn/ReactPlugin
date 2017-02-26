package org.cyberpwn.react.updater;

import java.util.HashMap;
import java.util.Map;

public class SpigotSiteCore implements SpigotSiteAPI {
	/* Spigot User Manager */
	private UserManager userManager = null;
	/* Spigot Resource Manager */
	private ResourceManager resourceManager = null;
	/* Spigot Forum Manager */
	private ForumManager forumManager = null;
	/* Spigot Conversation Manager */
	private ConversationManager conversationManager = null;
	private boolean ddosProtection = false;
	private static Map<String, String> baseCookies = new HashMap<String, String>();
	private static boolean firstStart = true;
	private static String baseURL = "https://www.spigotmc.org/";
	private static int rateLimitTimeout = 2000;

	public SpigotSiteCore(Map<String,String> baseCookies) {
        // Set managers
        userManager = new SpigotUserManager();
        resourceManager = new SpigotResourceManager();
        forumManager = new SpigotForumManager();
        conversationManager = new SpigotConversationManager();

        // Initialize webclient
        HTTPUnitRequest.initialize();

        // Set Site API
        SpigotSite.setAPI(this);

        if (firstStart) {
            if (baseCookies == null) {
                HTTPResponse res = Request.get("https://www.spigotmc.org/",
                        getBaseCookies(), new HashMap<String, String>());
                setBaseCookies(res.getCookies());
            }else{
                setBaseCookies(baseCookies);
            }
            firstStart = false;
            Request.setDdosBypass(false);
        }
	}

	public SpigotSiteCore() {
	    this(null);
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public ForumManager getForumManager() {
		return forumManager;
	}

	public void setForumManager(ForumManager forumManager) {
		this.forumManager = forumManager;
	}

	public static Map<String, String> getBaseCookies() {
		return baseCookies;
	}

	public static void setBaseCookies(Map<String, String> baseCookies) {
		SpigotSiteCore.baseCookies = baseCookies;
	}

	public ConversationManager getConversationManager() {
		return conversationManager;
	}

	public boolean isDDoSProtection() {
		return ddosProtection;
	}

	public void setDDoSProtection(boolean ddosProtection) {
		this.ddosProtection = ddosProtection;
	}

	public static String getBaseURL() {
		return baseURL;
	}

	public static void setBaseURL(String baseURL) {
		SpigotSiteCore.baseURL = baseURL;
	}

	public static int getRateLimitTimeout() {
		return rateLimitTimeout;
	}

	public static void setRateLimitTimeout(int rateLimitTimeout) {
		SpigotSiteCore.rateLimitTimeout = rateLimitTimeout;
	}
}
