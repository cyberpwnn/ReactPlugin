package org.cyberpwn.react.timings;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GBook;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.GPage;
import org.cyberpwn.react.util.Severity;

public class PaperTimingsProcessor extends Thread
{
	private GList<String> timings;
	private GList<String> plugins;
	private GMap<String, TimingsReport> reports;
	private PaperTimingsCallback tc;
	
	public PaperTimingsProcessor(GList<String> timings, GList<String> plugins, PaperTimingsCallback tc)
	{
		this.timings = timings;
		this.plugins = plugins;
		this.tc = tc;
		this.reports = new GMap<String, TimingsReport>();
	}
	
	public void run()
	{
		scanPlugins();
		scanRest();
		
		GBook k = k();
		String hh = hh();
		
		if(reports.containsKey("Server") && reports.get("Server").getData().containsKey("FullServerTick"))
		{
			tc.run(reports, k, hh, reports.get("Server").getData().get("FullServerTick").getMs());
		}
	}
	
	public GBook k()
	{
		return getAllProc(reports);
	}
	
	public GBook getAllProblems(GMap<String, TimingsReport> reports)
	{
		GBook book = new GBook("Timings: Problems");
		GMap<GPage, Double> order = new GMap<GPage, Double>();
		
		for(String i : reports.keySet())
		{
			String type = i;
			
			for(String j : reports.get(i).getProblems().keySet())
			{
				TimingsObject o = reports.get(i).getData().get(j);
				
				String name = j;
				String severity = StringUtils.capitalize(reports.get(i).getProblems().get(j).toString().toLowerCase());
				String ms = F.f(o.getMs(), 3) + "ms";
				String hits = F.f(o.getCount());
				String via = F.f(o.getViolations());
				String pcot = F.pc(o.getMs() / 50.0, 0) + " of tick";
				String pa = "";
				
				pa = pa + ChatColor.BLUE + "Type: " + ChatColor.DARK_RED + type + "\n";
				pa = pa + ChatColor.RED + "This is a " + severity + " issue." + "\n\n";
				pa = pa + ChatColor.BLUE + "Avg Timing: " + ChatColor.DARK_RED + ms + ChatColor.GOLD + "(" + pcot + ")" + "\n";
				pa = pa + ChatColor.BLUE + "Hits: " + ChatColor.DARK_RED + hits + "\n";
				pa = pa + ChatColor.BLUE + "Violations: " + ChatColor.DARK_RED + via + "\n";
				
				GPage gp = new GPage();
				gp.put(name, pa);
				order.put(gp, o.getMs());
			}
		}
		
		List<Double> db = order.v();
		Collections.sort(db);
		Collections.reverse(db);
		
		for(Double i : db)
		{
			for(GPage j : order.k())
			{
				if(order.get(j) == i)
				{
					book.addPage(j);
					order.remove(j);
					break;
				}
			}
		}
		
		return book;
	}
	
	private GBook getAllProc(GMap<String, TimingsReport> reports)
	{
		GBook book = new GBook("Timings: Everything");
		GMap<GPage, Double> order = new GMap<GPage, Double>();
		
		for(String i : reports.keySet())
		{
			String type = i;
			
			for(String j : reports.get(i).getData().keySet())
			{
				TimingsObject o = reports.get(i).getData().get(j);
				
				String name = j;
				String ms = F.f(o.getMs(), 3) + "ms";
				String hits = F.f(o.getCount());
				String via = F.f(o.getViolations());
				String pcot = F.pc(o.getMs() / 50.0, 0) + ChatColor.DARK_RED + " of tick";
				String severity = "This does not appear cause issues.";
				
				if(reports.get(i).getProblems().containsKey(j))
				{
					severity = ChatColor.RED + "" + ChatColor.UNDERLINE + "" + StringUtils.capitalize(reports.get(i).getProblems().get(j).toString().toLowerCase()) + ChatColor.RESET + ChatColor.RED + " issue.";
				}
				
				String pa = "";
				
				pa = pa + ChatColor.BLUE + "Type: " + ChatColor.DARK_RED + type + "\n";
				pa = pa + ChatColor.DARK_GREEN + severity + "\n\n";
				pa = pa + ChatColor.BLUE + "Avg Timing: " + ChatColor.DARK_RED + ms + "\n";
				pa = pa + ChatColor.BLUE + pcot + "\n";
				pa = pa + ChatColor.BLUE + "Hits: " + ChatColor.DARK_RED + hits + "\n";
				pa = pa + ChatColor.BLUE + "Violations: " + ChatColor.DARK_RED + via + "\n";
				
				GPage gp = new GPage();
				gp.put(name, pa);
				order.put(gp, o.getMs());
			}
		}
		
		List<Double> db = order.v();
		Collections.sort(db);
		Collections.reverse(db);
		
		for(Double i : db)
		{
			for(GPage j : order.k())
			{
				if(order.get(j) == i)
				{
					book.addPage(j);
					order.remove(j);
					break;
				}
			}
		}
		
		if(db.isEmpty())
		{
			book.addPage(new GPage().put("Turn on Timings", "To use this feature, you need to turn on timings. Use the command: \n\n/timings on\n\nYou can also reset them to clear up some data with \n\n/timings reset"));
		}
		
		return book;
	}
	
	public String hh()
	{
		String nam = "No Data";
		double lim = -1;
		
		for(String i : reports.k())
		{
			for(String j : reports.get(i).getData().k())
			{
				if(reports.get(i).getData().get(j).getMs() > lim)
				{
					lim = reports.get(i).getData().get(j).getMs();
					nam = j + " " + F.f(reports.get(i).getData().get(j).getMs(), 2) + "ms";
				}
			}
		}
		
		return nam;
	}
	
	public void scanRest()
	{
		TimingsReport tr = new TimingsReport();
		
		for(String i : timings)
		{
			String key = getKey(i);
			
			if(key != null && !key.equals(""))
			{
				if(key.endsWith("C"))
				{
					key = key.substring(0, key.length() - 1);
				}
				
				else if(key.endsWith("Co"))
				{
					key = key.substring(0, key.length() - 2);
				}
				
				else if(key.endsWith("Cou"))
				{
					key = key.substring(0, key.length() - 3);
				}
				
				else if(key.endsWith("Coun"))
				{
					key = key.substring(0, key.length() - 4);
				}
				
				else if(key.endsWith("Count"))
				{
					key = key.substring(0, key.length() - 5);
				}
				
				Long time = getValue(i, " Time: ");
				Long count = getValue(i, " Count: ");
				
				if(time != null)
				{
					TimingsObject to = new TimingsObject(time, count, time, 0l);
					tr.put(key, to);
					
					double ms = to.getMs();
					
					if(ms > 5)
					{
						if(ms > 50)
						{
							tr.put(key, Severity.SERIOUS);
						}
						
						else if(ms > 30)
						{
							tr.put(key, Severity.PROBLEMATIC);
						}
						
						else if(ms > 20)
						{
							tr.put(key, Severity.NOTABLE);
						}
						
						else
						{
							tr.put(key, Severity.POSSIBLE);
						}
					}
				}
			}
		}
		
		reports.put("Server", tr);
	}
	
	public void scanPlugins()
	{
		TimingsReport tr = new TimingsReport();
		
		for(String i : timings)
		{
			for(String p : plugins)
			{
				if(i.contains("Combined " + p) && i.startsWith("C"))
				{
					Long time = getValue(i, " Time: ");
					Long count = getValue(i, " Count: ");
					
					TimingsObject to = new TimingsObject(time, count, time, 0l);
					
					tr.put(p, to);
					double ms = to.getMs();
					
					if(ms > 5)
					{
						if(ms > 50)
						{
							tr.put(p, Severity.SERIOUS);
						}
						
						else if(ms > 30)
						{
							tr.put(p, Severity.PROBLEMATIC);
						}
						
						else if(ms > 20)
						{
							tr.put(p, Severity.NOTABLE);
						}
						
						else
						{
							tr.put(p, Severity.POSSIBLE);
						}
					}
					
					break;
				}
			}
		}
		
		reports.put("Plugin", tr);
	}
	
	private Long getValue(String line, String key)
	{
		int ind = 0;
		
		if(!line.contains(key))
		{
			return null;
		}
		
		for(char i : line.toCharArray())
		{
			boolean b = false;
			if(i == key.charAt(0))
			{
				
			}
			
			if(line.substring(ind, ind + key.length()).equals(key))
			{
				b = true;
				String v = "";
				
				try
				{
					for(char j : line.substring(ind + key.length()).toCharArray())
					{
						if(j == ' ')
						{
							break;
						}
						
						if(Character.isDigit(j))
						{
							v = v + j;
						}
					}
					
					return Long.valueOf(v);
					
				}
				
				catch(Exception e)
				{
					return -1l;
				}
			}
			
			if(b)
			{
				break;
			}
			
			ind++;
		}
		
		return -1l;
	}
	
	private String getKey(String line)
	{
		String key = "";
		int ind = 0;
		
		for(char i : line.toCharArray())
		{
			if(line.length() <= ind + 7)
			{
				return "";
			}
			
			if(line.substring(ind, ind + 7).equals("Count: "))
			{
				break;
			}
			
			if(i == ':')
			{
				break;
			}
			
			if(i == '*')
			{
				continue;
			}
			
			if(i == ' ')
			{
				continue;
			}
			
			else
			{
				key = key + i;
			}
			
			ind++;
		}
		
		return key;
	}
}
