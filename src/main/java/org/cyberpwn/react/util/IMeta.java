package org.cyberpwn.react.util;

import java.util.List;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class IMeta
{
	private String display;
	private List<String> lore;
	private Map<Integer, Integer> enchant;
	
	@SuppressWarnings("deprecation")
	public IMeta(ItemMeta i)
	{
		display = i.getDisplayName();
		lore = i.getLore();
		enchant = new GMap<Integer, Integer>();
		
		for(Enchantment j : i.getEnchants().keySet())
		{
			enchant.put(j.getId(), i.getEnchants().get(j));
		}
	}
	
	public String getDisplay()
	{
		return display;
	}
	
	public void setDisplay(String display)
	{
		this.display = display;
	}
	
	public List<String> getLore()
	{
		return lore;
	}
	
	public void setLore(List<String> lore)
	{
		this.lore = lore;
	}
	
	public Map<Integer, Integer> getEnchant()
	{
		return enchant;
	}
	
	public void setEnchant(Map<Integer, Integer> enchant)
	{
		this.enchant = enchant;
	}
}
