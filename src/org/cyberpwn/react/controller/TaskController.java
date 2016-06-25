package org.cyberpwn.react.controller;

import org.cyberpwn.react.React;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.M;

public class TaskController extends Controller
{
	private GList<Runnable> tasks;
	
	public TaskController(React react)
	{
		super(react);
		
		tasks = new GList<Runnable>();
	}
	
	public void tick()
	{
		long ms = M.ms();
		
		while(!tasks.isEmpty() && M.ms() - ms < 3)
		{
			tasks.get(0).run();
		}
	}
	
	/**
	 * Run a task, even if you are async, it will be executed sync
	 * @param r runnable
	 */
	public void newTask(Runnable r)
	{
		tasks.add(r);
	}
}
