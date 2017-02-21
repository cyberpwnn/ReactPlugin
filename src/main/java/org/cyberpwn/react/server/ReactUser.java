package org.cyberpwn.react.server;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;

public class ReactUser implements Configurable
{
	private String username;
	private String password;
	private boolean canViewConsole;
	private boolean canUseConsole;
	private boolean canUseActions;
	private boolean enabled;
	private ClusterConfig cc;
	
	public ReactUser(String username)
	{
		this.username = username;
		password = "unknown";
		canViewConsole = true;
		canUseActions = false;
		canUseConsole = false;
		enabled = false;
		cc = new ClusterConfig();
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("username", username);
		cc.set("password", password);
		cc.set("permissions.console.view", canViewConsole);
		cc.set("permissions.console.use", canUseConsole);
		cc.set("permissions.actions.use", canUseActions);
		cc.set("enabled", enabled, "Copy this file and make new users.\nMake sure the file name matches the username.\nSet enabled to true to turn on users");
	}
	
	@Override
	public void onReadConfig()
	{
		username = cc.getString("username");
		password = cc.getString("password");
		canViewConsole = cc.getBoolean("permissions.console.view");
		canUseConsole = cc.getBoolean("permissions.console.use");
		canUseActions = cc.getBoolean("permissions.actions.use");
		enabled = cc.getBoolean("enabled");
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return username;
	}
	
	public void reload()
	{
		React.instance().getDataController().load(new File(new File(React.instance().getDataFolder(), "remote-users"), getCodeName() + ".yml"), this);
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public boolean canViewConsole()
	{
		return canViewConsole;
	}
	
	public boolean canUseConsole()
	{
		return canUseConsole;
	}
	
	public boolean canUseActions()
	{
		return canUseActions;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	@Override
	public String toString()
	{
		ClusterConfig ccx = cc.copy();
		ccx.set("password", StringUtils.repeat("*", cc.getString("password").length()));
		return ccx.toYaml().saveToString();
	}
}
