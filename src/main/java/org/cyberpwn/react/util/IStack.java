package org.cyberpwn.react.util;

import org.bukkit.inventory.ItemStack;

public class IStack
{
	private int material;
	private byte data;
	private short durability;
	private int amount;
	private IMeta im;
	
	@SuppressWarnings("deprecation")
	public IStack(ItemStack s)
	{
		material = s.getTypeId();
		data = s.getData().getData();
		durability = s.getDurability();
		amount = s.getAmount();
		
		try
		{
			im = new IMeta(s.getItemMeta());
		}
		
		catch(Exception e)
		{
			im = null;
		}
	}
	
	public int getMaterial()
	{
		return material;
	}
	
	public void setMaterial(int material)
	{
		this.material = material;
	}
	
	public byte getData()
	{
		return data;
	}
	
	public void setData(byte data)
	{
		this.data = data;
	}
	
	public short getDurability()
	{
		return durability;
	}
	
	public void setDurability(short durability)
	{
		this.durability = durability;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public void setAmount(int amount)
	{
		this.amount = amount;
	}
	
	public IMeta getIm()
	{
		return im;
	}
	
	public void setIm(IMeta im)
	{
		this.im = im;
	}
}
