package org.cyberpwn.react.timings;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberpwn.react.React;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Reflection;
import com.avaje.ebeaninternal.api.ClassUtil;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import co.aikar.timings.TimingHistory;
import co.aikar.timings.Timings;
import co.aikar.timings.TimingsManager;

public class PaperTimings
{
	private static final String TIMINGS_PACKAGE = "co.aikar.timings";
	
	private static final String EXPORT_CLASS = TIMINGS_PACKAGE + '.' + "TimingsExport";
	private static final String HANDLER_CLASS = TIMINGS_PACKAGE + '.' + "TimingHandler";
	private static final String HISTORY_ENTRY_CLASS = TIMINGS_PACKAGE + '.' + "TimingHistoryEntry";
	private static final String DATA_CLASS = TIMINGS_PACKAGE + '.' + "TimingData";
	
	private final React plugin;
	private int historyIntervall;
	
	public PaperTimings(React plugin)
	{
		this.plugin = plugin;
		
		try
		{
			historyIntervall = Reflection.getField("com.destroystokyo.paper.PaperConfig", "config", YamlConfiguration.class).get(null).getInt("timings.history-interval");
		}
		
		catch(IllegalArgumentException illegalArgumentException)
		{
			historyIntervall = 10;
		}
	}
	
	@SuppressWarnings("unchecked")
	public GList<String> getTimings(int threads)
	{
		GList<String> gdt = new GList<String>();
		
		if(!ClassUtil.isPresent(EXPORT_CLASS))
		{
			return gdt;
		}
		
		if(!Timings.isTimingsEnabled())
		{
			return gdt;
		}
		
		EvictingQueue<TimingHistory> history = Reflection.getField(TimingsManager.class, "HISTORY", EvictingQueue.class).get(null);
		
		TimingHistory lastHistory = history.peek();
		if(lastHistory == null)
		{
			return gdt;
		}
		
		List<String> lines = Lists.newArrayList();
		printTimings(lines, lastHistory, threads);
		
		for(String i : lines)
		{
			gdt.add(i);
		}
		
		return gdt;
	}
	
	public void printTimings(List<String> lines, TimingHistory lastHistory, int threads)
	{
		printHeadData(lastHistory, lines);
		
		Map<Integer, String> idHandler = Maps.newHashMap();
		
		Map<?, ?> groups = Reflection.getField(TIMINGS_PACKAGE + ".TimingIdentifier", "GROUP_MAP", Map.class).get(null);
		
		for(Object group : groups.values())
		{
			String groupName = Reflection.getField(group.getClass(), "name", String.class).get(group);
			ArrayDeque<?> handlers = Reflection.getField(group.getClass(), "handlers", ArrayDeque.class).get(group);
			
			for(Object handler : handlers)
			{
				int id = Reflection.getField(HANDLER_CLASS, "id", Integer.TYPE).get(handler);
				String name = Reflection.getField(HANDLER_CLASS, "name", String.class).get(handler);
				if(name.contains("Combined"))
				{
					idHandler.put(id, "Combined " + groupName);
				}
				else
				{
					idHandler.put(id, name);
				}
			}
		}
		
		Object[] entries = Reflection.getField(TimingHistory.class, "entries", Object[].class).get(lastHistory);
		
		for(Object entry : entries)
		{
			Object parentData = Reflection.getField(HISTORY_ENTRY_CLASS, "data", Object.class).get(entry);
			int childId = Reflection.getField(DATA_CLASS, "id", Integer.TYPE).get(parentData);
			
			String handlerName = idHandler.get(childId);
			String parentName;
			if(handlerName == null)
			{
				parentName = "Unknown-" + childId;
			}
			else
			{
				parentName = handlerName;
			}
			
			int parentCount = Reflection.getField(DATA_CLASS, "count", Integer.TYPE).get(parentData);
			long parentTime = Reflection.getField(DATA_CLASS, "totalTime", Long.TYPE).get(parentData);
			
			lines.add(parentName + " Count: " + parentCount + " Time: " + parentTime);
			
			Object[] children = Reflection.getField(HISTORY_ENTRY_CLASS, "children", Object[].class).get(entry);
			for(Object childData : children)
			{
				printChilds(parentData, childData, idHandler, lines);
			}
		}
	}
	
	private void printChilds(Object parent, Object childData, Map<Integer, String> idMap, List<String> lines)
	{
		int childId = Reflection.getField(DATA_CLASS, "id", Integer.TYPE).get(childData);
		
		String handlerName = idMap.get(childId);
		String childName;
		if(handlerName == null)
		{
			childName = "Unknown-" + childId;
		}
		else
		{
			childName = handlerName;
		}
		
		int childCount = Reflection.getField(DATA_CLASS, "count", Integer.TYPE).get(childData);
		long childTime = Reflection.getField(DATA_CLASS, "totalTime", Long.TYPE).get(childData);
		long parentTime = Reflection.getField(DATA_CLASS, "totalTime", Long.TYPE).get(parent);
		float percent = (float) childTime / parentTime;
		
		lines.add("    " + childName + " Count: " + childCount + " Time: " + childTime + ' ' + round(percent) + '%');
	}
	
	private void printHeadData(TimingHistory lastHistory, List<String> lines)
	{
		long totalTime = Reflection.getField(TimingHistory.class, "totalTime", Long.TYPE).get(lastHistory);
		long totalTicks = Reflection.getField(TimingHistory.class, "totalTicks", Long.TYPE).get(lastHistory);
		long cost = (long) Reflection.getMethod(EXPORT_CLASS, "getCost").invoke(null);
		float totalSeconds = (float) totalTime / 1000 / 1000;
		long playerTicks = TimingHistory.playerTicks;
		long activatedEntityTicks = TimingHistory.activatedEntityTicks;
		long entityTicks = TimingHistory.entityTicks;
		float activatedAvgEntities = (float) activatedEntityTicks / totalTicks;
		float totalAvgEntities = (float) entityTicks / totalTicks;
		float averagePlayers = (float) playerTicks / totalTicks;
		float desiredTicks = 20 * historyIntervall;
		float averageTicks = totalTicks / desiredTicks * 20;
		
		lines.add("Cost: " + Long.toString(cost));
		lines.add("Total (sec): " + round(totalSeconds));
		lines.add("Ticks: " + round(totalTicks));
		lines.add("Avg ticks: " + round(averageTicks));
		lines.add("AVG Players: " + round(averagePlayers));
		lines.add("Activated Entities: " + round(activatedAvgEntities) + " / " + round(totalAvgEntities));
	}
	
	private float round(float number)
	{
		return (float) (Math.round(number * 100.0) / 100.0);
	}
	
	public React getPlugin()
	{
		return plugin;
	}
}