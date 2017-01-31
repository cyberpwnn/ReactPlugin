package org.cyberpwn.react.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
	
	@SuppressWarnings("deprecation")
	public ItemStack toItemStack()
	{
		ItemStack is = new ItemStack(Material.getMaterial(material), amount, durability, data);
		ItemMeta imx = im.toItemMeta(is.getItemMeta());
		is.setItemMeta(imx);
		
		return is;
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
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		result = prime * result + data;
		result = prime * result + durability;
		result = prime * result + ((im == null) ? 0 : im.hashCode());
		result = prime * result + material;
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
		IStack other = (IStack) obj;
		if(amount != other.amount)
		{
			return false;
		}
		if(data != other.data)
		{
			return false;
		}
		if(durability != other.durability)
		{
			return false;
		}
		if(im == null)
		{
			if(other.im != null)
			{
				return false;
			}
		}
		else if(!im.equals(other.im))
		{
			return false;
		}
		if(material != other.material)
		{
			return false;
		}
		return true;
	}
	
}
