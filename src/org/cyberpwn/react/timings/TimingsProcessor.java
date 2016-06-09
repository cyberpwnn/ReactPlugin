package org.cyberpwn.react.timings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.object.GBook;
import org.cyberpwn.react.object.GList;
import org.cyberpwn.react.object.GMap;
import org.cyberpwn.react.object.GPage;
import org.cyberpwn.react.object.Severity;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.Timer;
import org.spigotmc.CustomTimingsHandler;

public class TimingsProcessor extends Thread
{
	private ClusterConfig cc;
	private GList<String> data;
	private GMap<String, TimingsObject> events;
	private GMap<String, TimingsObject> tasks;
	private GMap<String, TimingsObject> normal;
	private TimingsCallback cb;
	private File df;
	private String hh;
	private Double ms;
	
	public TimingsProcessor(File df, TimingsCallback cb)
	{
		this.df = df;
		this.cb = cb;
		cc = new ClusterConfig();
		data = new GList<String>();
		events = new GMap<String, TimingsObject>();
		tasks = new GMap<String, TimingsObject>();
		normal = new GMap<String, TimingsObject>();
	}
	
	public void run()
	{
		poll();
	}
	
	private void poll()
	{
		try
		{
			File fcx = new File(new File(df, "cache"), "timings.yml");
			Timer t = new Timer();
			t.start();
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(bs);
			CustomTimingsHandler.printTimings(ps);
			
			data.clear();
			
			for(String i : StringUtils.split(bs.toString().trim(), "\n"))
			{
				data.add(i.trim());
			}
			
			for(String i : data)
			{
				parseTCAV(i);
			}
			
			for(String i : normal.keySet())
			{
				cc.set("normal." + StringUtils.remove(i, '.') + ".time", normal.get(i).getTime() / 1000000);
				cc.set("normal." + StringUtils.remove(i, '.') + ".count", normal.get(i).getCount());
				cc.set("normal." + StringUtils.remove(i, '.') + ".avg", normal.get(i).getAvg() / 1000000);
				cc.set("normal." + StringUtils.remove(i, '.') + ".violations", normal.get(i).getViolations());
				cc.set("normal." + StringUtils.remove(i, '.') + ".ms", (double) normal.get(i).getTime() / 1000000.0 / (double) normal.get(i).getCount());
			}
			
			for(String i : events.keySet())
			{
				cc.set("events." + StringUtils.remove(i, '.') + ".time", events.get(i).getTime() / 1000000);
				cc.set("events." + StringUtils.remove(i, '.') + ".count", events.get(i).getCount());
				cc.set("events." + StringUtils.remove(i, '.') + ".avg", events.get(i).getAvg() / 1000000);
				cc.set("events." + StringUtils.remove(i, '.') + ".violations", events.get(i).getViolations());
				cc.set("events." + StringUtils.remove(i, '.') + ".ms", (double) events.get(i).getTime() / 1000000.0 / (double) events.get(i).getCount());
			}
			
			for(String i : tasks.keySet())
			{
				cc.set("tasks." + StringUtils.remove(i, '.') + ".time", tasks.get(i).getTime() / 1000000);
				cc.set("tasks." + StringUtils.remove(i, '.') + ".count", tasks.get(i).getCount());
				cc.set("tasks." + StringUtils.remove(i, '.') + ".avg", tasks.get(i).getAvg() / 1000000);
				cc.set("tasks." + StringUtils.remove(i, '.') + ".violations", tasks.get(i).getViolations());
				cc.set("tasks." + StringUtils.remove(i, '.') + ".ms", (double) tasks.get(i).getTime() / 1000000.0 / (double) tasks.get(i).getCount());
			}
			
			GMap<String, TimingsReport> reports = analyze(normal, tasks, events);
			FileConfiguration fc = cc.toYaml();
			fc.save(fcx);
			GBook k = getAllProc(reports);
			t.stop();
			cb.run(reports, k, hh, ms, cc);
		}
		
		catch(Exception e)
		{
			
		}
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
		
		else
		{
			ms = db.get(0);
			
			for(String i : reports.keySet())
			{
				for(String j : reports.get(i).getData().keySet())
				{
					TimingsObject o = reports.get(i).getData().get(j);
					
					if(o.getMs() == ms)
					{
						hh = j;
					}
				}
			}
		}
		
		return book;
	}
	
	private GMap<String, TimingsReport> analyze(GMap<String, TimingsObject> normal, GMap<String, TimingsObject> tasks, GMap<String, TimingsObject> events)
	{
		GMap<String, TimingsReport> reports = new GMap<String, TimingsReport>();
		reports.put("Server", analyze(normal));
		reports.put("Plugin Task", analyze(tasks));
		reports.put("Plugin Event", analyze(events));
		return reports;
	}
	
	private TimingsReport analyze(GMap<String, TimingsObject> data)
	{
		TimingsReport report = new TimingsReport();
		
		for(String i : data.keySet())
		{
			report.put(i, data.get(i));
			double ms = data.get(i).getMs();
			
			if(ms > 5)
			{
				if(ms > 50)
				{
					report.put(i, Severity.SERIOUS);
				}
				
				else if(ms > 30)
				{
					report.put(i, Severity.PROBLEMATIC);
				}
				
				else if(ms > 20)
				{
					report.put(i, Severity.NOTABLE);
				}
				
				else
				{
					report.put(i, Severity.POSSIBLE);
				}
			}
		}
		
		return report;
	}
	
	private String getKey(String line)
	{
		String key = "";
		int ind = 0;
		
		for(char i : line.toCharArray())
		{
			if(line.substring(ind, ind + 6).equals("Time: "))
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
	
	private String getPluginName(String line)
	{
		String key = "";
		int ind = 0;
		
		for(char i : line.toCharArray())
		{
			if(line.substring(ind, ind + 10).equals("Runnable: "))
			{
				break;
			}
			
			if(line.substring(ind, ind + 7).equals("Event: "))
			{
				break;
			}
			
			if(i == '*')
			{
				continue;
			}
			
			else
			{
				key = key + i;
			}
			
			ind++;
		}
		
		key = key.substring(8);
		key = StringUtils.remove(key, "Event:");
		
		return key.substring(0, key.length() - 1);
	}
	
	private String getPluginNameTask(String line)
	{
		String key = "";
		int ind = 0;
		
		for(char i : line.toCharArray())
		{
			if(line.substring(ind, ind + 10).equals("Runnable: "))
			{
				break;
			}
			
			if(line.substring(ind, ind + 7).equals("Event: "))
			{
				break;
			}
			
			if(i == '*')
			{
				continue;
			}
			
			else
			{
				key = key + i;
			}
			
			ind++;
		}
		
		key = key.substring(6);
		key = StringUtils.remove(key, "Event:");
		
		return key.substring(0, key.length() - 1);
	}
	
	private void handleEvent(String line)
	{
		String key = getPluginName(line);
		Long time = getValue(line, " Time: ");
		Long count = getValue(line, " Count: ");
		Long avg = getValue(line, " Avg: ");
		Long violations = getValue(line, " Violations: ");
		
		if(time == null)
		{
			time = 0l;
		}
		
		if(count == null)
		{
			count = 0l;
		}
		
		if(avg == null)
		{
			avg = 0l;
		}
		
		if(violations == null)
		{
			violations = 0l;
		}
		
		if(events.containsKey(key))
		{
			events.get(key).add(time, count, avg, violations);
		}
		
		else
		{
			events.put(key, new TimingsObject(time, count, avg, violations));
		}
	}
	
	private void handleTask(String line)
	{
		String key = getPluginNameTask(line);
		Long time = getValue(line, " Time: ");
		Long count = getValue(line, " Count: ");
		Long avg = getValue(line, " Avg: ");
		Long violations = getValue(line, " Violations: ");
		
		if(time == null)
		{
			time = 0l;
		}
		
		if(count == null)
		{
			count = 0l;
		}
		
		if(avg == null)
		{
			avg = 0l;
		}
		
		if(violations == null)
		{
			violations = 0l;
		}
		
		if(tasks.containsKey(key))
		{
			tasks.get(key).add(time, count, avg, violations);
		}
		
		else
		{
			tasks.put(key, new TimingsObject(time, count, avg, violations));
		}
	}
	
	private void parseTCAV(String line)
	{
		if(!line.contains("Time: "))
		{
			return;
		}
		
		try
		{
			String key = getKey(line);
			
			if(key.equalsIgnoreCase("plugin"))
			{
				handleEvent(line);
				return;
			}
			
			if(key.equalsIgnoreCase("task"))
			{
				handleTask(line);
				return;
			}
			
			Long time = getValue(line, " Time: ");
			Long count = getValue(line, " Count: ");
			Long avg = getValue(line, " Avg: ");
			Long violations = getValue(line, " Violations: ");
			
			if(time == null)
			{
				time = 0l;
			}
			
			if(count == null)
			{
				count = 0l;
			}
			
			if(avg == null)
			{
				avg = 0l;
			}
			
			if(violations == null)
			{
				violations = 0l;
			}
			
			normal.put(key, new TimingsObject(time, count, avg, violations));
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
