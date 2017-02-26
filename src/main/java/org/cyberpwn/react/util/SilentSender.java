package org.cyberpwn.react.util;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class SilentSender implements CommandSender
{
	public SilentSender()
	{
		
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin arg0)
	{
		return null;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1)
	{
		return null;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2)
	{
		return null;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3)
	{
		return null;
	}
	
	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions()
	{
		return Bukkit.getConsoleSender().getEffectivePermissions();
	}
	
	@Override
	public boolean hasPermission(String arg0)
	{
		return true;
	}
	
	@Override
	public boolean hasPermission(Permission arg0)
	{
		return true;
	}
	
	@Override
	public boolean isPermissionSet(String arg0)
	{
		return true;
	}
	
	@Override
	public boolean isPermissionSet(Permission arg0)
	{
		return true;
	}
	
	@Override
	public void recalculatePermissions()
	{
		
	}
	
	@Override
	public void removeAttachment(PermissionAttachment arg0)
	{
		
	}
	
	@Override
	public boolean isOp()
	{
		return true;
	}
	
	@Override
	public void setOp(boolean arg0)
	{
		
	}
	
	@Override
	public String getName()
	{
		return "React";
	}
	
	@Override
	public Server getServer()
	{
		return Bukkit.getServer();
	}
	
	@Override
	public void sendMessage(String arg0)
	{
		
	}
	
	@Override
	public void sendMessage(String[] arg0)
	{
		for(String i : arg0)
		{
			sendMessage(i);
		}
	}
	
}
