package org.cyberpwn.react.util;

public enum InstabilityCause
{
	CHUNK_GEN("Mass Chunk Gen", "The server is generating a lot of chunks, causing the server to lag, and even spike.", "Create a world border, or pregenerate your world within a border to optimize chunk loading. Take a look at the plugin World Border."), FIRE("Fire Lag", "The server is struggling to handle all of the fire catching and burning on your server. This can cause decent lag.", "Consider configuring react to cull excess fire, or extinguish it yourself manually (view details)"), WORLD_BORDER(false, "World Border", "The server is lagging from world border. However, since this is typically important, react will not interfere. No reactions will be called when world border FILL is running.", "Ensure no one is online while this is running."), ENTITIES(false, "Entity Overload", "It appears there is a large amount of entities on the server. This can cause the server to consume more memory, and lag the server for several reasons.", "Consider adjusting the react configuration to cull excess entities, or manually purge them yourself. (view)"), PLUGIN(false, "Plugin Blocker", "A Plugin appears to be freezing the server, or causing a lot of lag/spikes. View the details.", "Consider adjusting that plugin's config, or simply removing it if you don't need it, or if there is a better plugin for it's purpose."), REDSTONE("Redstone Overload", "The server is struggling to handle all of the redstone calculations that are active right now.", "Consider adjusting the react config to prevent this from causing lag, or consider preventing players from using redstone clocks."), LIQUID("Liquid Overload", "The server is using a lot of resources to handle liquid flow. This can be caused by someone placing a lot of liquid, or world editing massive areas intending for liquid to flow long distances.", "Consider adjusting the react configuration to crack down on liquid flowing, or prevent players from using this in world edit."), TNT_EXPLOSIONS("TNT Lag", "The server is consuming a lot of memory for explosions from tnt. The server may also be lagging from tnt also.", "To fix this, you may want to change some of the react configuration to crack down on massive amounts of tnt exploading at once."), CHUNKS("Chunk Lag", "The server has too many chunks loaded for the server memory to handle. This can be caused by too many players, or a plugin preventing a relative massive amount of chunks from unloading.", "Consider upgrading your memory, however if this is not possible, try unloading worlds, or removing any plugins that prevent a massive amount of chunk unloads."), MEMORY("Low Memory", "The server is consuming too much memory very often. This causes the server to look for garbage more often than normal. Upgrading your memory would be advised.", "You can upgrade your memory, however if this is not an option, consider unloading/removing worlds, removing plugins that are not needed, or even decreasing the player limit."), PHYSICS("Physics Overload", "The server is struggling to handle a lot of pistons and falling blocks (sand/gravel)", "Consider configuring react to handle this, or check the details, and manually remove it."), LAG("Unknown Lag Source", "The server is struggling to handle something. React is trying several things, as it is unclear for a source.", "Possibly someone might know? Possibly another program or service is running on the host that is degrading tps?"), PLAYERS(false, "Player Overload", "The server is struggling to calculate the amount of players online on average. This usually has nothing to do with lag from anything else.", "If you have a lot of players, it may be a good idea to distribute them across multiple servers on a hub network. Look online for 'Spigot Bungeecord'.");
	
	private final String name;
	private final String problem;
	private final String fix;
	private final boolean talkative;
	
	private InstabilityCause(String name, String problem, String fix)
	{
		this.name = name;
		this.problem = problem;
		this.fix = fix;
		this.talkative = true;
	}
	
	private InstabilityCause(boolean tktk, String name, String problem, String fix)
	{
		this.name = name;
		this.problem = problem;
		this.fix = fix;
		this.talkative = tktk;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getProblem()
	{
		return problem;
	}
	
	public String getFix()
	{
		return fix;
	}
	
	public boolean isTalkative()
	{
		return talkative;
	}
}
