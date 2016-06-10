package org.cyberpwn.react.util;

import java.util.UUID;

import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;

public class PlayerData implements Configurable
{
	private ClusterConfig cc;
	private UUID uuid;
	private Integer monitoringTab;
	private Boolean monitoring;
	private Boolean lockedTab;
	private Boolean mapping;
	
	public PlayerData(UUID uuid)
	{
		this.cc = new ClusterConfig();
		this.uuid = uuid;
		this.monitoringTab = -1;
		this.monitoring = false;
		this.lockedTab = false;
		this.mapping = false;
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("monitor.uuid", uuid.toString());
		cc.set("monitor.options.monitoring.enabled", monitoring);
		cc.set("monitor.options.monitoring.current", monitoringTab);
		cc.set("monitor.options.monitoring.locked", lockedTab);
		cc.set("monitor.options.mapping.enabled", mapping);
	}
	
	@Override
	public void onReadConfig()
	{
		uuid = UUID.fromString(cc.getString("monitor.uuid"));
		monitoringTab = cc.getInt("monitor.options.monitoring.current");
		monitoring = cc.getBoolean("monitor.options.monitoring.enabled");
		lockedTab = cc.getBoolean("monitor.options.monitoring.locked");
		mapping = cc.getBoolean("monitor.options.mapping.enabled");
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return uuid.toString();
	}
	
	public Integer getMonitoringTab()
	{
		return monitoringTab;
	}
	
	public void setMonitoringTab(Integer monitoringTab)
	{
		this.monitoringTab = monitoringTab;
	}
	
	public Boolean isMonitoring()
	{
		return monitoring;
	}
	
	public void setMonitoring(Boolean monitoring)
	{
		this.monitoring = monitoring;
	}
	
	public Boolean isLockedTab()
	{
		return lockedTab;
	}
	
	public void setLockedTab(Boolean lockedTab)
	{
		this.lockedTab = lockedTab;
	}
	
	public UUID getUuid()
	{
		return uuid;
	}
	
	public Boolean isMapping()
	{
		return mapping;
	}
	
	public void setMapping(Boolean mapping)
	{
		this.mapping = mapping;
	}
}
