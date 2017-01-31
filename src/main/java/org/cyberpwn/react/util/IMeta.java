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
	
	public ItemMeta toItemMeta(ItemMeta im)
	{
		im.setDisplayName(display);
		im.setLore(lore);
		
		for(int j : enchant.keySet())
		{
			@SuppressWarnings("deprecation")
			Enchantment e = Enchantment.getById(j);
			im.addEnchant(e, enchant.get(j), true);
		}
		
		return im;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((display == null) ? 0 : display.hashCode());
		result = prime * result + ((enchant == null) ? 0 : enchant.hashCode());
		result = prime * result + ((lore == null) ? 0 : lore.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		if(obj == null)
		{
			return false;
		}
		if(getClass() != obj.getClass())
		{
			return false;
		}
		IMeta other = (IMeta) obj;
		if(display == null)
		{
			if(other.display != null)
			{
				return false;
			}
		}
		else if(!display.equals(other.display))
		{
			return false;
		}
		if(enchant == null)
		{
			if(other.enchant != null)
			{
				return false;
			}
		}
		else if(!enchant.equals(other.enchant))
		{
			return false;
		}
		if(lore == null)
		{
			if(other.lore != null)
			{
				return false;
			}
		}
		else if(!lore.equals(other.lore))
		{
			return false;
		}
		return true;
	}
	
}
