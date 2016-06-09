package org.cyberpwn.react.object;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class MaterialList
{
	private GList<Material> materials;
	
	public MaterialList()
	{
		materials = new GList<Material>();
	}
	
	public MaterialList(Material... materials)
	{
		this();
		this.materials.add(materials);
		this.materials.removeDuplicates();
	}
	
	public MaterialList(GList<String> strings)
	{
		this();
		
		for(String i : strings)
		{
			try
			{
				Material m = Material.valueOf(i);
				
				if(m != null)
				{
					materials.add(m);
				}
			}
			
			catch(Exception e)
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Invalid Item Data: " + i);
			}
		}
		
		this.materials.removeDuplicates();
	}
	
	public MaterialList put(Material material)
	{
		materials.add(material);
		materials.removeDuplicates();
		return this;
	}
	
	public GList<String> getStrings()
	{
		return materials.stringList();
	}
	
	public GList<Material> getMaterials()
	{
		return materials;
	}
	
	public void setMaterials(GList<Material> materials)
	{
		this.materials = materials;
	}
	
}
