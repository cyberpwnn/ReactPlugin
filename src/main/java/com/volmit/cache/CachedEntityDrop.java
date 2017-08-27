package com.volmit.cache;

import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public interface CachedEntityDrop extends CachedEntity
{
	public Material getDropMaterial();
	
	public byte getDropData();
	
	public int getDropAmount();
	
	public short getDropDurability();
	
	public Map<Enchantment, Integer> getDropEnchantments();
	
	public String getCustomMetaName();
	
	public List<String> getCustomLore();
	
	public boolean hasCustomMeta();
	
	public ItemStack getItemStack();
}
