package org.cyberpwn.react.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.cyberpwn.react.lang.Info;

public class GBook
{
	private GList<GPage> pages;
	private String title;
	private String author;
	
	public GBook(String title)
	{
		pages = new GList<GPage>();
		this.title = title;
		this.author = Info.NAME;
	}
	
	public GBook addPage(GPage page)
	{
		pages.add(page);
		return this;
	}
	
	public GList<GPage> getPages()
	{
		return pages;
	}
	
	public void setPages(GList<GPage> pages)
	{
		this.pages = pages;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public void setAuthor(String author)
	{
		this.author = author;
	}
	
	public GList<String> toPages()
	{
		GList<String> pages = new GList<String>();
		
		for(GPage i : this.pages)
		{
			String p = "";
			
			for(String j : i.getElements().keySet())
			{
				p = p + ChatColor.DARK_AQUA + ChatColor.UNDERLINE + "" + ChatColor.BOLD + j + ChatColor.RESET + "\n\n" + i.getElements().get(j) + "\n\n" + ChatColor.RESET;
			}
			
			pages.add(p);
		}
		
		return pages;
	}
	
	public ItemStack toBook()
	{
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		bookMeta.setTitle(getTitle());
		bookMeta.setAuthor(getAuthor());
		bookMeta.setPages(toPages());
		book.setItemMeta(bookMeta);
		return book;
	}
	
	public String toString()
	{
		String s = "";
		int lim = 12;
		for(GPage i : pages)
		{
			for(String j : i.getElements().keySet())
			{
				s = s + Info.HR + "\n" + ChatColor.RESET;
				s = s + ChatColor.AQUA + j + "\n" + ChatColor.RESET;
				s = s + ChatColor.WHITE + i.getElements().get(j) + "\n" + ChatColor.RESET;
				lim--;
				if(lim <= 0)
				{
					break;
				}
			}
			
			if(lim <= 0)
			{
				break;
			}
		}
		
		s = s + Info.HR + ChatColor.RESET;
		
		return s;
	}
	
	public void filterTiming(String string)
	{
		for(GPage i : pages.copy())
		{
			boolean match = false;
			
			for(String j : i.getElements().keySet())
			{
				if(j.toLowerCase().contains(string.toLowerCase()))
				{
					match = true;
				}
			}
			
			if(!match)
			{
				pages.remove(i);
			}
		}
		
		if(pages.isEmpty())
		{
			pages.add(new GPage().put("Search Query", "React did not find any timings related to your search query '" + string + "'."));
		}
	}
}
