package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.Value;
import org.cyberpwn.react.util.ValueType;

public class Sample implements Samplable, Configurable
{
	protected final SampleController sampleController;
	protected final ClusterConfig cc;
	protected String name;
	protected String cname;
	protected String description;
	protected String target;
	protected String explaination;
	protected ValueType type;
	protected Value value;
	protected Long lastTick;
	protected Integer maxDelay;
	protected Integer minDelay;
	protected Integer idealDelay;
	protected Integer currentDelay;
	protected Long reactionTime;
	protected String codeName;
	
	public Sample(SampleController sampleController, String cname, ValueType type, String name, String description)
	{
		this.sampleController = sampleController;
		this.name = name;
		this.cname = cname;
		this.codeName = F.cname(cname);
		this.type = type;
		this.value = new Value((int) 0);
		this.lastTick = System.currentTimeMillis();
		this.sampleController.registerSample(this);
		this.currentDelay = 1;
		this.target = "No Target";
		this.explaination = "No Explaination";
		this.cc = new ClusterConfig();
	}
	
	public Sample(SampleController sampleController, String cname, ValueType type, String name, String description, boolean register)
	{
		this.sampleController = sampleController;
		this.name = name;
		this.cname = cname;
		this.type = type;
		this.value = new Value((int) 0);
		this.lastTick = System.currentTimeMillis();
		
		if(register)
		{
			this.sampleController.registerSample(this);
		}
		
		this.currentDelay = 1;
		this.target = "No Target";
		this.explaination = "No Explaination";
		this.cc = new ClusterConfig();
	}
	
	public void onTick()
	{
		
	}
	
	public void onStart()
	{
		
	}
	
	public void onStop()
	{
		
	}
	
	public void onMetricsPlot(Graph graph)
	{
		graph.addPlotter(new Metrics.Plotter(getName())
		{
			@Override
			public int getValue()
			{
				return getMetricsValue();
			}
		});
	}
	
	public int getMetricsValue()
	{
		return getValue().getInteger();
	}
	
	public long timeSinceLastTick()
	{
		return System.currentTimeMillis() - lastTick;
	}
	
	public ValueType getType()
	{
		return type;
	}
	
	public Value get()
	{
		return value;
	}
	
	public Long getLastTick()
	{
		return lastTick;
	}
	
	public void setLastTick(Long lastTick)
	{
		this.lastTick = lastTick;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public Value getValue()
	{
		return value;
	}
	
	public void setValue(Value value)
	{
		this.value = value;
	}
	
	public Integer getMaxDelay()
	{
		return maxDelay;
	}
	
	public void setMaxDelay(Integer maxDelay)
	{
		this.maxDelay = maxDelay;
	}
	
	public Integer getMinDelay()
	{
		return minDelay;
	}
	
	public void setMinDelay(Integer minDelay)
	{
		this.minDelay = minDelay;
	}
	
	public Integer getIdealDelay()
	{
		return idealDelay;
	}
	
	public void setIdealDelay(Integer idealDelay)
	{
		this.idealDelay = idealDelay;
	}
	
	public SampleController getSampleController()
	{
		return sampleController;
	}
	
	public void setType(ValueType type)
	{
		this.type = type;
	}
	
	public Integer getCurrentDelay()
	{
		return currentDelay;
	}
	
	public void setCurrentDelay(Integer currentDelay)
	{
		this.currentDelay = currentDelay;
	}
	
	public String getTarget()
	{
		return target;
	}
	
	public void setTarget(String target)
	{
		this.target = target;
	}
	
	public String getExplaination()
	{
		return explaination;
	}
	
	public void setExplaination(String explaination)
	{
		this.explaination = explaination;
	}
	
	@Override
	public String getCodeName()
	{
		return codeName;
	}
	
	@Override
	public String formatted(boolean acc)
	{
		return "?";
	}
	
	@Override
	public ChatColor color()
	{
		return ChatColor.DARK_GRAY;
	}
	
	@Override
	public Long getReactionTime()
	{
		return reactionTime;
	}
	
	@Override
	public void setReactionTime(long ns)
	{
		reactionTime = ns;
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("component.interval", idealDelay, "Its typically not a good idea to change this\nunless you know what you are doing.");
	}
	
	@Override
	public void onReadConfig()
	{
		idealDelay = cc.getInt("component.interval");
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public boolean isProblematic()
	{
		return false;
	}
	
	@Override
	public String getProblem()
	{
		return "Problem";
	}

	@Override
	public void handleAction()
	{
		
	}
}
