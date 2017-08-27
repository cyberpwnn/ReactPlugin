package com.volmit.cache;

import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.volmit.react.util.GList;
import com.volmit.react.util.GMap;

public class CEntityDrop extends CEntity implements CachedEntityDrop
{
	protected Material dropMaterial;
	protected byte dropData;
	protected int dropAmount;
	protected short dropDurability;
	protected Map<Enchantment, Integer> enchantments;
	protected List<String> customLore;
	protected String customNameMeta;
	protected boolean hasCustomMeta;
	protected boolean hasCustomMetaName;
	
	public CEntityDrop()
	{
		super();
		
		dropMaterial = Material.STONE;
		dropData = 0;
		dropAmount = 1;
		dropDurability = 0;
		enchantments = new GMap<Enchantment, Integer>();
		customLore = new GList<String>();
		customNameMeta = "";
		hasCustomMeta = false;
		hasCustomMetaName = false;
	}
	
	@Override
	public Material getDropMaterial()
	{
		return dropMaterial;
	}
	
	@Override
	public byte getDropData()
	{
		return dropData;
	}
	
	@Override
	public int getDropAmount()
	{
		return dropAmount;
	}
	
	@Override
	public short getDropDurability()
	{
		return dropDurability;
	}
	
	@Override
	public Map<Enchantment, Integer> getDropEnchantments()
	{
		return enchantments;
	}
	
	@Override
	public String getCustomMetaName()
	{
		return customNameMeta;
	}
	
	@Override
	public List<String> getCustomLore()
	{
		return customLore;
	}
	
	@Override
	public boolean hasCustomMeta()
	{
		return hasCustomMeta;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void read(Entity e)
	{
		super.read(e);
		
		ItemStack is = ((Item) e).getItemStack();
		dropMaterial = is.getType();
		dropData = is.getData().getData();
		dropAmount = is.getAmount();
		dropDurability = is.getDurability();
		enchantments = is.getEnchantments();
		
		if(is.hasItemMeta())
		{
			ItemMeta im = is.getItemMeta();
			hasCustomMeta = true;
			customLore = im.getLore();
			
			if(im.hasDisplayName())
			{
				hasCustomMetaName = true;
				customNameMeta = im.getDisplayName();
			}
		}
	}
	
	@Override
	public Entity restore()
	{
		Entity e = super.restore();
		((Item) e).setItemStack(getItemStack());
		
		return e;
	}
	
	@Override
	public ItemStack getItemStack()
	{
		@SuppressWarnings("deprecation")
		ItemStack is = new ItemStack(dropMaterial, dropAmount, dropDurability, dropData);
		
		is.addUnsafeEnchantments(enchantments);
		
		if(hasCustomMeta)
		{
			ItemMeta im = is.getItemMeta();
			
			if(hasCustomMetaName)
			{
				im.setDisplayName(customNameMeta);
			}
			
			im.setLore(customLore);
			is.setItemMeta(im);
		}
		
		return is;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((customLore == null) ? 0 : customLore.hashCode());
		result = prime * result + ((customNameMeta == null) ? 0 : customNameMeta.hashCode());
		result = prime * result + dropAmount;
		result = prime * result + dropData;
		result = prime * result + dropDurability;
		result = prime * result + ((dropMaterial == null) ? 0 : dropMaterial.hashCode());
		result = prime * result + ((enchantments == null) ? 0 : enchantments.hashCode());
		result = prime * result + (hasCustomMeta ? 1231 : 1237);
		result = prime * result + (hasCustomMetaName ? 1231 : 1237);
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		if(!super.equals(obj))
		{
			return false;
		}
		if(getClass() != obj.getClass())
		{
			return false;
		}
		CEntityDrop other = (CEntityDrop) obj;
		if(customLore == null)
		{
			if(other.customLore != null)
			{
				return false;
			}
		}
		else if(!customLore.equals(other.customLore))
		{
			return false;
		}
		if(customNameMeta == null)
		{
			if(other.customNameMeta != null)
			{
				return false;
			}
		}
		else if(!customNameMeta.equals(other.customNameMeta))
		{
			return false;
		}
		if(dropAmount != other.dropAmount)
		{
			return false;
		}
		if(dropData != other.dropData)
		{
			return false;
		}
		if(dropDurability != other.dropDurability)
		{
			return false;
		}
		if(dropMaterial != other.dropMaterial)
		{
			return false;
		}
		if(enchantments == null)
		{
			if(other.enchantments != null)
			{
				return false;
			}
		}
		else if(!enchantments.equals(other.enchantments))
		{
			return false;
		}
		if(hasCustomMeta != other.hasCustomMeta)
		{
			return false;
		}
		if(hasCustomMetaName != other.hasCustomMetaName)
		{
			return false;
		}
		return true;
	}
	
}
