package org.cyberpwn.react.controller;

import org.cyberpwn.react.React;
import org.cyberpwn.react.action.ActionCollectGarbage;
import org.cyberpwn.react.action.ActionCullDrops;
import org.cyberpwn.react.action.ActionCullEntities;
import org.cyberpwn.react.action.ActionDullEntities;
import org.cyberpwn.react.action.ActionHeavyChunk;
import org.cyberpwn.react.action.ActionInstabilityCause;
import org.cyberpwn.react.action.ActionPurgeChunks;
import org.cyberpwn.react.action.ActionPurgeDrops;
import org.cyberpwn.react.action.ActionPurgeEntities;
import org.cyberpwn.react.action.ActionReStackEntities;
import org.cyberpwn.react.action.ActionStackEntities;
import org.cyberpwn.react.action.ActionSuppressRedstone;
import org.cyberpwn.react.action.ActionSuppressTnt;
import org.cyberpwn.react.action.ActionUnStackEntities;
import org.cyberpwn.react.action.Actionable;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;

public class ActionController extends Controller
{
	private GMap<Actionable, Integer> actions;
	
	public static boolean enabled = true;
	private final ActionInstabilityCause actionInstabilityCause;
	private final ActionCullEntities actionCullEntities;
	private final ActionSuppressRedstone actionSuppressRedstone;
	private final ActionSuppressTnt actionSuppressTnt;
	private final ActionCollectGarbage actionCollectGarbage;
	private final ActionPurgeChunks actionPurgeChunks;
	private final ActionCullDrops actionCullDrops;
	private final ActionPurgeDrops actionPurgeDrops;
	private final ActionPurgeEntities actionPurgeEntities;
	private final ActionHeavyChunk actionHeavyChunk;
	private final ActionStackEntities actionStackEntities;
	private final ActionUnStackEntities actionUnStackEntities;
	private final ActionReStackEntities actionReStackEntities;
	private final ActionDullEntities actionDullEntities;
	
	public ActionController(React react)
	{
		super(react);
		
		actions = new GMap<Actionable, Integer>();
		
		actionInstabilityCause = new ActionInstabilityCause(this);
		actionCullEntities = new ActionCullEntities(this);
		actionSuppressRedstone = new ActionSuppressRedstone(this);
		actionSuppressTnt = new ActionSuppressTnt(this);
		actionCollectGarbage = new ActionCollectGarbage(this);
		actionPurgeChunks = new ActionPurgeChunks(this);
		actionCullDrops = new ActionCullDrops(this);
		actionPurgeEntities = new ActionPurgeEntities(this);
		actionPurgeDrops = new ActionPurgeDrops(this);
		actionHeavyChunk = new ActionHeavyChunk(this);
		actionStackEntities = new ActionStackEntities(this);
		actionUnStackEntities = new ActionUnStackEntities(this);
		actionReStackEntities = new ActionReStackEntities(this);
		actionDullEntities = new ActionDullEntities(this);
	}
	
	public void load()
	{
		for(Actionable i : actions.keySet())
		{
			if(i instanceof Configurable)
			{
				Configurable c = (Configurable) i;
				react.getDataController().load("reactions", c);
				o("Loaded Configuration for " + c.getCodeName());
			}
		}
	}
	
	@Override
	public void start()
	{
		load();
		
		for(Actionable i : new GList<Actionable>(actions.keySet()))
		{
			if(i.isEnabled())
			{
				i.start();
			}
		}
	}
	
	@Override
	public void tick()
	{
		for(Actionable i : new GList<Actionable>(actions.keySet()))
		{
			actions.put(i, actions.get(i) + 1);
			
			if(actions.get(i) >= i.getIdealTick())
			{
				actions.put(i, 0);
				
				if(i.isEnabled() && ActionController.enabled)
				{
					long ns = System.nanoTime();
					
					try
					{
						i.preAct();
					}
					
					catch(Exception e)
					{
						
					}
					
					i.setReactionTime(System.nanoTime() - ns);
				}
			}
		}
	}
	
	@Override
	public void stop()
	{
		for(Actionable i : new GList<Actionable>(actions.keySet()))
		{
			if(i.isEnabled())
			{
				i.stop();
			}
		}
	}
	
	public void registerAction(Actionable actionable)
	{
		actions.put(actionable, 0);
	}
	
	public GMap<Actionable, Integer> getActions()
	{
		return actions;
	}
	
	public void setActions(GMap<Actionable, Integer> actions)
	{
		this.actions = actions;
	}
	
	public ActionInstabilityCause getActionInstabilityCause()
	{
		return actionInstabilityCause;
	}
	
	public ActionCullEntities getActionCullEntities()
	{
		return actionCullEntities;
	}
	
	public ActionSuppressRedstone getActionSuppressRedstone()
	{
		return actionSuppressRedstone;
	}
	
	public ActionSuppressTnt getActionSuppressTnt()
	{
		return actionSuppressTnt;
	}
	
	public ActionCollectGarbage getActionCollectGarbage()
	{
		return actionCollectGarbage;
	}
	
	public ActionPurgeChunks getActionPurgeChunks()
	{
		return actionPurgeChunks;
	}
	
	public ActionCullDrops getActionCullDrops()
	{
		return actionCullDrops;
	}
	
	public ActionPurgeDrops getActionPurgeDrops()
	{
		return actionPurgeDrops;
	}
	
	public ActionPurgeEntities getActionPurgeEntities()
	{
		return actionPurgeEntities;
	}
	
	public ActionHeavyChunk getActionHeavyChunk()
	{
		return actionHeavyChunk;
	}
	
	public static boolean isEnabled()
	{
		return enabled;
	}
	
	public ActionStackEntities getActionStackEntities()
	{
		return actionStackEntities;
	}
	
	public static void setEnabled(boolean enabled)
	{
		ActionController.enabled = enabled;
	}
	
	public ActionUnStackEntities getActionUnStackEntities()
	{
		return actionUnStackEntities;
	}
	
	public ActionReStackEntities getActionReStackEntities()
	{
		return actionReStackEntities;
	}
	
	public ActionDullEntities getActionDullEntities()
	{
		return actionDullEntities;
	}
}
